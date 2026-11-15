package com.hrydzina.task3.service;

import com.hrydzina.task3.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PortLauncher {

  private static final Logger logger = LogManager.getLogger(PortLauncher.class);
  private static final String THREAD_NAME_PREFIX = "Ship-";

  public void launch(List<Ship> ships) throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    for (Ship ship : ships) {
      Thread thread = new Thread(ship, THREAD_NAME_PREFIX + ship.getId());
      threads.add(thread);
      thread.start();
    }
    logger.info("{} ship threads were started", threads.size());

    try {
      joinAll(threads);
    } catch (InterruptedException e) {
      logger.warn("Main thread was interrupted, stopping ship threads");
      for (Thread thread : threads) {
        thread.interrupt();
      }
      joinAll(threads);
      throw e;
    }
  }

  private void joinAll(List<Thread> threads) throws InterruptedException {
    for (Thread thread : threads) {
      thread.join();
    }
  }
}

