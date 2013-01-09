/**
 * Kapselt Produktdaten. 
 *
 */
package media.definitions;

import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class Product {

	public enum Type { book, dvd, music }

	private Integer number;
	private String asin;
	private boolean stateNew = true;
	private Float avgRating = null;
	private Integer salesRank = null;
	private String title = "";
	private Float price = null;
	private String currency = "";
	private boolean available = false;
	private String picUrl = "";
	private Set<Category> categories = new LinkedHashSet<Category>(); //leafs of categoryTree
	private Set<Review> reviews = new LinkedHashSet<Review>();
	private Type type;

	/**
	 * @return true if the product is a book
	 */
	public boolean isBook() {
		return type == Type.book;
	}

	/**
	 * @return true if the product is a dvd
	 */
	public boolean isDvd() {
		return type == Type.dvd;
	}

	/**
	 * @return true if the product is music
	 */
	public boolean isMusic() {
		return type == Type.music;
	}

	public Product(String asin, Float averagerating,
								 Integer salesrank, String title, Float price,
								 String currency, boolean available,
								 String pic_url, Set<Category> categories, Type type) {
		this.asin = asin;
		this.avgRating = averagerating;
		this.salesRank = salesrank;
		this.title = title;
		this.price = price;
		this.currency = currency;
		this.available = available;
		this.picUrl = pic_url;
		this.categories = categories;
		this.type = type;
	}

	public Product() {
	}

	public Product(Product prod) {
		this.asin = prod.getAsin();
		this.avgRating = prod.getAvgRating();
		this.salesRank = prod.getSalesRank();
		this.title = prod.getTitle();
		this.price = prod.getPrice();
		this.currency = prod.getCurrency();
		this.available = prod.isAvailable();
		this.picUrl = prod.getPicUrl();
		this.type = prod.getType();
		
	}

	/**
	 * Returnt the type of the product.
	 *
	 * @return type of the product
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the type of the product.
	 *
	 * @param type type of the product
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Returnt the type of the product as string.
	 *
	 * @return type of the product as string
	 */
	public String getTypeAsString() {
		return type.toString();
	}

	/**
	 * Sets the type of the product as string.
	 *
	 * @param value type of the product as string
	 */
	public void setTypeAsString(String value) {
		value = value.toLowerCase();

		if (value.equals("buch")) {
			type = Type.book;
		}
		else if (value.equals("dvd")) {
			type = Type.dvd;
		}
		else if (value.equals("musik_cd")) {
			type = Type.music;
		}
		else {
			type = Type.valueOf(value);
		}
	}

	/**
	 * @return Returns the available.
	 */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * @param available The available to set.
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}

	/**
	 * @return Returns the avg_rating.
	 */
	public Float getAvgRating() {
		return this.avgRating;
	}

	/**
	 * @param avgRating The avg_rating to set.
	 */
	public void setAvgRating(Float avgRating) {
		this.avgRating = avgRating;
	}

	/**
	 * @return Returns the category.
	 */
	public Set<Category> getCategories() {
		return this.categories;
	}

	/**
	 * @param categories The category to set.
	 */
	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	/**
	 * @return Returns the currency.
	 */
	public String getCurrency() {
		return this.currency;
	}

	/**
	 * @param currency The currency to set.
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return Returns the id.
	 */
	public Integer getNumber() {
		return this.number;
	}

	/**
	 * @param number The id to set.
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * Returns the product identification of the shop.
	 *
	 * @return product identification of the shop
	 */
	public String getAsin() {
		return asin;
	}

	/**
	 * Sets the product identification of the shop.
	 * 
	 * @param asin product identification of the shop
	 */
	public void setAsin(String asin) {
		this.asin = asin;
	}

	/**
	 * @return Returns the pic_url.
	 */
	public String getPicUrl() {
		return this.picUrl;
	}

	/**
	 * @param picUrl The pic_url to set.
	 */
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	/**
	 * @return Returns the price.
	 */
	public Float getPrice() {
		return this.price;
	}

	/**
	 * @param price The price to set.
	 */
	public void setPrice(Float price) {
		this.price = price;
	}

	/**
	 * @return Returns the reviews.
	 */
	public Set<Review> getReviews() {
		return this.reviews;
	}

	/**
	 * @param reviews The reviews to set.
	 */
	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	/**
	 * Adds a review to the product. This method sets also the product property of the review.
	 * Use this method instead of getReviews().add()!
	 *
	 * @param review review to add
	 */
	public void addReview(Review review) {
		reviews.add(review);
		review.setProduct(this);
	}

	/**
	 * Removes the review from this product.
	 * Use this method instead of getReviews().remove()!
	 *
	 * @param review review to remove
	 */
	public void removeReview(Review review) {
		reviews.remove(review);
		review.setProduct(null);
	}

	/**
	 * @return Returns the salesrank.
	 */
	public Integer getSalesRank() {
		return this.salesRank;
	}

	/**
	 * @param salesRank The salesrank to set.
	 */
	public void setSalesRank(Integer salesRank) {
		this.salesRank = salesRank;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return Returns the state_new.
	 */
	public boolean isStateNew() {
		return this.stateNew;
	}

	/**
	 * @param stateNew The state_new to set.
	 */
	public void setStateNew(boolean stateNew) {
		this.stateNew = stateNew;
	}

	public String toString() {
		return title;
	}
}
