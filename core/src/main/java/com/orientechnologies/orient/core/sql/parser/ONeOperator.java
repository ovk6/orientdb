/* Generated By:JJTree: Do not edit this line. ONeOperator.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.sql.operator.OQueryOperatorEquals;

public
class ONeOperator extends SimpleNode  implements OBinaryCompareOperator{
  public ONeOperator(int id) {
    super(id);
  }

  public ONeOperator(OrientSql p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  @Override public boolean execute(Object left, Object right) {
    return !OQueryOperatorEquals.equals(left, right);
  }

  @Override public String toString() {
    return "!=";
  }

  @Override public boolean supportsBasicCalculation() {
    return true;
  }

  @Override public ONeOperator copy() {
    return new ONeOperator(-1);
  }

  @Override public boolean equals(Object obj) {
    return obj != null && obj.getClass().equals(this.getClass());
  }

  @Override public int hashCode() {
    return getClass().hashCode();
  }
}
/* JavaCC - OriginalChecksum=ac0ae426fb86c930dea83013ddc202ba (do not edit this line) */
