package com.iching.inventory;

public class DataSourceParser {
	public boolean checkRequestWellForm(String str) {
		if(!((str.charAt(0) == '{') && (str.charAt(str.length()-1) == '}'))) {
			System.out.println("The request is not well form.");
			return false;
		}
		return true;
	}
	
	public String checkRequest(String str, String pattern, String part, int begin, int end) {
		if(!str.substring(begin, end).equals(pattern)) {
			System.out.println("Something wrong at " + part + " part");
			return "";
		}
		
		return str.substring(end);
	}
}
