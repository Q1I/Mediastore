/**
 * Diese Klasse definiert den rekursiven Typ "Category". Ein Objekt
 * sammelt seine Soehne ein. Vorgehensweise ist daher zunaechst das
 * Erstellen der Soehne und Uebergabe an den Vater.
 *
 * @since 05/2006
 */
package media.definitions;

import java.io.Serializable;
import java.util.*;


/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class Category implements Serializable {

	private String id;
	private String name;
	private Set<Category> children = new LinkedHashSet<Category>();
	private Category parent = null;

	/**
	 * Default constructor for hibernate.
	 */
	public Category() {
	}

	/**
	 * Konstruktor
	 *
	 * @param catid	 ID dieser Kategorie
	 * @param catname Name dieser Kategorie
	 */
	public Category(String catid, String catname) {
		this.id = catid;
		this.name = catname;
	}

	/**
	 * Returns the ID of the Category.
	 *
	 * @return ID of the Category
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Sets the ID of the category.
	 *
	 * @param id ID of the category
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the parent category.
	 *
	 * @param parent parent category
	 */
	private void setParent(Category parent) {
		this.parent = parent;
	}

	/**
	 * Returns the parent category.
	 *
	 * @return parent category
	 */
	public Category getParent() {
		return this.parent;
	}

	/**
	 * Returns the name of the category.
	 *  
	 * @return name of the category
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the category.
	 *
	 * @param name name of the category
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Das aktuelle Objekt wird somit zum Vater der uebergebenen
	 * Kategorie. Objekt uebernimmt einen Sohn.
	 *
	 * @param cat neuer Sohn
	 */
	public void addChild(Category cat) {
		this.children.add(cat);
		cat.setParent(this);
	}

	/**
	 * @return liefert alle Kinder des Objekts.
	 */
	public Set<Category> getChildren() {
		return this.children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}

	/**
	 * Gibt alle Vorfahren in sortierter Reihenfolge eines Knotens an.
	 *
	 * @return Vorfahren enumeriert.
	 */
	public List<Category> getAncestors() {
		ArrayList<Category> list = new ArrayList<Category>();
		for (Category cat = this.getParent(); cat != null; cat = cat.getParent()) {
			list.add(cat);
		}

		return list;
	}

	public String toString() {
		return name;
	}
}
