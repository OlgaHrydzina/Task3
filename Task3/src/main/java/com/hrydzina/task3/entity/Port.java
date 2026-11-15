package com.hrydzina.task3.entity;

import com.hrydzina.task3.config.PortConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {

  private static final Logger logger = LogManager.getLogger(Port.class);

  private static Port instance;
  private static final Lock instanceLock = new ReentrantLock();

  private final int berthCount;
  private final Deque<Berth> freeBerths = new ArrayDeque<>();
  private final Deque<Ship> shipQueue = new ArrayDeque<>();
  private final Lock berthLock = new ReentrantLock();
  private final Condition berthReleased = berthLock.newCondition();
  private final PortStorage storage;
  private final AtomicInteger servedShips = new AtomicInteger(0);

  private Port(PortConfig config) {
    berthCount = config.berthCount();
    for (int i = 1; i <= berthCount; i++) {
      freeBerths.addLast(new Berth(i));
    }
    storage = new PortStorage(config.storageCapacity(), config.storageContainers());
    logger.info("Port was created: {}", config);
  }
  public static void initialize(PortConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("Port config must not be null");
    }
    instanceLock.lock();
    try {
      if (instance != null) {
        throw new IllegalStateException("Port is already initialized");
      }
      instance = new Port(config);
    } finally {
      instanceLock.unlock();
    }
  }
  public static Port getInstance() {
    instanceLock.lock();
    try {
      if (instance == null) {
        throw new IllegalStateException("Port is not initialized, call Port.initialize() first");
      }
      return instance;
    } finally {
      instanceLock.unlock();
    }
  }
  public Berth acquireBerth(Ship ship) throws InterruptedException {
    if (ship == null) {
      throw new IllegalArgumentException("Ship must not be null");
    }
    berthLock.lock();
    try {
      shipQueue.addLast(ship);
      logger.info("Ship {} joined the queue. Ships in queue: {}", ship.getId(), shipQueue.size());
      try {
        while (shipQueue.peekFirst() != ship || freeBerths.isEmpty()) {
          berthReleased.await();
        }
      } catch (InterruptedException e) {
        shipQueue.remove(ship);
        berthReleased.signalAll();
        logger.warn("Ship {} was interrupted and left the queue", ship.getId());
        throw e;
      }
      shipQueue.pollFirst();
      berthReleased.signalAll();
      return freeBerths.pollFirst();
    } finally {
      berthLock.unlock();
    }
  }
  public void releaseBerth(Berth berth) {
    if (berth == null) {
      throw new IllegalArgumentException("Berth must not be null");
    }
    berthLock.lock();
    try {
      if (berth.id() < 1 || berth.id() > berthCount) {
        throw new IllegalArgumentException("Berth " + berth.id() + " does not belong to the port");
      }
      if (freeBerths.contains(berth)) {
        throw new IllegalStateException("Berth " + berth.id() + " is already free");
      }
      freeBerths.addLast(berth);
      berthReleased.signalAll();
    } finally {
      berthLock.unlock();
    }
  }
  public void registerServedShip() {
    servedShips.incrementAndGet();
  }
  public PortStorage getStorage() {
    return storage;
  }
  public int getServedShips() {
    return servedShips.get();
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Port{");
    builder.append("berthCount=").append(berthCount);
    builder.append(", servedShips=").append(servedShips.get());
    builder.append(", storage=").append(storage);
    builder.append('}');
    return builder.toString();
  }
}

