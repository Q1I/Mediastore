package media.definitions;

import java.io.Serializable;

public class ProduktKategorie implements Serializable{

	private String product;
	private String category;
	
	public ProduktKategorie(){
		
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String toString(){
		return product + " , "+ category;
 	}

}
