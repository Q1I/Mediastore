package media.definitions;

import java.util.List;
import java.util.Properties;

/**
 * Schnittstelle, die von der Zugriffsschicht auf die Media-Datenbank
 * implementiert werden muss.
 */
public interface MediaDbInterface {

	/**
	 * Initialisierung der Zugriffsschicht. Das Property-Objekt enthaelt alle
	 * notwendigen Parameter.
	 *
	 * @param prop Property-Objekt mit Attribut-Wert-Paaren, die zur
	 *             Initialisierung dienen (Login-Name, Passwort,...).
	 */
	public void init(Properties prop);

	/**
	 * Diese Methode wird bei Beendigung der Anwendung aufgerufen. Hier koennen
	 * alle Ressourcen freigegeben werden (Verbindung usw.)
	 */
	public void finish();

	/**
	 * Gibt eine Liste von Produkten (reiner Typ <code>Product</code> ausreichend) zurueck,
	 * deren Titel auf das <code>namePattern</code> passt.
	 *
	 * @param namePattern SQL-LIKE-Praedikat
	 * @return Liste der Produkte
	 */
	public List<Product> getProducts(String namePattern);

	// -- Direkter Datenbankzugriff per SQL -------------------------------------
	/**
	 * Fuehrt die gegebene SQL-Operation auf der Datenbank aus und liefert das
	 * Ergebnis in einem SQLResult-Objekt aufbereitet zurueck.<br>
	 * <p/>
	 * Achtung: es kann sich bei der Operation sowohl um eine Anfrage als auch
	 * um eine Aenderungsoperation (insert, drop, ...) handeln. Dieses muss
	 * beruecksichtigt werden (Hinweis: Statement.execute()). Je nach Art der
	 * Operation wird das Anfrageergebnis oder numerische Rueckgabewert der
	 * Aenderungsoperation von der Methode zurueckgegeben.
	 *
	 * @param query die auszufuehrende Operation
	 * @return das Ergebnis der Operation als SQLResultInterface-Objekt;
	 *         entweder das Anfrageergebnis oder der numerische Wert bei einer
	 *         Update-Operation
	 * @throws Exception wenn waehrend der Ausfuehrung der Anfrage ein Fehler
	 *                   auftrat (z.B. java.sql.SQLException)
	 */
	public SQLResult executeSqlQuery(String query) throws Exception;

	// -- Direkter Datenbankzugriff per HQL ------------------------------------
	/**
	 * Fuehrt einen gegebenen HQL-Query auf der Datenbank aus und liefert das
	 * Ergebnis in einem SQLResult-Objekt aufbereitet zurueck.<br>
	 * <p/>
	 * Achtung: nur select-Queries muessen an dieser Stelle unterstuetzt werden.
	 * <p/>
	 * Bei Anfragen wie "from Category" sollten alle Attribute des Objektes von
	 * primitivem Typ (int, long, float, String) ausgelesen und zurueck gegeben werden.
	 * Wenn <code>deref</code> auf <code>true</code> gesetzt ist, so sollten auch die
	 * nicht-primitiven Attribute per <code>toString()</code> ausgelesen werden.
	 * <p/>
	 * Beispiele:
	 *  - Das Ergebnis fuer "select name, parent from Category" sollte 2 Spalten besitzen.
	 * 		Falls <code>deref</code> auf <code>true</code> gesetzt ist, steht in der
	 * 		2. Spalte (aufgrund von toString()) der Name der der Vaterkategorie.
	 *
	 * @param query HQL-Query
	 * @param deref gibt an, ob f�r nicht primitive Typen der Inhalt der Objekte per
	 * 				<code>toString</code>-Methode zurck gegeben werden soll
	 * @return das Ergebnis der Operation als SQLResult-Objekt
	 */
	public SQLResult executeHqlQuery(String query, boolean deref);

	/**
	 * Gibt die Wurzel des Kategorienbaumes zurueck.
	 * Aus Performance-Gruenden sollten die Unterkategorien erst bei Bedarf geladen werden.
	 *
	 * @return Wurzel des Kategorienbaumes
	 */
	public Category getCategoryTree();
	
	public Category getCategoryTree(String catID);

	/**
	 * Gibt die Produkte (reiner Typ <code>Product</code> ausreichend) zu einem gegebenen
	 * Kategorienpfad zurueck.
	 *
	 * @param categoriesPath Kategorienpfad
	 * @return Liste der Produkte zu dem Kategorienpfad
	 */
	public List<Product> getProductsByCategoryPath(Category[] categoriesPath);

	/**
	 * Gibt alle Produkte (reiner Typ <code>Product</code> ausreichend) in einer Liste zurueck,
	 * die mindestens eine Produktbewertung besitzen.
	 *
	 * @return Liste der Produkte mit Bewertungen
	 */
	public List<Product> getReviewProducts();

	/**
	 * Fuegt einen neue Produktbewertung hinzu. Die Bewertung soll in der Datenbank gespeichert
	 * werden. Weiterhin soll das Produkt zu der Bewertung danach aktualisiert werden.
	 *
	 * @param review Produktbewertung
	 */
	public void addNewReview(Review review);

	/**
	 * Liefert das Produkt (reiner Typ <code>Product</code> ausreichend) mit gegebener Produkt-ID.
	 *
	 * @param id Produkt-ID
	 * @return Produkt mit der Produkt-ID
	 */
	public Product getProduct(String id);

	/**
	 * Liefert die DVD mit gegebener Produkt-ID.
	 *
	 * @param id Produkt-ID
	 * @return DVD mit der Produkt-ID
	 */
	public DVD getDVD(String id);

	/**
	 * Liefert die Musik-CD mit gegebener Produkt-ID.
	 *
	 * @param id Produkt-ID
	 * @return Musik-CD mit der Produkt-ID
	 */
	public Music getMusic(String id);

	/**
	 * Liefert das Buch mit gegebener Produkt-ID.
	 *
	 * @param id Produkt-ID
	 * @return Buch mit der Produkt-ID
	 */
	public Book getBook(String id);

	/**
	 * Gibt alle Angebote zu einem Produkt zur�ck.
	 *
	 * @param product Produkt
	 * @return Angebote zu dem Produkt
	 */
	public List<Offer> getOffers(Product product);
}
