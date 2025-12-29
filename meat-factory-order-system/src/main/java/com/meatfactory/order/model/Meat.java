package com.meatfactory.order.model;

public class Meat {

	private String code; //BEEF
	private String name; //牛肉
	private int price; //単価1000

	//コンストラクタ
	public Meat(String code, String name, int price) {
		this.code = code;
		this.name = name;
		this.price = price;
	}
	//getter
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

}
