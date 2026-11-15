package com.hrydzina.task3.state;

mport com.hrydzina.task3.entity.Berth;
import com.hrydzina.task3.entity.Port;
import com.hrydzina.task3.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArrivedState implements ShipState {

  private static final Logger logger = LogManager.getLogger(ArrivedState.class);

  @Override
  public void handle(Ship ship) throws InterruptedException {
    logger.info("{} arrived at port", ship);
    Berth berth = Port.getInstance().acquireBerth(ship);
    ship.setBerth(berth);
    logger.info("Ship {} moored to berth {}", ship.getId(), berth.id());

    if (ship.getUnloadCount() > 0) {
      ship.setState(new UnloadingState());
    } else if (ship.getLoadCount() > 0) {
      ship.setState(new LoadingState());
    } else {
      ship.setState(new DepartingState());
    }
  }
}

