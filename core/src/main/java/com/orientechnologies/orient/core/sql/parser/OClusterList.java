/* Generated By:JJTree: Do not edit this line. OClusterList.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OClusterList extends SimpleNode {

  protected List<OIdentifier> clusters = new ArrayList<OIdentifier>();

  public OClusterList(int id) {
    super(id);
  }

  public OClusterList(OrientSql p, int id) {
    super(p, id);
  }

  /**
   * Accept the visitor.
   **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {

    builder.append("cluster:[");
    boolean first = true;
    for (OIdentifier id : clusters) {
      if (!first) {
        builder.append(",");
      }
      id.toString(params, builder);
      first = false;
    }
    builder.append("]");
  }

  public List<OCluster> toListOfClusters() {
    List<OCluster> result = new ArrayList<>();
    for (OIdentifier id : clusters) {
      OCluster cluster = new OCluster(-1);
      cluster.clusterName = id.getStringValue();
      result.add(cluster);
    }
    return result;
  }

  public OClusterList copy() {
    OClusterList result = new OClusterList(-1);
    result.clusters = clusters.stream().map(x -> x.copy()).collect(Collectors.toList());
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    OClusterList that = (OClusterList) o;

    if (clusters != null ? !clusters.equals(that.clusters) : that.clusters != null)
      return false;

    return true;
  }

  @Override public int hashCode() {
    return clusters != null ? clusters.hashCode() : 0;
  }
}
/* JavaCC - OriginalChecksum=bd90ffa0b9d17f204b3cf2d47eedb409 (do not edit this line) */
