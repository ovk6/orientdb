package com.orientechnologies.orient.server.distributed;

public interface ODistributedCommitToken {

  byte[] token();

  void notifyDone();

}
