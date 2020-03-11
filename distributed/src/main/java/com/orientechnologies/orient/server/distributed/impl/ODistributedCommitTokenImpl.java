package com.orientechnologies.orient.server.distributed.impl;

import com.orientechnologies.orient.server.distributed.ODistributedCommitToken;

import java.util.concurrent.CountDownLatch;

public class ODistributedCommitTokenImpl implements ODistributedCommitToken {
  private final CountDownLatch request;
  private final byte[]         status;

  public ODistributedCommitTokenImpl(CountDownLatch request, byte[] status) {
    this.request = request;
    this.status = status;
  }

  @Override
  public byte[] token() {
    return status;
  }

  @Override
  public void notifyDone() {
    request.countDown();
  }
}
