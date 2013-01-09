package media.definitions;

/**
 * @author Stefan Endrullis
 */
public abstract class ProductChild<P extends Product> {
	protected Long id;
	protected P product;
	protected String name;

	public ProductChild() { }

	public ProductChild(String name) { this.name = name;	}

	public Long getId() { return id; }

	public void setId(Long id) { this.id = id; }

	public P getProduct() { return product; }

	public void setProduct(P product) { this.product = product; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public abstract int getNo();

	public void setNo(int no) {	} // do nothing (this method is used to declare "no" as bean property)

	public String toString() { return name;	}
}
