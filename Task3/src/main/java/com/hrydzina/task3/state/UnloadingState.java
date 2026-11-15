package com.hrydzina.task3.state;

import com.hrydzina.task3.entity.Port;
import com.hrydzina.task3.entity.PortStorage;
import com.hrydzina.task3.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class UnloadingState implements ShipState {

  private static final Logger logger = LogManager.getLogger(UnloadingState.class);
  private static final long TIME_PER_CONTAINER = 20;

  @Override
  public void handle(Ship ship) throws InterruptedException {
    PortStorage storage = Port.getInstance().getStorage();
    logger.info("Ship {} starts unloading {} containers", ship.getId(), ship.getUnloadCount());

    while (ship.getUnloadCount() > 0) {
      int unloaded = storage.putContainers(ship.getUnloadCount());
      ship.setUnloadCount(ship.getUnloadCount() - unloaded);
      ship.setContainers(ship.getContainers() - unloaded);
      TimeUnit.MILLISECONDS.sleep(unloaded * TIME_PER_CONTAINER);
    }
    logger.info("Ship {} finished unloading", ship.getId());

    if (ship.getLoadCount() > 0) {
      ship.setState(new LoadingState());
    } else {
      ship.setState(new DepartingState());
    }
  }
}

