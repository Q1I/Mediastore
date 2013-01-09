package media.mediadbimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import media.definitions.Book;
import media.definitions.Category;
import media.definitions.DVD;
import media.definitions.Music;
import media.definitions.Offer;
import media.definitions.Person;
import media.definitions.Product;
import media.definitions.Review;
import media.definitions.SQLResult;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

;

public class Test {

	public static SessionFactory sessionFactory;

	public static void init() {
		System.out.println("LOG");
		Logger.getLogger("org.hibernate").setLevel(Level.ALL);

		/*
		 * Herstellung der Datenbankverbindung.
		 */
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure(
					"media/config/hibernate.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

	public static boolean isWrapperType(String clazz) {
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

	public static SQLResult executeHqlQuery(String query, boolean deref) {
		init();

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
			Query hqlQuery = sess.createQuery(query);
			hqlQuery.setMaxResults(2);

			org.hibernate.type.Type singleType = hqlQuery.getReturnTypes()[0];
			if (singleType.isAssociationType()) { // Object
				System.out
						.println("Der erste Rückgabewert ist ein Objekt vom Typ "
								+ singleType.getName());
				ClassMetadata metadata = sessionFactory
						.getClassMetadata(singleType.getName());
				String[] propertyNames = metadata.getPropertyNames();
				for (int i = 0; i < propertyNames.length; i++) {
					System.out.println(i + ".propNames: " + propertyNames[i]);
				}
				System.out.println("propNames: "
						+ metadata.getIdentifierPropertyName());

				sqlRes.setHeader(propertyNames);

				org.hibernate.type.Type[] propertyTypes = metadata
						.getPropertyTypes();
				Iterator results  = hqlQuery.iterate();
//				ScrollableResults results = hqlQuery.scroll();
				int k = 0;
				int rowCount = 0;
				while (results.hasNext()) {
					System.out.println("RowCount: " + rowCount);
					k = 0;
					bodyRow.clear();
					Object value = results.next();
					Object[] values = metadata.getPropertyValues(value, EntityMode.POJO);
					System.out.println(k+". value: "+value.toString()+ " length: "+values.length);
					for (int l=0;l<values.length;l++) {
						System.out.println(l+". "+values[l]);
					}
					System.out.println();
//					for (Object value : results.get()) {
//						System.out.print((value == null ? "null" : k+"="+value
//								.toString()) + '\t');
//
//						k++;
//					}
					System.out.println();

					rowCount++;
					body.add(finalBodyRow);
				}

				// for (int i = 0; i < propertyTypes.length; i++) {
				// System.out.println(i + ".propertyTypesName: "
				// + propertyTypes[i].getName());
				// System.out.println(i + ".propertyTypes: "
				// + propertyTypes[i].getReturnedClass().toString());
				//
				// // System.out.println("STRING: "+propertyTypes[i].getName());
				// // System.out.println("STRING: "+String.class.getName());
				// // System.out.println("STRING: "+String.class.toString());
				// }

			} else {
				System.out
						.println("Der erste Rückgabewert ist primitiven Typs");
				String[] aliases = hqlQuery.getReturnAliases();
				for (int i = 0; i < aliases.length; i++) {
					System.out.println(i + ".propNames: " + aliases[i]);
				}
				
				sqlRes.setHeader(aliases);
				List li = hqlQuery.list();
				Iterator results = li.iterator();
//				ScrollableResults results = hqlQuery.scroll();
				while (results.hasNext()) {
					bodyRow.clear();
					Object value = results.next();
					System.out.println("VALUE: "+value.toString());
//					for (Object value : results.get()) {
//						System.out.print((value == null ? "null" : value
//								.toString()) + '\t');
//
//						bodyRow.add(value.toString());
//					}
					System.out.println();
					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
						System.out.println(j + ".body: " + finalBodyRow[j]);
					}
					finalBodyRow = new String[bodyRow.size()];
					for (int j = 0; j < bodyRow.size(); j++) {
						finalBodyRow[j] = bodyRow.get(j);
						System.out.println(j + ".body: " + finalBodyRow[j]);
					}
					body.add(finalBodyRow);
				}
				sqlRes.setBody(body);
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
		System.out.println("HQL done!");
		return sqlRes;

	}

	public static void main(String args[]) {
		 String query = "select p.id,p.title,p.reviews from Product as p";
//		 String query = "select p from Product as p";
//		String query = "select p from Track as p";
		executeHqlQuery(query, false);
	}
}
