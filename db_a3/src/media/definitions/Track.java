package media.definitions;

import java.io.Serializable;

/**
 * @author Stefan Endrullis
 */
public class Track<P extends Product> implements Serializable{
	protected Long id;
	protected P product;
	protected String name;

	public Track() { }

	public Track(String name) { this.name = name;	}

	public Long getId() { return id; }

	public void setId(Long id) { this.id = id; }

	public P getProduct() { return product; }

	public void setProduct(P product) { this.product = product; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

//	public int getNo() { return ((Music)product).getTracks().indexOf(this); }
//
//	public void setNo(int no) {	} // do nothing (this method is used to declare "no" as bean property)

	public String toString() { return name;	}
}
