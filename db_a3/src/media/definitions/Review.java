/**
 *
 */
package media.definitions;

import java.util.Date;

/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class Review {

	private Integer id;
	private Product product;
	private Kunde username = null;
	private Integer rating = null;
	private Integer helpful_vo = null;
	private Date date = null;
	private String summary = "";
	private String content = "";


	public Review() {
	}

	/**
	 * Konstruktor.
	 *
	 * @param product
	 * @param username
	 * @param rating
	 * @param helpfulvotes
	 * @param date
	 * @param summary
	 * @param content
	 */
	public Review(Product product, Kunde username, Integer rating,
								Integer helpfulvotes, Date date,
								String summary, String content) {
		this.product = product;
		this.username = username;
		this.rating = rating;
		this.helpful_vo = helpfulvotes;
		this.date = date;
		this.summary = summary;
		this.content = content;
	}

	/**
	 * Returns the ID of the review.
	 *
	 * @return ID of the review
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the ID of the review.
	 *
	 * @param id ID of the review
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the product of the review.
	 *
	 * @return product of the review
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * Sets the product of the review.
	 *
	 * @param product product of the review
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * @return Returns the content.
	 */
	public String getContent() {
		return this.content;
	}


	/**
	 * @param content The content to set.
	 */
	public void setContent(String content) {
		this.content = content;
	}


	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return this.date;
	}


	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}


	/**
	 * @return Returns the summary.
	 */
	public String getSummary() {
		return this.summary;
	}


	/**
	 * @param summary The summary to set.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}


	/**
	 * @return Returns the user.
	 */
	public Kunde getUsername() {
		return this.username;
	}


	/**
	 * @param username The user to set.
	 */
	public void setUsername(Kunde username) {
		this.username = username;
	}

	public void setKundenName(String id){
		this.username = new Kunde(id);
	}
	/**
	 * @param helpful_vo The helpful_vo to set.
	 */
	public void setHelpful_vo(Integer helpful_vo) {
		this.helpful_vo = helpful_vo;
	}

	/**
	 * @param rating The rating to set.
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * @return Returns the helpful_vo.
	 */
	public Integer getHelpful_vo() {
		return this.helpful_vo;
	}

	/**
	 * @return Returns the rating.
	 */
	public Integer getRating() {
		return this.rating;
	}

	public String toString() {
		return username.getId();
	}
}
