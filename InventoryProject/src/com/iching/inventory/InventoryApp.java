package com.iching.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryApp {
	public static void main(String[] args) {
		// db simulator
		ReentrantLock lock = new ReentrantLock();
		InventoryApp inventoryApp = new InventoryApp();
		inventoryApp.dbGenerator();
		
		List<Order> taskQueue = new ArrayList<>();
		int queueSize = 6;
		
		String path = "./resource/";
		String fileName = path + "InventoryInput.txt";
//		String fileName = path + "InvalidInventoryInput.txt";	
//		String fileName = path + "SpecialInventoryInput.txt";
		Runnable dataSource = new DataSource(taskQueue, queueSize, fileName);
		Runnable inventoryAllocator = new InventoryAllocator(taskQueue, lock);
		
		Thread dataSourceThread = new Thread(dataSource);
		Thread inventoryAllocatorThread = new Thread(inventoryAllocator);
		dataSourceThread.start();
		inventoryAllocatorThread.start();	
		
		// generate another set of data 
		Runnable dbRunnable = new DBRunnable(lock);
		Thread dbGeneratorThread = new Thread(dbRunnable);
		dbGeneratorThread.start();
	}
	
	public void dbGenerator() {
		InventoryDB db = InventoryDB.getInstance();
		db.addProduct("A", 2);
		db.addProduct("B", 3);
		db.addProduct("C", 1);
		db.addProduct("D", 0);
		db.addProduct("E", 0);
	}
}