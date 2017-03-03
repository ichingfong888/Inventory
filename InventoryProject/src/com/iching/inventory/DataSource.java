package com.iching.inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class DataSource implements Runnable {
	private List<Order> taskQueue;
	private final int MAX_SIZE;
	private static int requestId;
	private File dataSource;
	
	public DataSource(List<Order> queue, int size, String dataSource) {
		this.taskQueue = queue;
		MAX_SIZE = size;
		requestId = 1;
		this.dataSource = new File(dataSource);
	}
	
	public void run() {
		// construct Orders
		try {
			Scanner sc = new Scanner(dataSource);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if((line == null) || (line.length() == 0)) {
					continue;
				}
				Order myOrder = generateOrder(line);
				if(myOrder != null) {
					myOrder.setRequestId(requestId++);
					put(myOrder);
				}	
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void put(Order order) {
		synchronized (taskQueue) {
			while(taskQueue.size() == MAX_SIZE) { 
				try {
					taskQueue.wait();
				} catch (InterruptedException e) {
					System.out.println("put() InterruptedException:" + e.getMessage());
				}
			}
			
			taskQueue.add(order);
			taskQueue.notifyAll();
		}
	}
	
	public Order generateOrder(String str) {
		Order myOrder = new Order();
		List<Product> list = new LinkedList<>();
		DataSourceParser parser = new DataSourceParser();
		
		// well form
		str = str.replaceAll(" ", "");		
		if(!parser.checkRequestWellForm(str)) {
			return null;
		}
		str = str.substring(1, str.length()); // take off well form {}
		
		// get header
		List<String> resultList = getHeader(str, parser);
		str = resultList.get(0);
		String headerStr = resultList.get(1);
		int header = Integer.parseInt(headerStr);
		
		myOrder.setHeader(header);
		myOrder.setList(list);
		
		// Lines
		str = str.substring(headerStr.length());
		String pattern = ",\"Lines\":"; 
		str = parser.checkRequest(str, pattern, "Lines", str.indexOf(pattern), pattern.length());
		if(str.equals("")) {
			return null;
		}
	
		// get product & quantity
		if(!parser.checkRequestWellForm(str)) {
			return null;
		}
		String[] st = str.split("}");
		
		for(int i=0; i<st.length; i++) {
			Product product = getProduct(st[i], parser);
			list.add(product);
		}
		
		if ((list.size() == 1)) {
			if (list.get(0) != null) {
				if (list.get(0).getQuantity() == 0) {
					System.out.println("Invalid request! Quantity is 0 for only 1 product.");
					return null;
				}
			}
		}
		
		return myOrder;
	}
	
	public List<String> getHeader(String str, DataSourceParser parser) {
		String pattern = "\"Header\":";
		str = parser.checkRequest(str, pattern, "Header", str.indexOf(pattern), pattern.length());
		List<String> list = new ArrayList<>();
		list.add(str);
		
		if(str.equals("")) {
			return null;
		}
		
		// get header
		String headerStr = "";
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i) == ',') {
				break;
			}
			headerStr = headerStr + str.charAt(i);
		}
		
		list.add(headerStr);
		return list;
	}	
	
	private Product getProduct(String str, DataSourceParser parser) {
		// get productStr
		List<String> resultList = getProductString(str, parser);
		str = resultList.get(0);
		String productStr = resultList.get(1);
		if (productStr == null) {
			return null;
		}
		
		if(!isValidProductName(productStr)) {
			System.out.println("Invalid request! Invalid product name: " + productStr);
			return null;
		}

		// get quantity
		Integer quantity = getQuantity(str.substring(str.indexOf("\"") + 1), parser);
		if (quantity == null) {
			return null;
		}
		
		if((quantity < 0) || (quantity > 5)) {
			System.out.println("Invalid request! Quantity is less than 0 or greater than 5.");
			return null;
		}
 		
		return new Product(productStr, quantity);
	}
	
	private boolean isValidProductName(String name) {
		for(ProductSpec ps: ProductSpec.values()) {
			if(name.equals(ps.name())) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getProductString(String str, DataSourceParser parser) {
		List<String> resultList = new ArrayList<>();
		String pattern = "{\"Product\":\""; 
		str = parser.checkRequest(str, pattern, "Product", str.indexOf(pattern), pattern.length());
		
		if(str.equals("")) {
			return null;
		}
		
		if(!str.contains("\"")) {
			System.out.println("Request misses \"");
			return null;
		}
		String productStr = str.substring(0, str.indexOf("\""));
		if(!productStr.matches("[a-zA-Z0-9]")) {
			System.out.println("Something wrong at Product name");
			return null;
		}
		
		resultList.add(str);
		resultList.add(productStr);
		
		return resultList;
	}
	
	public Integer getQuantity(String str, DataSourceParser parser) {
		String pattern = ",\"Quantity\":\"";
		String quantityStr = parser.checkRequest(str, pattern, "Quantity", str.indexOf(pattern), pattern.length());
		
		if(!quantityStr.endsWith("\"")) {
			System.out.println("Missing \" at the end");
			return null;
		}	
		quantityStr = quantityStr.substring(0, quantityStr.length()-1);
		
		return Integer.parseInt(quantityStr);
	}
	
}
