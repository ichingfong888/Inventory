package com.iching.inventory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DBRunnable implements Runnable {
private ReentrantLock lock;
	
	public DBRunnable(ReentrantLock lock) {
		this.lock = lock;
	}
	
	public void run() {
		try {
			Thread.sleep(5000);
			if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
				try {
					InventoryApp inventoryApp = new InventoryApp();
					inventoryApp.dbGenerator();
				} finally {
					lock.unlock();
				}
			} else {
				System.out.println("dbGenerator: unable to lock thread " + Thread.currentThread().getName() + " will retry again");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
