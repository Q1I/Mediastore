/**
 * Implementation des Interfaces MediaDbInterface. Dabei handelt es sich
 * um eine Middleware um das Frontend mit Daten aus dem Backend zu versorgen.
 * @author Silvio Paschke
 * @author Alrik Hausdorf (Aenderung zu voll funktionstuechtiger TestImplementierung) (10/2011)
 * @since 02/06
 */
package media.mediadbimpl;

import media.definitions.*;
import media.definitions.Product.Type;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;




public class DummyImpl implements MediaDbInterface {

	private String url;
	private String password;
	private String userName;
	private String driver;
	
	private Properties prop = null;

	private List<Product> pKatList = new ArrayList<Product>();
	private List<ProduktKategorie> prodKatList = new ArrayList<ProduktKategorie>();

	private Category category = null;

	private List<Product> products = new ArrayList<Product>();

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	Product Dvd, Musik, Buch;

	/**
	 * Konstruktor
	 */
	public DummyImpl() {
		

	}

	/**
	 * Initialisierung der Zugriffsschicht. Das Property-Objekt enthaelt alle
	 * notwendigen Parameter.
	 * 
	 * @param prop
	 *            Property-Objekt mit Attribut-Wert-Paaren, die zur
	 *            Initialisierung dienen (Login-Name, Passwort,...).
	 */
	public void init(Properties prop) {
		System.out.println("LOG");
		Logger.getLogger("org.hibernate").setLevel(Level.ALL);

		this.prop = prop;

		System.out.println("Einstellungen:");
		prop.list(System.out);
		
		Configuration config = new Configuration();
		Properties props = config.configure(prop.getProperty("hibernate")).getProperties();
		this.userName=props.getProperty("hibernate.connection.username");
		this.password = props.getProperty("hibernate.connection.password");
		this.url =  props.getProperty("hibernate.connection.url");
		this.driver =  props.getProperty("hibernate.connection.driver_class");
		
		System.out.println("blaaaaa: "+userName);
		System.out.println("blaaaaa: "+password);
		System.out.println("blaaaaa: "+url);
		System.out.println("blaaaaa: "+driver);
		/*
		 * Herstellung der Datenbankverbindung.
		 */
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure(
					prop.getProperty("hibernate")).buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		// // start the session
		// Session session = getSessionFactory().openSession();
		// Transaction transaction = session.beginTransaction();
		//
		// SQLQuery query = session.createSQLQuery("select * from Produkt");
		// ScrollableResults results = query.scroll();
		// while (results.next()) {
		// for (Object value : results.get()) {
		// System.out.print((value == null ? "null" : value.toString()) + '\t');
		// }
		// System.out.println();
		// }
		// // close session
		// transaction.commit();
		// session.close();

		System.out.println("Prop: " + prop.toString());
		System.out.println("Prop22: " + prop.getProperty("hibernate"));
//		test7();
//		getProducts("%");
		// getReviewProducts();

	}

	/**
	 * Connects to database
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {
		System.out.println("Connecting to database..");
		Connection con = null;
		try {
			Class.forName(this.driver); 
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(this.url, this.userName,
					this.password);
			// con = DriverManager.getConnection(this.url);
			System.out.println("Connected to database");
		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return con;
	}

	/**
	 * Diese Methode wird bei Beendigung der Anwendung aufgerufen. Hier koennen
	 * alle Ressourcen freigegeben werden (Verbindung usw.)
	 */
	public void finish() {

		try {
			if (sessionFactory != null)
				sessionFactory.close();
		} catch (Exception exCl) {
		}

	}


	/**
	 * Gibt das Ergebnis der Anfrage zurueck.
	 * 
	 * @param query
	 *            enthaelt die Anfrage
	 * @return Rueckgabeobjekt
	 */
	public SQLResult executeSqlQuery(String query) throws Exception {

		SQLResult sqlRes = new SQLResult();
		
		System.out.println("SQL Query: " + query);
		
		if(query.length()==0){
			System.out.println("ERROR SQL: Empty String!");
			List<String[]> body = new ArrayList<String[]>();
			String[] dummy = new String[2];
			dummy[0] = "ERROR SQL: ";
			dummy[1] = "Empty Query!";
			body.add(dummy);
			sqlRes.setBody(body);
			String[] head = new String[2];
			head[0] = "Error";
			head[1] = "";
			sqlRes.setHeader(head);
			return sqlRes;
		}
		
		if (checkSQL(query))
			sqlRes = executeQuery(query);
		else
			sqlRes = executeUpdate(query);

		return sqlRes;
	}

	private SQLResult executeUpdate(String query) throws SQLException {
		System.out.println("EXECUTE UPDATE");

		SQLResult sqlRes = new SQLResult();
		List<String[]> body = new ArrayList<String[]>();
		String[] dummy = new String[2];

		if (checkUpdate(query) == false) {
			System.out.println("ERROR SQL: no permission");
			dummy[0] = "ERROR SQL: ";
			dummy[1] = "You don't have permission for this operation!";
			if (query == null)
				dummy[1] = "Empty Query!";

			body.add(dummy);
			String[] head = new String[2];
			head[0] = "ERROR";
			head[1] = "";
			sqlRes.setHeader(head);
			sqlRes.setBody(body);
			return sqlRes;

		}

		String[] head = new String[2];
		head[0] = "Result";
		head[1] = "";
		sqlRes.setHeader(head);
		Connection con = getConnection();

		int colNum = 0;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			int row = stmt.executeUpdate(query);
			// sqlRes.setHeader(columns);

			dummy[0] = "Numbers of row changed => ";
			dummy[1] = row + "";
			body.add(dummy);
			sqlRes.setBody(body);

		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			dummy[0] = "ERROR SQL: ";
			dummy[1] = e.getMessage();
			body.add(dummy);
			sqlRes.setBody(body);
			head[0] = "Error";
			head[1] = "";
			sqlRes.setHeader(head);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		return sqlRes;
	}

	private boolean checkUpdate(String query) {
		String s = query.replaceFirst("(\\s+)(insert)", "insert");
		if (s.startsWith("insert"))
			return true;
		return false;
	}

	private SQLResult executeQuery(String query) throws SQLException {
		System.out.println("EXECUTE QUERY");
		SQLResult sqlRes = new SQLResult();
		Connection con = getConnection();

		int colNum = 0;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(query);
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
			colNum = rsmd.getColumnCount();
			System.out.println("colnum: " + colNum);
			String[] columns = new String[colNum];
			System.out.println("Get names!");
			for (int i = 0; i < colNum; i++) {
				System.out.println(i + ".attempt");
				columns[i] = rsmd.getColumnName(i + 1);
				if (columns[i].equals("Preis"))
					columns[i] = "Preis (mult 0.01)";

				System.out.println(i + ".cols: " + columns[i]);
			}
			sqlRes.setHeader(columns);

			List<String[]> data = new ArrayList<String[]>();
			ResultSet rs2 = (ResultSet) stmt.executeQuery(query);
			int k = 0;
			while (rs2.next()) {
				String[] res = new String[colNum];
				k = 0;
				for (int i = 0; i < colNum; i++) {
					System.out.print((rs2.getObject(i + 1) == null ? "null"
							: rs2.getObject(i + 1).toString()) + '\t');
					res[k] = ((rs2.getObject(i + 1) == null ? "null" : rs2
							.getObject(i + 1).toString()));

					k++;
				}
				System.out.println("");
				data.add(res);
			}
			sqlRes.setBody(data);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			throw new SQLException(e.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		// Session session = sessionFactory.openSession();
		// Transaction transaction = session.beginTransaction();
		// SQLQuery q = session.createSQLQuery(query);
		//
		// ScrollableResults results = q.scroll();
		// int k = 0;
		// while (results.next()) {
		// String[] res = new String[colNum];
		// k=0;
		// for (Object value : results.get()) {
		// System.out
		// .print((value == null ? "null" : value.toString()) + '\t');
		// res[k] = ((value == null ? "null" : value.toString()));
		//
		// k++;
		// }
		// System.out.println("");
		// data.add(res);
		// }
		// sqlRes.setBody(data);

		return sqlRes;
	}

	private boolean checkSQL(String query) {
		String s = query.replaceFirst("(\\s+)(select)", "select");
		if (s.startsWith("select"))
			return true;
		return false;
	}

	/**
	 * Gibt das Ergebnis der Anfrage zurueck.
	 * 
	 * @param query
	 *            enthaelt die Anfrage
	 * @param deref
	 * @return Rueckgabeobjekt
	 */
	public SQLResult executeHqlQuery2(String query, boolean deref) {
		SQLResult sqlRes = new SQLResult();
		System.out.println("Query: " + query);
		Session sess = null;
		Transaction trx = null;
		Product prod = null;
		List<String> head = new ArrayList<String>();
		List<String> bodyRow = new ArrayList<String>();
		List<String[]> body = new ArrayList<String[]>();
		String[] finalBodyRow = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query q = sess.createQuery(query);
			q.setMaxResults(40);
			Iterator itr = q.iterate();
			while (itr.hasNext()) {
				Object i = itr.next();

				System.out.println(i.getClass());
				System.out.println("toString: " + i.toString());
				if (i instanceof Product) { // Product
					System.out.println("Product");
					Product p = (Product) i;

					head.clear();
					head.add("Produkt_ID");
					head.add("Titel");
					head.add("Verkaufsrang");
					head.add("Bild");
					System.out.println("headder did");

					bodyRow.clear();
					bodyRow.add(p.getAsin());
					bodyRow.add(p.getTitle());
					bodyRow.add(p.getSalesRank() + "");
					bodyRow.add(p.getPicUrl());
					System.out.println("body did");
					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
						System.out.println(j + ".body: " + finalBodyRow[j]);
					}

					if (i instanceof Book) {
						System.out.println("Book");
					} else if (i instanceof Music)
						System.out.println("Music");
					else if (i instanceof DVD)
						System.out.println("DVD");
				} else if (i instanceof Review) { // Review
					System.out.println("Review");
					Review p = (Review) i;
					if (head != null)
						head = new ArrayList<String>();
					head.add("R_ID");
					head.add("Produkt_ID");
					head.add("Kunden_ID");
					head.add("Rezension");
					head.add("Inhalt");

					bodyRow.clear();
					bodyRow.add(p.getId().toString());
					bodyRow.add(p.getProduct().getAsin());
					bodyRow.add(p.getUsername().getId());
					bodyRow.add(p.getSummary());
					bodyRow.add(p.getContent());

					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
					}
				} else if (i instanceof Offer) { // Offer
					System.out.println("Offer");
					Offer p = (Offer) i;
					if (head != null)
						head = new ArrayList<String>();
					head.add("Filial_ID");
					head.add("Produkt_ID");
					head.add("Zustand");
					head.add("Preis");

					bodyRow.clear();
					bodyRow.add(p.getLocation());
					bodyRow.add(p.getProduct().getAsin());
					bodyRow.add(p.getState());
					bodyRow.add(p.getPrice() + "");

					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
					}
				} else if (i instanceof Category) { // Category
					System.out.println("Category");
					Category p = (Category) i;
					if (head != null)
						head = new ArrayList<String>();
					head.add("Kategorie_ID");
					head.add("Name");
					head.add("Oberkategorie_ID");

					bodyRow.clear();
					bodyRow.add(p.getId());
					bodyRow.add(p.getName());
					bodyRow.add(p.getParent().getId());

					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
					}
				} else if (i instanceof Person) { // Person
					System.out.println("Person");
					Person p = (Person) i;
					if (head != null)
						head = new ArrayList<String>();
					head.add("Name");
					head.add("Rolle");
					head.add("Produkt_ID");

					bodyRow.clear();
					bodyRow.add(p.getName());
					bodyRow.add(p.getRole());
					bodyRow.add(p.getProduct().getAsin());

					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
					}
				}
				body.add(finalBodyRow);

			}
			trx.commit();
			String[] finalHead = new String[head.size()];
			for (int i = 0; i < head.size(); i++) {
				finalHead[i] = head.get(i);
				System.out.println(i + ".head: " + finalHead[i]);
			}
			sqlRes.setHeader(finalHead);
			sqlRes.setBody(body);

		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}
		System.out.println("HQL done!");
		return sqlRes;

	}

	public boolean isWrapperType(String clazz) {
		HashSet<String> ret = new HashSet<String>();
		ret.add(Boolean.class.getName());
		ret.add(Character.class.getName());
		ret.add(Byte.class.getName());
		ret.add(Short.class.getName());
		ret.add(Integer.class.getName());
		ret.add(Long.class.getName());
		ret.add(Float.class.getName());
		ret.add(Double.class.getName());
		ret.add(Void.class.getName());
		ret.add("string");
		ret.add("integer");
		ret.add("double");
		ret.add("float");
		return ret.contains(clazz);
	}

	public SQLResult executeHqlQuery(String query, boolean deref) {

		SQLResult sqlRes = new SQLResult();
		System.out.println("Query: " + query);
		
		if(query.length()==0){
			System.out.println("ERROR HQL: Empty String!");
			List<String[]> body = new ArrayList<String[]>();
			String[] dummy = new String[2];
			dummy[0] = "ERROR HQL: ";
			dummy[1] = "Empty Query!";
			body.add(dummy);
			sqlRes.setBody(body);
			String[] head = new String[2];
			head[0] = "Error";
			head[1] = "";
			sqlRes.setHeader(head);
			return sqlRes;
		}
		
		Session sess = null;
		Transaction trx = null;
		List<String> bodyRow = new ArrayList<String>();
		List<String[]> body = new ArrayList<String[]>();
		String[] finalBodyRow = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query hqlQuery = sess.createQuery(query);
			hqlQuery.setMaxResults(200);

			org.hibernate.type.Type singleType = hqlQuery.getReturnTypes()[0];
			if (singleType.isAssociationType()) { // Object
				System.out
						.println("Der erste Rückgabewert ist ein Objekt vom Typ "
								+ singleType.getName());
				ClassMetadata metadata = sessionFactory
						.getClassMetadata(singleType.getName());
				
				String[] propertyNames = metadata.getPropertyNames();
//				for (int i = 0; i < propertyNames.length; i++) {
//					System.out.println(i + ".propNames: " + propertyNames[i]);
//				}
				String[] header = new String[propertyNames.length+1];
				for(int j =0; j<propertyNames.length;j++){
					header[j]=propertyNames[j];
				}
				System.out.println("#############header id: "+metadata.getIdentifierPropertyName());
				System.out.println("#############header id tpye: "+metadata.getIdentifierType());
				String primHead =metadata.getIdentifierPropertyName();
				if(primHead==null)
					primHead="primary";
				header[header.length-1]=primHead;
				System.out.println("Print headerName:");
				for(int j =0; j<header.length;j++){
					System.out.println(j+".headerName: "+header[j]);
				}
				sqlRes.setHeader(header);
				System.out.println("@@@@@@@@@@@@@@@@@@@@@header done!");

				System.out.println("get body:");
				org.hibernate.type.Type[] propertyTypes = metadata
						.getPropertyTypes();
				System.out.println("propertyTypes size: "+propertyTypes.length);
				List li = hqlQuery.list();
				System.out.println("debug lis size: "+li.size());
				Iterator results = li.iterator();
				int k = 0;
				while (results.hasNext()) {
					k = 0;
					bodyRow.clear();
					Object value = results.next(); // object
					System.out.println("value: "+value.toString());
					Object[] values = metadata.getPropertyValues(value, EntityMode.POJO);
					System.out.println("values suze: "+values.length);
					
					
					for (int l=0;l<values.length;l++) {
						if (isWrapperType(propertyTypes[l].getName())) {
//							System.out.println("is primitive!!!!!");
							if(values[l] != null)
								bodyRow.add(values[l].toString());
							else
								bodyRow.add("null");
						} else {
//							System.out.println("is complexxx!!!!!");
							if (deref){
								if(values[l] != null)
									bodyRow.add(values[l].toString());
								else
									bodyRow.add("null");
							}else
								bodyRow.add("<lazy>");
						}
						k++;
					}
					// primary value
					String primValue =metadata.getIdentifier(value, EntityMode.POJO).toString();
					System.out.println("#############body primary: "+primValue);
					bodyRow.add(primValue);
					
//					System.out.println("id type: "+metadata.getIdentifierType());
//					System.out.println("id val: "+metadata.getIdentifier(value, EntityMode.POJO));
//					System.out.println("Print finalBodyRow: "+bodyRow.size());
					finalBodyRow = new String[bodyRow.size()];
					
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
						System.out.println(j + ".body: " + finalBodyRow[j]);
					}
					body.add(finalBodyRow);
					
				}

			} else {
				System.out
						.println("Der erste Rückgabewert ist primitiven Typs");
				String[] aliases = hqlQuery.getReturnAliases();
				for (int i = 0; i < aliases.length; i++) {
					System.out.println(i + ".propNames: " + aliases[i]);
				}
				sqlRes.setHeader(aliases);

				ScrollableResults results = hqlQuery.scroll();
				while (results.next()) {
					bodyRow.clear();
					System.out.println("debug: "+results.get());
					for (Object value : results.get()) {
						System.out.print((value == null ? "null" : value
								.toString()) + '\t');

						bodyRow.add(value.toString());
					}
					System.out.println();
					
					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
						System.out.println(j + ".body: " + finalBodyRow[j]);
					}
					body.add(finalBodyRow);
				}
				
			}
			sqlRes.setBody(body);
			trx.commit();

		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}
		System.out.println("HQL done!");
		return sqlRes;

	}

	/**
	 * Diese Methode liefert eine Liste mit Produkten, die dem Pattern
	 * entsprechen. Der Pattern soll ein Teil des Titels sein. Attribute: Id,
	 * Title, ProduktGroup
	 * 
	 * @param namePattern
	 *            SQL-Pattern im Sinne des SQL-Like
	 * @return Liste mit den Produkten, die dem Pattern entsprechen
	 */
	public synchronized List<Product> getProducts(String namePattern) {
		products=new ArrayList<Product>();
		String patternQuery = null;
		if (namePattern == null || namePattern.isEmpty() || namePattern.equals("%"))
			patternQuery = "from Product";
		else {
			patternQuery = "from Product where Titel like :namePattern";
		}
		System.out.println("Get Products: " + patternQuery);
		System.out.println("namePattern = "+namePattern);
		Session sess = null;
		Transaction trx = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			query.setMaxResults(150);
			if(namePattern.equals("%") == false)
				query.setParameter("namePattern", namePattern);
			// here
			Iterator itr = query.iterate();
			while (itr.hasNext()) {
				try{
				Product prod = (Product) itr.next();
				System.out.println("Product: " + prod.getAsin() + " Titel: "
						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
//						+ " review: " + prod.getReviews().size()
//						+ " Category: " + prod.getCategories().size()
						);
				products.add(prod);
				}
				catch(Exception e){
					System.out.println("ERROR querying products: "+e.getMessage());
					return products;
				}
			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
				
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return products;
	}

	public void test2() {
		String patternQuery = "from Review";

		System.out.println("\n\nTEEESST Get REEVIEW: " + patternQuery);
		Session sess = null;
		Transaction trx = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			List<Review> rev = query.list();
			System.out.println("rev size: " + rev.size());
			Iterator itr = query.iterate();
			while (itr.hasNext()) {
				Review prod = (Review) itr.next();
				System.out.println("Review: " + prod.getId() + " Prod: "
						+ prod.getProduct().getTitle() + " user: "
						+ prod.getUsername());
				// if(prod.getReviews().size()>0)
				// System.out.println( "Product: " + prod.getAsin()
				// + " Titel: " + prod.getTitle()
				// +"review: "+prod.getReviews().size() );
			}
			System.out.println("\n\n\n");
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

	}

//	public void test() {
//		Session sess = null;
//		Transaction trx = null;
//		try {
//
//			Product prod = new Product("1234as", 3.3f, 1, "test it", 3.50f,
//					"EU", true, "picURL bla", null, Product.Type.book);
//			Product p = new Product();
//			p.setAsin("1blbla");
//			p.setSalesRank(12);
//			p.setTitle("Titel");
//			p.setPicUrl("blablabla");
//			Review rev = new Review(prod, "tester", 3, 4, new Date(), "blue",
//					"content blue");
//
//			Product d = new Product(
//					"a1002TB60W",
//					new Float(5),
//					new Integer(1586),
//					"asdf Various Artists - Karaoke: Love Songs, Vol. 01",
//					new Float(16.48),
//					"EUR",
//					true,
//					"http://images.amazon.com/images/P/B0002TB60W.03._SCMZZZZZZZ_.jpg",
//					null, Product.Type.dvd);
//
//			System.out.println("PRINT: " + d.getAsin() + " , " + d.getTitle());
//			sess = sessionFactory.openSession();
//			trx = sess.beginTransaction();
//			// sess.save(rev);
//			System.out.println("save");
//			sess.save(d);
//			System.out.println("save done");
//			trx.commit();
//			System.out.println("DONE COMMIT");
//		} catch (HibernateException ex) {
//			if (trx != null)
//				try {
//					trx.rollback();
//				} catch (HibernateException exRb) {
//				}
//			throw new RuntimeException(ex.getMessage());
//		} finally {
//			try {
//				if (sess != null)
//					sess.close();
//			} catch (Exception exCl) {
//			}
//		}
//	}

	public void test3() {
		String patternQuery = null;
		List<Product> productList = new ArrayList<Product>();
		System.out.println("Get Person Products: " + patternQuery);
		Session sess = null;
		Transaction trx = null;
		try {
			int count = 0;
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			String hql = "from Product";
			Query query = sess.createQuery(hql);
			products = query.list();
			System.out.println("products size: " + products.size());
			// for(int i=0;i<products.size();i++){
			// System.out.println(i+".product: "+products.get(i).getTitle());
			// }
			Iterator itr = query.iterate();
			while (itr.hasNext()) {
				Product prod = (Product) itr.next();
				System.out.println("Product: " + prod.getAsin() + " Titel: "
						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
						+ " review: " + prod.getReviews().size());
				Iterator itr2 = prod.getReviews().iterator();
				while (itr2.hasNext()) {
					Review rev = (Review) itr2.next();
					System.out
							.println("HASS REVIEWWW!!!: " + rev.getUsername());
				}
				// add to products
				if (prod.getReviews().size() > 0) {
					count++;
					System.out.println("Add Product with review");
					productList.add(prod);
					System.out.println("Product: " + prod.getAsin()
							+ " Titel: " + prod.getTitle() + "review: "
							+ prod.getReviews().size());
				}
			}
			trx.commit();
			System.out.println("Num Products with reviews = " + count);
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}
	}

	public void test4() {
		String patternQuery = "from Category";

		System.out.println("\n\nGet Category: " + patternQuery);
		Session sess = null;
		Transaction trx = null;
		Category prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			List<Category> rev = query.list();
			// System.out.println("cat size: "+rev.size());
			Iterator itr = query.iterate();
			while (itr.hasNext()) {
				try {
					prod = (Category) itr.next();
					System.out.println("Category: " + prod.getId() + " name: "
							+ prod.getName() + " parent: " + prod.getParent()
							+ " children: " + prod.getChildren().size());
					if (prod.getChildren().size() > 0) {
						Iterator it = prod.getChildren().iterator();
						int i = 0;
						while (it.hasNext()) {
							System.out.println(i + ".child: " + it.next());
							i++;
						}
					}
				} catch (java.util.NoSuchElementException ex) {
					System.out.println("prod: " + prod.getChildren().size());
					System.out.println("Error: " + ex.getMessage());

					if (prod.getName().equals("null"))
						System.out.println("Null category");
					else
						throw ex;
				}
				// if(prod.getReviews().size()>0)
				// System.out.println( "Product: " + prod.getAsin()
				// + " Titel: " + prod.getTitle()
				// +"review: "+prod.getReviews().size() );
			}
			System.out.println("\n\n\n");
			for (int i = 0; i < rev.size(); i++) {
				if (rev.get(i).getName().equals("null")) {
					System.out.println("NULLL!!!!");
					System.out.println("Size: "
							+ rev.get(i).getChildren().size());
					Iterator it = rev.get(i).getChildren().iterator();
					int j = 0;
					while (it.hasNext()) {
						Category cat = (Category) it.next();
						System.out.println(j + ".child: " + cat.getName());
						j++;
						int k = 0;
						Iterator it2 = cat.getChildren().iterator();
						while (it2.hasNext()) {
							Category cat2 = (Category) it2.next();
							System.out.println(">>>>" + k + ".child: "
									+ cat2.getName());
							k++;

						}
					}
				}
			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

	}

	public void test5() {
		String namePattern = null;
		String patternQuery = null;
		patternQuery = "from Product";

		List<Product> products = new ArrayList<Product>();
		System.out.println("Get Products: " + patternQuery);
		Session sess = null;
		Transaction trx = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
//			products = query.list();
			System.out.println("userList size: " + products.size());
			Iterator itr = query.iterate();
			while (itr.hasNext()) {
				Product prod = (Product) itr.next();
				System.out.println("Product: " + prod.getAsin() + " Titel: "
						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
						+ " review: " + prod.getReviews().size()
						+ " Category: " + prod.getCategories().size());

			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

	}

	public void test6() {
		String namePattern = null;
		String patternQuery = null;
		patternQuery = "from Offer";

		List<Product> products = new ArrayList<Product>();
		System.out.println("Get Offer: " + patternQuery);
		Session sess = null;
		Transaction trx = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			products = query.list();
			System.out.println("userList size: " + products.size());
			Iterator itr = query.iterate();
			while (itr.hasNext()) {
				Offer prod = (Offer) itr.next();
				System.out.println("Loc: " + prod.getLocation() + " Titel: "
						+ prod.getProduct().getAsin() + " Price: "
						+ prod.getPrice() + " state: " + prod.getState()
						+ " curr: " + prod.getCurrency());

			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

	}

	
	public void test7(){
		String user ="01test";
		Session sess = null;
		Transaction trx = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			String q = "from Kunde where K_ID = :id";
			Query query = sess.createQuery(q);
			List<Kunde> kList = query.setParameter("id", user).list();
			System.out.println("kList size: "+kList.size());
			// here
			if (kList.size()>0) {
				Kunde k = kList.get(0);
				System.out.println("Found kunde: "+k.getId());
			}
			else{
				System.out.println("Kunde nicht gefunden! Neuen Kunden anlegen! K_ID = "+user);
				Kunde newK = new Kunde(user);
				addUser(newK);
				
			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
				
			throw new RuntimeException(ex.getMessage());
		} catch (SQLException e) {
			System.out.println("ERROR SQL: "+e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (sess != null){
					sess.flush();
					sess.close();
				}
			} catch (Exception exCl) {
			}
		}
	}
	
	private void addUser(Kunde newK) throws SQLException {
		Connection con = getConnection();
		String query ="Insert into dbprak20.vKunde(K_ID) values ('"+newK.getId()+"')";
		System.out.println("Insert User query: "+query);
		int colNum = 0;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(query);
			System.out.println("Add user done!");
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			throw new SQLException(e.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	private void addReview(Review newK) throws SQLException {
		Connection con = getConnection();
		String query ="Insert into dbprak20.vReview (Produkt_ID,Kunden_ID,Punkte,Rezension,Inhalt) values " +
				"('"+newK.getProduct().getAsin()+"' , '"+
				newK.getUsername().getId()+"', "+
				newK.getRating()+", '"+
				newK.getSummary()+"' , '"+
				newK.getContent()+"')";
		System.out.println("Insert Review Query: "+query);
		int colNum = 0;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(query);
			System.out.println("Add Review done!");
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			throw new SQLException(e.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Liefert diejenigen Produkte, denen auch tatsaechlich ein Review
	 * zugeordnet ist.
	 */
	public synchronized List<Product> getReviewProducts() {
		String patternQuery = null;
		List<Product> productList = new ArrayList<Product>();
		System.out.println("Get Review Products: ");
		Session sess = null;
		Transaction trx = null;
		try {
			int count = 0;
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			String hql = "from Product as p where p.reviews is not empty";
			Query query = sess.createQuery(hql);
			products= query.list();
			System.out.println("productList size: "+products.size());
			Iterator itr = query.iterate();
//			while (itr.hasNext()) {
//				try{
//				Product prod = (Product) itr.next();
//				System.out.println("Review Product: " + prod.getAsin() + " Titel: "
//						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
//						+ " review: " + prod.getReviews().size());
//				
//				Product p = new Product(prod);
//				p.setReviews(prod.getReviews());
//				productList.add(p);
//				}
//				catch(Exception e){
//					System.out.println("ERROR getting review product: "+e.getMessage());
//					return productList;
//				}
////				Iterator itr2 = prod.getReviews().iterator();
////				while (itr2.hasNext()) {
////					Review rev = (Review) itr2.next();
////					// System.out.println("HASS REVIEWWW!!!: "+rev.getUsername());
////				}
////				// add to products
////				if (prod.getReviews().size() > 0) {
////					count++;
////					// System.out.println("Add Product with review");
////					productList.add(prod);
////					// System.out.println( "Product: " + prod.getAsin()
////					// + " Titel: " +
////					// prod.getTitle()+"review: "+prod.getReviews().size() );
////				}
//			}
			trx.commit();
			// System.out.println("Num Products with reviews = "+count);
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return products;
		// return null;
	}

	/**
	 * fuegt ein neues Review ein. Die Daten sind dabei aus dem Objekt zu lesen
	 * und in der Datenbank einzufuegen.
	 * 
	 * @param review
	 *            Reviewdaten.
	 */
	public synchronized void addNewReview(Review review) {
		Review newRev = new Review();
		
		String user = review.getUsername().getId();
		String summar = review.getSummary();
		String cont = review.getContent();
		int rat = review.getRating();
		Product p = getProduct(review.getProduct().getAsin());
		
		newRev.setKundenName(user);
		newRev.setSummary(summar);
		newRev.setContent(cont);
		newRev.setProduct(p);
		newRev.setRating(rat);
		
		System.out.println("ADD REVIEW: ");
		System.out.println("user: "+newRev.getUsername().getId());
		System.out.println("sum: "+newRev.getSummary());
		System.out.println("cont: "+newRev.getContent());
		System.out.println("rat: "+newRev.getRating());
		System.out.println("prod: "+newRev.getProduct().getAsin());
		
		System.out.println("Kunden checken!");
		
		// ist der User bestehendes Mitglied? ja: ermittle customerid, nein:
		// user neu anlegen

		Session sess = null;
		Transaction trx = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			String q = "from Kunde where K_ID = :id";
			Query query = sess.createQuery(q);
			List<Kunde> kList = query.setParameter("id", user).list();
			System.out.println("kList size: "+kList.size());
			// here
			Iterator itr = query.iterate();
			if (kList.size()>0) {
				Kunde k = kList.get(0);
				System.out.println("Found kunde: "+k.getId());
			}
			else{
				System.out.println("Kunde nicht gefunden! Neuen Kunden anlegen! K_ID = "+user);
				Kunde newK = new Kunde(user);
				addUser(newK);
			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
				
			throw new RuntimeException(ex.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL Error insert kunde: "+e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}
		
		
		// speichere daten in der datenbank
		System.out.println("Review speichern!");
		try {
			addReview(newRev);
		} catch (SQLException e) {
			System.out.println("SQL ERROR INsert review: "+e.getMessage());
			e.printStackTrace();
		}
		
		
//		 sess = null;
//		 trx = null;
//		try {
//
//			sess = sessionFactory.openSession();
//			trx = sess.beginTransaction();
//			System.out.println("save");
//			sess.save(newRev);
//			System.out.println("save done");
//			trx.commit();
//			System.out.println("DONE COMMIT");
//		} catch (HibernateException ex) {
//			if (trx != null)
//				try {
//					trx.rollback();
//				} catch (HibernateException exRb) {
//				}
//				System.out.println("DEBUG ERROR: "+ex.getMessage());
////			throw new RuntimeException(ex.getMessage());
//		} finally {
//			try {
//				if (sess != null)
//					sess.close();
//			} catch (Exception exCl) {
//			}
//		}

	}

	/**
	 * Gibt die Wurzel des Kategorienbaumes zurueck.
	 * 
	 * @return Kategorienbaum
	 */
	public synchronized Category getCategoryTree() {

		String patternQuery = "select distinct p from Category as p";

		System.out.println("\n\nGet Category: " + patternQuery);
		Session sess = null;
		Transaction trx = null;
		Category prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			List<Category> rev = query.list();
			 System.out.println("cat size: "+rev.size());
//			Iterator itr = rev.iterator();
//			while (itr.hasNext()) {
//				try {
//					prod = (Category) itr.next();
//					System.out.println("Category: " + prod.getId() + " name: "
//							+ prod.getName() + " parent: " + prod.getParent()
//							+ " children: " + prod.getChildren().size());
//					if (prod.getChildren().size() > 0) {
//						Iterator it = prod.getChildren().iterator();
//						int i = 0;
//						while (it.hasNext()) {
//							System.out.println(i + ".child: " + it.next());
//							i++;
//						}
//					}
//				} catch (java.util.NoSuchElementException ex) {
//					System.out.println("prod: " + prod.getChildren().size());
//					System.out.println("Error: " + ex.getMessage());
//
//					if (prod.getName().equals("null"))
//						System.out.println("Null category");
//					else
//						throw ex;
//				}
//			}
			System.out.println("\n\n\n");
			// here
			for (int i = 0; i < rev.size(); i++) {
				if (rev.get(i).getId().equals("null_null_null")) {
					category = rev.get(i);
					System.out.println("NULLL!!!!");
					System.out.println("Size: "
							+ rev.get(i).getChildren().size());
//					Iterator it = rev.get(i).getChildren().iterator();
//					int j = 0;
//					while (it.hasNext()) {
//						Category cat = (Category) it.next();
//						System.out.println(j + ".child: " + cat.getName());
//						j++;
//						int k = 0;
//						Iterator it2 = cat.getChildren().iterator();
//						while (it2.hasNext()) {
//							Category cat2 = (Category) it2.next();
//							System.out.println(">>>>" + k + ".child: "
//									+ cat2.getName());
//							k++;
//
//						}
//					}

				}
			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return category;

	}

	
	public Category getCategoryTree(String catId) {

		System.out.println("Get Category ID = "+catId);
		String patternQuery = "from Category as c where c.id = :catID";

		System.out.println("\n\nGet Category: " + patternQuery);
		Session sess = null;
		Transaction trx = null;
		Category prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			List<Category> rev = query.setParameter("catID",catId).list();
			 System.out.println("cat size: "+rev.size());
			 category = rev.get(0); 
			 System.out.println("Size: "
						+ category.getChildren().size());
			System.out.println("\n\n\n");
			// here
//			for (int i = 0; i < rev.size(); i++) {
//				if (rev.get(i).getId().equals("null_null_null")) {
//					category = rev.get(i);
//					System.out.println("NULLL!!!!");
//					System.out.println("Size: "
//							+ rev.get(i).getChildren().size());
//
//				}
//			}
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return category;

	}
	
	
	/**
	 * Diese Methode liefert eine Liste mit Produkten vom Typ Product. Aufgrund
	 * des uebergebenen Pfades werden die Produkte ausgewaehlt. Hinweis:
	 * ueberlegen Sie sich, ob an dieser Stelle ein PreparedStatement sinnvoll
	 * ist und inwiefern dynamische Ansaetze bei der Query- Erstellung sinnvoll
	 * sind.
	 * 
	 * @param categoriesPath
	 *            Kategorien-Pfad
	 * @return Liste vom Typ Product
	 */
	public synchronized List<Product> getProductsByCategoryPath(
			Category[] categoriesPath) {
		pKatList.clear();
		/*
		 * Bestimmen Sie hier, welche Produkte zu dem Pfad gehoeren. Beachten
		 * Sie, dass "+" keine Kategorie ist. Diese dient nur der
		 * Zusammenfuehrung der Einzelbaeume.
		 */
		String id = categoriesPath[categoriesPath.length - 1].getId();
		String patternQuery = "from ProduktKategorie where Kategorie_ID = :id";
		System.out.println("Get ProductKategorie with = " + id);
		Session sess = null;
		Transaction trx = null;
		ProduktKategorie product = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			query.setParameter("id", id);
			prodKatList = query.setParameter("id", id).list();
			System.out.println("lsit size: " + prodKatList.size());
			for (int i = 0; i < prodKatList.size(); i++)
			// Iterator<ProduktKategorie> it = query.iterate();
			// while(it.hasNext())
			{
				System.out
						.println("blaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				ProduktKategorie prodKat = prodKatList.get(i);
				System.out.println("prodKAT: " + prodKat.getCategory());

				System.out.println("p: " + prodKat.getProduct());
				Product prod = getProduct(prodKat.getProduct());
				System.out.println("Prod infos: " + prod.getAsin());
				System.out.println("prod bla");
				Product p = new Product(prod);

				// String asin =prod.getAsin();
				// System.out.println("prod: "+asin);
				// System.out.println("Product: " + prod.getAsin() + " Titel: "
				// + prod.getTitle() + " Rank: " + prod.getSalesRank()
				// + " review: " +
				// prod.getReviews().size()+" cat: "+prod.getCategories().size());

				// prod = getProduct(asin);
				System.out.println("getProduct done");
				// System.out.println("GotProduct success --> Product: " +
				// prod.getAsin() + " Titel: "
				// + prod.getTitle() + " Rank: " + prod.getSalesRank()
				// + " review: " + prod.getReviews().size());

				pKatList.add(p);
				System.out.println("add done");

			}
			trx.commit();
			System.out.println("commit done");
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}
		// return (this.Dvd.getAsin().equals(id)) ? this.Dvd : (this.Musik
		// .getAsin().equals(id)) ? this.Musik : null;
		System.out.println("pkat LIst size : " + pKatList.size());
		for (int k = 0; k < pKatList.size(); k++) {
			Product p = pKatList.get(k);
			System.out.println(k + ". Product: " + p.getAsin() + " Titel: "
					+ p.getTitle() + " , cat: " + p.getCategories().size());
		}
		return pKatList;
	}

	/**
	 * Gibt zu einer Produkt-ID das passende Produkt zurueck.
	 * 
	 * @param id
	 *            Produktnummer
	 * @return passendes Produkt
	 */
	public Product getProduct(String id) {
		String patternQuery = "from Product where P_ID = :id";
		System.out.println("Get Product with id = " + id);
		Session sess = null;
		Transaction trx = null;
		Product prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			query.setParameter("id", id);
			Iterator itr = query.iterate();
			if (itr.hasNext()) {
				prod = (Product) itr.next();
				System.out.println("Product: " + prod.getAsin() + " Titel: "
						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
						+ " review: " + prod.getReviews().size());
			} else
				System.out.println("Product not found!");
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}
		System.out.println("Get product done!");
		return prod;

		// return (this.Dvd.getAsin().equals(id)) ? this.Dvd : (this.Musik
		// .getAsin().equals(id)) ? this.Musik : null;
	}

	/**
	 * Liefert die detailierten Buchdaten eines Produkts, sofern es sich um ein
	 * Buch handelt.
	 * 
	 * @param id
	 *            Produkt-ID
	 * @return Datenstruktur vom Typ Book
	 */
	public Book getBook(String id) {
		String patternQuery = "from Book where P_ID = :id";
		System.out.println("Get Book with id = " + id);
		Session sess = null;
		Transaction trx = null;
		Book prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			query.setParameter("id", id);
			Iterator itr = query.iterate();
			if (itr.hasNext()) {
				prod = (Book) itr.next();
				System.out.println("Product: " + prod.getAsin() + " Titel: "
						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
						+ " review: " + prod.getReviews().size() + " pub: "
						+ prod.getPublisher() + " authors: "
						+ prod.getAuthors().size());

			} else
				System.out.println("Product not found!");
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return prod;
	}

	/**
	 * siehe getBookData
	 */
	public DVD getDVD(String id) {
		String patternQuery = "from DVD where P_ID = :id";
		System.out.println("Get DVD with id = " + id);
		Session sess = null;
		Transaction trx = null;
		DVD prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			query.setParameter("id", id);
			Iterator itr = query.iterate();
			if (itr.hasNext()) {
				prod = (DVD) itr.next();
				System.out.println("Product: " + prod.getAsin() + " Titel: "
						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
						+ " review: " + prod.getReviews().size()
						+ " involved: " + prod.getPersons().size());

			} else
				System.out.println("Product not found!");
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return prod;
	}

	/**
	 * siehe getBookData
	 */
	public Music getMusic(String id) {
		String patternQuery = "from Music where P_ID = :id";
		System.out.println("Get Music with id = " + id);
		Session sess = null;
		Transaction trx = null;
		Music prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			query.setParameter("id", id);
			Iterator itr = query.iterate();
			if (itr.hasNext()) {
				prod = (Music) itr.next();
				System.out.println("Product: " + prod.getAsin() + " Titel: "
						+ prod.getTitle() + " Rank: " + prod.getSalesRank()
						+ " review: " + prod.getReviews().size() + " artist: "
						+ prod.getArtists().size() + " label: "
						+ prod.getLabel() + " songs: "
						+ prod.getTracks().size());

			} else
				System.out.println("Product not found!");
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return prod;
	}

	public List<Offer> getOffers(Product product) {
		List<Offer> ret = new ArrayList<Offer>();

		String patternQuery = "from Offer where Produkt_ID = :id";
		System.out.println("Get Offer with id = " + product.getAsin());
		Session sess = null;
		Transaction trx = null;
		Offer prod = null;
		try {
			sess = sessionFactory.openSession();
			trx = sess.beginTransaction();
			Query query = sess.createQuery(patternQuery);
			ret = query.setParameter("id", product.getAsin()).list();
			Iterator itr = query.iterate();
			if (itr.hasNext()) {
				prod = (Offer) itr.next();
				System.out.println("Product: " + prod.getProduct().getAsin()
						+ " Titel: " + prod.getProduct().getTitle()
						+ " Price: " + prod.getPrice() + " loc: "
						+ prod.getLocation());
			} else
				System.out.println("Product not found!");
			trx.commit();
		} catch (HibernateException ex) {
			if (trx != null)
				try {
					trx.rollback();
				} catch (HibernateException exRb) {
				}
			throw new RuntimeException(ex.getMessage());
		} finally {
			try {
				if (sess != null)
					sess.close();
			} catch (Exception exCl) {
			}
		}

		return ret;
	}

}// class

