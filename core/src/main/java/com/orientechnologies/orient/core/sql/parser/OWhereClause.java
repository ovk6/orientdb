/* Generated By:JJTree: Do not edit this line. OWhereClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.common.collection.OMultiCollectionIterator;
import com.orientechnologies.common.util.OSizeable;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.*;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.executor.OResult;

import java.util.*;
import java.util.stream.Collectors;

public class OWhereClause extends SimpleNode {
  protected OBooleanExpression baseExpression;

  protected List<OAndBlock> flattened;

  public OWhereClause(int id) {
    super(id);
  }

  public OWhereClause(OrientSql p, int id) {
    super(p, id);
  }

  /**
   * Accept the visitor. *
   */
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public boolean matchesFilters(OIdentifiable currentRecord, OCommandContext ctx) {
    if (baseExpression == null) {
      return true;
    }
    return baseExpression.evaluate(currentRecord, ctx);
  }

  public boolean matchesFilters(OResult currentRecord, OCommandContext ctx) {
    if (baseExpression == null) {
      return true;
    }
    return baseExpression.evaluate(currentRecord, ctx);
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    if (baseExpression == null) {
      return;
    }
    baseExpression.toString(params, builder);
  }

  /**
   * estimates how many items of this class will be returned applying this filter
   *
   * @param oClass
   *
   * @return an estimation of the number of records of this class returned applying this filter, 0 if and only if sure that no
   * records are returned
   */
  public long estimate(OClass oClass, long threshold, OCommandContext ctx) {
    long count = oClass.count();
    if (count > 1) {
      count = count / 2;
    }
    if (count < threshold) {
      return count;
    }

    long indexesCount = 0l;
    List<OAndBlock> flattenedConditions = flatten();
    Set<OIndex<?>> indexes = oClass.getIndexes();
    for (OAndBlock condition : flattenedConditions) {
      Map<String, Object> conditions = getEqualityOperations(condition, ctx);
      long conditionEstimation = Long.MAX_VALUE;
      for (OIndex index : indexes) {
        List<String> indexedFields = index.getDefinition().getFields();
        int nMatchingKeys = 0;
        for (String indexedField : indexedFields) {
          if (conditions.containsKey(indexedField)) {
            nMatchingKeys++;
          } else {
            break;
          }
        }
        if (nMatchingKeys > 0) {
          long newCount = estimateFromIndex(index, conditions, nMatchingKeys);
          if (newCount < conditionEstimation) {
            conditionEstimation = newCount;
          }
        }
      }
      if (conditionEstimation > count) {
        return count;
      }
      indexesCount += conditionEstimation;
    }
    return Math.min(indexesCount, count);
  }

  private long estimateFromIndex(OIndex index, Map<String, Object> conditions, int nMatchingKeys) {
    if (nMatchingKeys < 1) {
      throw new IllegalArgumentException("Cannot estimate from an index with zero keys");
    }
    OIndexDefinition definition = index.getDefinition();
    List<String> definitionFields = definition.getFields();
    Object key = null;
    if (definition instanceof OPropertyIndexDefinition) {
      key = convert(conditions.get(definitionFields.get(0)), definition.getTypes()[0]);
    } else if (definition instanceof OCompositeIndexDefinition) {
      key = new OCompositeKey();
      for (int i = 0; i < nMatchingKeys; i++) {
        Object keyValue = convert(conditions.get(definitionFields.get(i)), definition.getTypes()[i]);
        ((OCompositeKey) key).addKey(keyValue);
      }
    }
    if (key != null) {
      Object result = null;
      if (conditions.size() == definitionFields.size()) {
        result = index.get(key);
      } else if (index.supportsOrderedIterations()) {
        result = index.iterateEntriesBetween(key, true, key, true, true);
      }
      if (result instanceof OIdentifiable) {
        return 1;
      }
      if (result instanceof Collection) {
        return ((Collection) result).size();
      }
      if (result instanceof OSizeable) {
        return ((OSizeable) result).size();
      }
      if (result instanceof Iterable) {
        result = ((Iterable) result).iterator();
      }
      if (result instanceof Iterator) {
        int i = 0;
        while (((Iterator) result).hasNext()) {
          ((Iterator) result).next();
          i++;
        }
        return i;
      }
    }
    return Long.MAX_VALUE;
  }

  public Iterable fetchFromIndexes(OClass oClass, OCommandContext ctx) {

    List<OAndBlock> flattenedConditions = flatten();
    if (flattenedConditions == null || flattenedConditions.size() == 0) {
      return null;
    }
    Set<OIndex<?>> indexes = oClass.getIndexes();
    List<OIndex> bestIndexes = new ArrayList<OIndex>();
    List<Map<String, Object>> indexConditions = new ArrayList<Map<String, Object>>();
    for (OAndBlock condition : flattenedConditions) {
      Map<String, Object> conditions = getEqualityOperations(condition, ctx);
      long conditionEstimation = Long.MAX_VALUE;
      OIndex bestIndex = null;
      Map<String, Object> bestCondition = null;

      for (OIndex index : indexes) {
        List<String> indexedFields = index.getDefinition().getFields();
        int nMatchingKeys = 0;
        for (String indexedField : indexedFields) {
          if (conditions.containsKey(indexedField)) {
            nMatchingKeys++;
          } else {
            break;
          }
        }
        if (nMatchingKeys > 0) {
          long newCount = estimateFromIndex(index, conditions, nMatchingKeys);
          if (newCount >= 0 && newCount <= conditionEstimation) {
            conditionEstimation = newCount;
            bestIndex = index;
            bestCondition = conditions;
          }
        }
      }
      if (bestIndex == null) {
        return null;
      }
      bestIndexes.add(bestIndex);
      indexConditions.add(bestCondition);
    }
    OMultiCollectionIterator result = new OMultiCollectionIterator();

    for (int i = 0; i < bestIndexes.size(); i++) {
      OIndex index = bestIndexes.get(i);
      Map<String, Object> condition = indexConditions.get(i);
      result.add(fetchFromIndex(index, indexConditions.get(i)));
    }
    return result;
  }

  private Iterable fetchFromIndex(OIndex index, Map<String, Object> conditions) {
    OIndexDefinition definition = index.getDefinition();
    List<String> definitionFields = definition.getFields();
    Object key = null;
    if (definition instanceof OPropertyIndexDefinition) {
      key = convert(conditions.get(definitionFields.get(0)), definition.getTypes()[0]);
    } else if (definition instanceof OCompositeIndexDefinition) {
      key = new OCompositeKey();
      for (int i = 0; i < definitionFields.size(); i++) {
        String keyName = definitionFields.get(i);
        if (!conditions.containsKey(keyName)) {
          break;
        }
        Object keyValue = convert(conditions.get(keyName), definition.getTypes()[i]);
        ((OCompositeKey) key).addKey(conditions.get(keyName));
      }
    }
    if (key != null) {
      final Object result = index.get(key);
      if (result == null) {
        return Collections.EMPTY_LIST;
      }
      if (result instanceof Iterable) {
        return (Iterable) result;
      }
      if (result instanceof Iterator) {
        return new Iterable() {
          @Override public Iterator iterator() {
            return (Iterator) result;
          }
        };
      }
      return Collections.singleton(result);
    }
    return null;
  }

  private Object convert(Object o, OType oType) {
    return OType.convert(o, oType.getDefaultJavaType());
  }

  private Map<String, Object> getEqualityOperations(OAndBlock condition, OCommandContext ctx) {
    Map<String, Object> result = new HashMap<String, Object>();
    for (OBooleanExpression expression : condition.subBlocks) {
      if (expression instanceof OBinaryCondition) {
        OBinaryCondition b = (OBinaryCondition) expression;
        if (b.operator instanceof OEqualsCompareOperator) {
          if (b.left.isBaseIdentifier() && b.right.isEarlyCalculated()) {
            result.put(b.left.toString(), b.right.execute((OResult) null, ctx));
          }
        }
      }
    }
    return result;
  }

  public List<OAndBlock> flatten() {
    if (this.baseExpression == null) {
      return Collections.EMPTY_LIST;
    }
    if (flattened == null) {
      flattened = this.baseExpression.flatten();
    }
    // TODO remove false conditions (contraddictions)
    return flattened;

  }

  public List<OBinaryCondition> getIndexedFunctionConditions(OClass iSchemaClass, ODatabaseDocumentInternal database) {
    if (baseExpression == null) {
      return null;
    }
    return this.baseExpression.getIndexedFunctionConditions(iSchemaClass, database);
  }

  public boolean needsAliases(Set<String> aliases) {
    return this.baseExpression.needsAliases(aliases);
  }

  public void setBaseExpression(OBooleanExpression baseExpression) {
    this.baseExpression = baseExpression;
  }

  public OWhereClause copy() {
    OWhereClause result = new OWhereClause(-1);
    result.baseExpression = baseExpression.copy();
    result.flattened = flattened == null ? null : flattened.stream().map(x -> x.copy()).collect(Collectors.toList());
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    OWhereClause that = (OWhereClause) o;

    if (baseExpression != null ? !baseExpression.equals(that.baseExpression) : that.baseExpression != null)
      return false;
    if (flattened != null ? !flattened.equals(that.flattened) : that.flattened != null)
      return false;

    return true;
  }

  @Override public int hashCode() {
    int result = baseExpression != null ? baseExpression.hashCode() : 0;
    result = 31 * result + (flattened != null ? flattened.hashCode() : 0);
    return result;
  }

  public void extractSubQueries(SubQueryCollector collector) {
    if(baseExpression!=null){
      baseExpression.extractSubQueries(collector);
    }
    flattened = null;
  }

  public boolean refersToParent() {
    return baseExpression!=null && baseExpression.refersToParent();
  }

  public OBooleanExpression getBaseExpression() {
    return baseExpression;
  }

  public List<OAndBlock> getFlattened() {
    return flattened;
  }

  public void setFlattened(List<OAndBlock> flattened) {
    this.flattened = flattened;
  }
}
/* JavaCC - OriginalChecksum=e8015d01ce1ab2bc337062e9e3f2603e (do not edit this line) */
