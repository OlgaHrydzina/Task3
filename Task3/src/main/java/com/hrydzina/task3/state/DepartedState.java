package com.hrydzina.task3.state;

import com.hrydzina.task3.entity.Ship;

public class DepartedState implements ShipState {

  @Override
  public void handle(Ship ship) {
    throw new IllegalStateException("Ship " + ship.getId() + " has already departed");
  }

  @Override
  public boolean isFinal() {
    return true;
  }
}

