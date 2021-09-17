package entities;

public class Textile extends Product {
	
	int itemNo;
	String color;
	
	public Textile(int price, String description, int itemNo, String color) {
		super(price, description);
		this.itemNo = itemNo;
		this.color = color;
	}

	public int getItemNo() {
		return itemNo;
	}
	
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	

}
