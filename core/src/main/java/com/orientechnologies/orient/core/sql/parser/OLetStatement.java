/* Generated By:JJTree: Do not edit this line. OLetStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import java.util.Map;

public class OLetStatement extends OStatement {
  protected OIdentifier name;

  protected OStatement  statement;
  protected OExpression expression;

  public OLetStatement(int id) {
    super(id);
  }

  public OLetStatement(OrientSql p, int id) {
    super(p, id);
  }

  @Override public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("LET ");
    name.toString(params, builder);
    builder.append(" = ");
    if (statement != null) {
      statement.toString(params, builder);
    } else {
      expression.toString(params, builder);
    }
  }

  @Override public OLetStatement copy() {
    OLetStatement result = new OLetStatement(-1);
    result.name = name == null ? null : name.copy();
    result.statement = statement == null ? null : statement.copy();
    result.expression = expression == null ? null : expression.copy();
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    OLetStatement that = (OLetStatement) o;

    if (name != null ? !name.equals(that.name) : that.name != null)
      return false;
    if (statement != null ? !statement.equals(that.statement) : that.statement != null)
      return false;
    if (expression != null ? !expression.equals(that.expression) : that.expression != null)
      return false;

    return true;
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (statement != null ? statement.hashCode() : 0);
    result = 31 * result + (expression != null ? expression.hashCode() : 0);
    return result;
  }
}
/* JavaCC - OriginalChecksum=cc646e5449351ad9ced844f61b687928 (do not edit this line) */
