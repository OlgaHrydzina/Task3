package com.hrydzina.task3.state;

import com.hrydzina.task3.entity.Ship;

public interface ShipState {

  void handle(Ship ship) throws InterruptedException;

  default boolean isFinal() {
    return false;
  }
}

