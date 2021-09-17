package entities;

public class Order {

	int id;
	int userid;
	int shipped;
	OrderLine[] orderlines;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	
	public int getShipped() {
		return shipped;
	}
	public void setShipped(int shipped) {
		this.shipped = shipped;
	}
	
	public OrderLine[] getOrderlines() {
		return orderlines;
	}
	public void setOrderlines(OrderLine[] orderlines) {
		this.orderlines = orderlines;
	}
	
	
	
	

}
