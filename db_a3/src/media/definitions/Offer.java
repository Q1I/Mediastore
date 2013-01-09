package media.definitions;

import java.io.Serializable;

/**
 * @author Stefan Endrullis
 */
public class Offer implements Serializable{
	private Integer id;
	private Product product;
	private double price;
	private String currency = "EUR";
	private String location;
	private String state;
	private static double mult = 0.01;
	public Offer() {
	}

	public Offer(float price, String currency, String location) {
		this.price = price;
		this.currency = currency;
		this.location = location;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = mult*price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/** Edit if you want. */
	public String toString() {
		return product+" , " +price +" , "+location;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
}
