package com.orientechnologies.orient.core.sql.executor;

/**
 * Created by luigidellaquila on 08/08/16.
 */

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luigi Dell'Aquila
 */
public class OUpdateExecutionPlan extends OSelectExecutionPlan {

  List<OResult> result = new ArrayList<>();
  int           next   = 0;

  public OUpdateExecutionPlan(OCommandContext ctx) {
    super(ctx);
  }

  @Override public OTodoResultSet fetchNext(int n) {
    if (next >= result.size()) {
      return new OInternalResultSet();//empty
    }

    OIteratorResultSet nextBlock = new OIteratorResultSet(result.subList(next, Math.min(next + n, result.size())).iterator());
    next += n;
    return nextBlock;
  }

  @Override public void reset(OCommandContext ctx) {
    result.clear();
    next = 0;
    super.reset(ctx);
    executeInternal();
  }

  public void executeInternal() throws OCommandExecutionException {
    while (true) {
      OTodoResultSet nextBlock = super.fetchNext(100);
      if (!nextBlock.hasNext()) {
        return;
      }
      while (nextBlock.hasNext()) {
        result.add(nextBlock.next());
      }
    }
  }
}

