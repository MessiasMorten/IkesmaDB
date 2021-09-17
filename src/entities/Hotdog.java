package entities;

public class Hotdog extends Product {

	String flavor;

	public Hotdog(int price, String description, String flavor) {
		super(price, description);
		this.flavor = flavor;
	}

	public String getFlavor() {
		return flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}
	
	

}
