package media.definitions;

/**
 * Eine Person, die zu einem bestimmten Produkt gehoert.
 *
 * @author Stefan Endrullis
 */
public class Person<P extends Product> {
	protected P product;
	protected String name;
	protected String role;

	public Person() { }

	public Person(String name) { this.name = name;	}

	public P getProduct() { return product; }

	public void setProduct(P product) { this.product = product; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getRole() { return role; }

	public void setRole(String role) { this.role = role; }

	public String toString() { return name; }
}
