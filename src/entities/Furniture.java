package entities;

public class Furniture extends Product {
	
	int itemNo;
	int weight;
	
	public Furniture(int price, String description, int itemNo, int weight) {
		super(price, description);
		this.itemNo = itemNo;
		this.weight = weight;
	}

	public int getItemNo() {
		return itemNo;
	}
	
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	


}
