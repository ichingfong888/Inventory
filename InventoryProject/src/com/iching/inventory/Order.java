package com.iching.inventory;

import java.util.LinkedList;
import java.util.List;

public class Order {
	private int requestId;
	private int header;
	private List<Product> list = new LinkedList<>();
	
	public Order() {}
	
	public Order(int requestId, int header, List<Product> list) {
		this.requestId = requestId;
		this.header = header;
		this.list = list;
	}
	
	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getHeader() {
		return header;
	}
	public void setHeader(int header) {
		this.header = header;
	}
	public List<Product> getList() {
		return list;
	}
	public void setList(List<Product> list) {
		this.list = list;
	}
}