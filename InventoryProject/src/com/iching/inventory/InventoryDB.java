package com.iching.inventory;

import java.util.ArrayList;
import java.util.List;

// Lazy initialize Singleton
public class InventoryDB {
	private static volatile InventoryDB instance = null; 
	private int dbListSize = ProductSpec.values().length;
	private List<Product> dbList = new ArrayList<>(dbListSize);
	private static volatile int dbInventory = 0;
	
	private InventoryDB() {	
		for(ProductSpec item: ProductSpec.values()) {
			dbList.add(new Product(item.toString(), 0));
		}
	}
	
	public static InventoryDB getInstance() {
		if(instance == null) {
			synchronized(InventoryDB.class) {
				if(instance == null) {
					instance = new InventoryDB();
				}
			}
		}
		
		return instance;
	}

	public boolean isInventoryEmpty() {
        return (dbInventory == 0);
     }
	
	public void addProduct(String name, int quantity) {
		try {
			int index = ProductSpec.valueOf(name).ordinal();
			if ((index >=0) && (index < dbListSize)) {
		          quantity += dbList.get(index).getQuantity();
		          dbList.set(index, new Product(name, quantity));
		     }
		     dbInventory += quantity;
		} catch(IllegalArgumentException e) {
			System.out.println(name + " is not in ProductSpec enum. Exception: " + e.getMessage());
		}
	}

	public int getQuantity(String name) {
		try {
			int index = ProductSpec.valueOf(name).ordinal();
			if ((index >= 0) && (index < dbListSize)) {
				return dbList.get(index).getQuantity();
			} 
			return -1;
		} catch (IllegalArgumentException e) {
			System.out.println(name + " is not in ProductSpec enum. Exception: " + e.getMessage());
			return -1;
		}
	}

	public void setQuantity(String name, int quantity) {
		try {
			int index = ProductSpec.valueOf(name).ordinal();
			if ((index >= 0) && (index < dbListSize)) {
				int current = dbList.get(index).getQuantity();
				dbList.get(index).setQuantity(quantity);
				dbInventory += (quantity - current);
			}
		} catch (IllegalArgumentException e) {
			System.out.println(name + " is not in ProductSpec enum. Exception: " + e.getMessage());
		}
	}
}
