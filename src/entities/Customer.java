package entities;

public class Customer {

	int id;
	String email;
	String password;
	String first_name;
	String family_name;
	int membership;
	int discount_furniture;
	int discount_textile;
	int discount_hotdog;
	
	
	
	public Customer(int id,String email, String password, String first_name, String family_name, int membership, int discount_furniture,
			int discount_textile, int discount_hotdog) {

		this.id = id;
		this.email = email;
		this.password = password;
		this.first_name = first_name;
		this.family_name = family_name;
		this.membership = membership;
		this.discount_furniture = discount_furniture;
		this.discount_textile = discount_textile;
		this.discount_hotdog = discount_hotdog;
		
	}

	public int getUserid() {
		return id;
	}
	public void setUserid(int id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	public String getFamily_name() {
		return family_name;
	}
	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}
	
	public int getMembership() {
		return membership;
	}
	public void setMembership(int membership) {
		this.membership = membership;
	}
	
	public int getDiscount_furniture() {
		return discount_furniture;
	}
	public void setDiscount_furniture(int discount_furniture) {
		this.discount_furniture = discount_furniture;
	}
	
	public int getDiscount_textile() {
		return discount_textile;
	}
	public void setDiscount_textile(int discount_textile) {
		this.discount_textile = discount_textile;
	}
	
	public int getDiscount_hotdog() {
		return discount_hotdog;
	}
	public void setDiscount_hotdog(int discount_hotdog) {
		this.discount_hotdog = discount_hotdog;
	}
	
	
	
	
	

}
