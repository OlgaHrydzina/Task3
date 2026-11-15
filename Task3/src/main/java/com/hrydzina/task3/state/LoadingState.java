package com.hrydzina.task3.state;

import com.hrydzina.task3.entity.Port;
import com.hrydzina.task3.entity.PortStorage;
import com.hrydzina.task3.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class LoadingState implements ShipState {

  private static final Logger logger = LogManager.getLogger(LoadingState.class);
  private static final long TIME_PER_CONTAINER = 20;

  @Override
  public void handle(Ship ship) throws InterruptedException {
    PortStorage storage = Port.getInstance().getStorage();
    int freeSpace = ship.getCapacity() - ship.getContainers();
    ship.setLoadCount(Math.min(ship.getLoadCount(), freeSpace));
    logger.info("Ship {} starts loading {} containers", ship.getId(), ship.getLoadCount());

    while (ship.getLoadCount() > 0) {
      int loaded = storage.takeContainers(ship.getLoadCount());
      ship.setContainers(ship.getContainers() + loaded);
      ship.setLoadCount(ship.getLoadCount() - loaded);
      TimeUnit.MILLISECONDS.sleep(loaded * TIME_PER_CONTAINER);
    }
    logger.info("Ship {} finished loading. Containers on board: {}", ship.getId(), ship.getContainers());
    ship.setState(new DepartingState());
  }
}

