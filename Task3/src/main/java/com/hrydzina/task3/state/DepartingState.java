package com.hrydzina.task3.state;

import com.hrydzina.task3.entity.Berth;
import com.hrydzina.task3.entity.Port;
import com.hrydzina.task3.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepartingState implements ShipState {

  private static final Logger logger = LogManager.getLogger(DepartingState.class);

  @Override
  public void handle(Ship ship) {
    Berth berth = ship.leaveBerth();
    Port.getInstance().registerServedShip();
    logger.info("Ship {} left berth {}", ship.getId(), berth.id());
    ship.setState(new DepartedState());
  }
}

