package com.hrydzina.task3.entity;

import com.hrydzina.task3.config.ShipConfig;
import com.hrydzina.task3.state.ArrivedState;
import com.hrydzina.task3.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class Ship implements Runnable {

  private static final Logger logger = LogManager.getLogger(Ship.class);

  private final int id;
  private final int capacity;
  private int containers;
  private int unloadCount;
  private int loadCount;
  private ShipState state;
  private Berth berth;

  public Ship(ShipConfig config) {
    this(config.id(), config.capacity(), config.containers(), config.unloadCount(), config.loadCount());
  }

  public Ship(int id, int capacity, int containers, int unloadCount, int loadCount) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Ship " + id + ": capacity must be positive: " + capacity);
    }
    if (containers < 0 || containers > capacity) {
      throw new IllegalArgumentException("Ship " + id + ": containers must be in [0, " + capacity + "]: "
              + containers);
    }
    if (unloadCount < 0 || unloadCount > containers) {
      throw new IllegalArgumentException("Ship " + id + ": unload count must be in [0, " + containers + "]: "
              + unloadCount);
    }
    if (loadCount < 0) {
      throw new IllegalArgumentException("Ship " + id + ": load count must not be negative: " + loadCount);
    }
    this.id = id;
    this.capacity = capacity;
    this.containers = containers;
    this.unloadCount = unloadCount;
    this.loadCount = loadCount;
    this.state = new ArrivedState();
  }

  @Override
  public void run() {
    try {
      while (!state.isFinal()) {
        state.handle(this);
      }
    } catch (InterruptedException e) {
      logger.warn("Ship {} was interrupted in state {}", id, state.getClass().getSimpleName());
      Thread.currentThread().interrupt();
    } finally {
      if (berth != null) {
        leaveBerth();
      }
    }
  }

  public Berth leaveBerth() {
    if (berth == null) {
      throw new IllegalStateException("Ship " + id + " is not moored");
    }
    Berth releasedBerth = berth;
    Port.getInstance().releaseBerth(releasedBerth);
    berth = null;
    return releasedBerth;
  }

  public int getId() {
    return id;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getContainers() {
    return containers;
  }

  public void setContainers(int containers) {
    if (containers < 0 || containers > capacity) {
      throw new IllegalArgumentException("Ship " + id + ": containers must be in [0, " + capacity + "]: "
              + containers);
    }
    this.containers = containers;
  }

  public int getUnloadCount() {
    return unloadCount;
  }

  public void setUnloadCount(int unloadCount) {
    if (unloadCount < 0 || unloadCount > containers) {
      throw new IllegalArgumentException("Ship " + id + ": unload count must be in [0, " + containers + "]: "
              + unloadCount);
    }
    this.unloadCount = unloadCount;
  }

  public int getLoadCount() {
    return loadCount;
  }

  public void setLoadCount(int loadCount) {
    if (loadCount < 0) {
      throw new IllegalArgumentException("Ship " + id + ": load count must not be negative: " + loadCount);
    }
    this.loadCount = loadCount;
  }

  public ShipState getState() {
    return state;
  }

  public void setState(ShipState state) {
    if (state == null) {
      throw new IllegalArgumentException("State must not be null");
    }
    this.state = state;
  }

  public Berth getBerth() {
    return berth;
  }

  public void setBerth(Berth berth) {
    if (berth == null) {
      throw new IllegalArgumentException("Berth must not be null, use leaveBerth() to leave");
    }
    if (this.berth != null) {
      throw new IllegalStateException("Ship " + id + " is already moored to berth " + this.berth.id());
    }
    this.berth = berth;
  }
}

