package com.iching.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryAllocator implements Runnable {
	private List<Order> taskQueue;
	private List<String> resultList;
	private ReentrantLock lock;

	public InventoryAllocator(List<Order> queue, ReentrantLock lock) {
		this.taskQueue = queue;
		resultList = new ArrayList<>();
		this.lock = lock;
	}

	public void run() {
		while (true) {
			processRequest();
		}
	}

	public void processRequest() {
		synchronized (taskQueue) {
			while (taskQueue.isEmpty()) {
				try {
					taskQueue.wait();
				} catch (InterruptedException e) {
					System.out.println("get() InterruptedException." + e.getMessage());
				}

			}

			// check inventory: empty quantity
			if (isInventoryEmpty()) {
				try {
					if (lock.tryLock(500, TimeUnit.MILLISECONDS)) { 
						try {
							printResult(resultList);
							resultList.clear();
						} finally {
							lock.unlock();	// halt the system
						}
					} 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// System.exit(0);
				return;
			} // end ifInventoryEmpty

			// check inventory: not empty
			Order order = taskQueue.remove(0);
			// System.out.println("requestId = " + order.getRequestId());
			taskQueue.notifyAll();

			String result = processOrder(order);
			if (result.length() > 0) {
				resultList.add(result);
			}
		}
	}

	public void printResult(List<String> list) {
		for (String result : list) {
			System.out.println(result);
		}
	}

	private boolean isInventoryEmpty() {
		return InventoryDB.getInstance().isInventoryEmpty();
	}

	public String processOrder(Order order) {
		List<Product> list = order.getList();
		boolean isOrderProcessed = false;
		int enumSize = ProductSpec.values().length;
		int[] listOrder = new int[enumSize];
		for (int i = 0; i < enumSize; i++) {
			listOrder[i] = 0;
		}

		for (int i = 0; i < list.size(); i++) {
			Product p = list.get(i);
			if(p == null) {
				continue;
			}
			int index = ProductSpec.valueOf(p.getName()).ordinal();
			int dbQuantity = InventoryDB.getInstance().getQuantity(p.getName());
			int orderQuantity = p.getQuantity();

			if (dbQuantity >= orderQuantity) {
				InventoryDB.getInstance().setQuantity(p.getName(), dbQuantity - orderQuantity);
				listOrder[index] = orderQuantity;
				isOrderProcessed = true;

			} else {
				listOrder[index] = -1 * orderQuantity;
				isOrderProcessed = true;
			}
		}

		// bookkeeping
		return bookKeeping(isOrderProcessed, order, listOrder);
	}
	
	private String bookKeeping(boolean isOrderProcessed, Order order, int[] listOrder) {
		if (isOrderProcessed) {
			String result = order.getHeader() + ":";
			for (int num : listOrder) {
				if (num < 0) {
					num = (-1) * num;
				}
				result = result + num + ",";
			}
			result = result.substring(0, result.length() - 1) + "::";
			for (int num : listOrder) {
				if (num < 0 ) {
					num = 0;
				}
				result = result + num + ",";
			}
			result = result.substring(0, result.length() - 1) + "::";
			for (int num : listOrder) {
				if (num >=0) {
					num=0;
				}
				else {
					num = (-1) * num;
				}
				result = result + num + ",";
			}
			return result.substring(0, result.length() - 1);
		} else {
			return "";
		}
	}
}
