package com.hrydzina.task3.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PortStorage {

  private static final Logger logger = LogManager.getLogger(PortStorage.class);

  private final int capacity;
  private int size;
  private final Lock lock = new ReentrantLock();
  private final Condition notFull = lock.newCondition();
  private final Condition notEmpty = lock.newCondition();

  public PortStorage(int capacity, int initialContainers) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Storage capacity must be positive: " + capacity);
    }
    if (initialContainers < 0 || initialContainers > capacity) {
      throw new IllegalArgumentException("Initial containers must be in [0, " + capacity + "]: "
              + initialContainers);
    }
    this.capacity = capacity;
    this.size = initialContainers;
  }
  public int putContainers(int requested) throws InterruptedException {
    checkRequested(requested);
    lock.lock();
    try {
      while (size == capacity) {
        logger.info("Storage is full, waiting for free space");
        notFull.await();
      }
      int accepted = Math.min(requested, capacity - size);
      size += accepted;
      logger.info("{} containers were put to storage. Storage: {}/{}", accepted, size, capacity);
      notEmpty.signalAll();
      return accepted;
    } finally {
      lock.unlock();
    }
  }
  public int takeContainers(int requested) throws InterruptedException {
    checkRequested(requested);
    lock.lock();
    try {
      while (size == 0) {
        logger.info("Storage is empty, waiting for containers");
        notEmpty.await();
      }
      int given = Math.min(requested, size);
      size -= given;
      logger.info("{} containers were taken from storage. Storage: {}/{}", given, size, capacity);
      notFull.signalAll();
      return given;
    } finally {
      lock.unlock();
    }
  }
  public int getSize() {
    lock.lock();
    try {
      return size;
    } finally {
      lock.unlock();
    }
  }
  public int getCapacity() {
    return capacity;
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("PortStorage{");
    builder.append("capacity=").append(capacity);
    builder.append(", size=").append(getSize());
    builder.append('}');
    return builder.toString();
  }
  private void checkRequested(int requested) {
    if (requested <= 0) {
      throw new IllegalArgumentException("Requested containers must be positive: " + requested);
    }
  }
}

