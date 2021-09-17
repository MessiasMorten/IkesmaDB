package entities;

public class Product {
	
	int price;
	String description;
	int discount; //For display purposes
	
	public Product(int price, String description) {
		this.price = price;
		this.description = description;
	}

	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}
	
	

}
