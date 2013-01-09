
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.xml.sax.Attributes;

/** Parent of music, dvd and book. Describe methods for error handling and commit 
 * - which will be used from all children*/
public abstract class Produkt {

	protected String error = "";
	protected String Produkt_ID = null;
	protected String Titel = null;
	protected int Verkaufsrang = 0;
	protected String Bild = null;
	protected float Preis = 0;
	protected String Zustand = null;
	protected String Verfuegbarkeit = null;
	protected String Filial_ID = null;

	protected boolean okay = true;
	protected boolean hatAngebot = false;

	/** List of generated sql queries for this product */
	protected ArrayList<String> query;

	/** List of similar products */
	protected ArrayList<String> similar;

	public Produkt(String filiale) {
		this.Filial_ID = filiale;
		similar = new ArrayList<String>();
		query = new ArrayList<String>();
	}

	/** Reset parent infos */
	public void resetParent() {
		Produkt_ID = null;
		Titel = null;
		Verkaufsrang = 0;
		Bild = null;
		Preis = 0;
		Zustand = null;
		Verfuegbarkeit = null;
		// Filial_ID=null;
		okay = true;
		hatAngebot = false;
		query = new ArrayList<String>();
		similar = new ArrayList<String>();

		error = "";
	}

	/** Process attribute list for all products*/
	public void processAttributesParent(Attributes atts) {
		String msg = "";
		// Verkaufsrang
		if (atts.getValue("salesrank").length() > 0) {
			Verkaufsrang = Integer.parseInt(atts.getValue("salesrank"));
			System.out.println("Parse salesrank: " + Verkaufsrang);
		}
		// Bild
		Bild = atts.getValue("picture");

		 if(atts.getValue("asin")==null){

			 msg="ERROR: Invalid Produkt_ID = "+atts.getValue("asin");
			 this.setError(msg);
		 }
		 else if(atts.getValue("asin").length()<5){
			 msg="ERROR: Invalid Produkt_ID = "+atts.getValue("asin");
			 this.setError(msg);
		 }
		 else
			 Produkt_ID = atts.getValue("asin");

		// Preis
		if (atts.getValue("price") != null) {
			if (atts.getValue("price").length() > 1) {
				// Parse price
				String price = atts.getValue("price");
				System.out.println("price: " + price);
				price = price.replaceAll("EUR", ""); // Remove EUR
				System.out.println("price euro removed: " + price);
				if (price.length() > 1) {
					Preis = Float.parseFloat(price); // Parse from string to float
					if (Preis < 0)
						this.setError("Invalid price! Price has to be positive! Price = "
								+ Preis);
				} else {
					msg = "ERROR: Invalid price!";
					this.setError(msg);
				}
				// Has price <=> has angebot
				hatAngebot = true;
			}
		} else
			hatAngebot = false;
		// Zustand
		if (atts.getValue("state") != null)
			Zustand = atts.getValue("state");
	}

	/** Generate parent sql queries -> SQL Queries for all the products*/
	public void getQueryParent() {
		// Produkt
		query.add("insert into Produkt VALUES ('" + Produkt_ID + "' , '"
				+ Titel + "' , " + Verkaufsrang + " , '" + Bild + "')");
		// Aehnlich
		// for(int i=0; i<similar.size();i++)
		// query.add("insert into Aehnlich VALUES('"+Produkt_ID+"' , '"+similar.get(i)+"')");
		// Angebot
		if (hatAngebot == true)
			query.add("insert into Angebot VALUES ('" + Filial_ID + "' , '"
					+ Produkt_ID + "' , '" + Zustand + "' , " + Preis + " , '"
					+ Verfuegbarkeit + "')");
	}

	/** Reset whole product infos*/
	public abstract void reset();

	/** Process specific attribute list */
	public abstract void processAttributes(Attributes atts);

	/** Init SQL queries */
	public abstract ArrayList<String> initQuery();

	/** Reutrn error string */
	public abstract String getError();

	/** Commit SQL queries. Connects to db and execute queries.
	 * @return list of sql queries for similar products to this product
	 * */
	public ArrayList<String> commit(Connection con) throws SQLException {

		Statement stmt = con.createStatement();
		// stmt.executeUpdate(getQueryParent());
		getQueryParent();
		initQuery();
		for (int i = 0; i < this.query.size(); i++) {
			try {
				stmt.executeUpdate(this.query.get(i));
			} catch (SQLException e) {
				if (e.getErrorCode() != -803) // ignore duplicate entry error
					throw e;
			}
		}
		stmt.close();

		// Generate list of similar products, for delayed commit of similar products
		final ArrayList<String> sim = new ArrayList<String>();
		for (int i = 0; i < similar.size(); i++)
			sim.add("insert into Aehnlich VALUES('" + Produkt_ID + "' , '"
					+ similar.get(i) + "')");

		return sim;
	}

	/** Return sql queries*/
	public String getQuery() {
		String queryString = "Query:\n";
		for (int i = 0; i < query.size(); i++)
			queryString += query.get(i) + "\n";
		queryString += "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
		return queryString;
	}

	/** Product has no errors*/
	public boolean isOkay() {
		return okay;
	}

	/** Set error for product 
	 * @param msg Error message
	 */
	public void setError(String msg) {
		this.okay = false;
		this.error += msg + "\n";
	}

	public String getProdukt_ID() {
		return Produkt_ID;
	}

	public void setProdukt_ID(String produkt_ID) {
		Produkt_ID = produkt_ID;
	}

	public String getTitel() {
		return Titel;
	}

	public void setTitel(String titel) {
		Titel = titel;
	}

	public int getVerkaufsrang() {
		return Verkaufsrang;
	}

	public void setVerkaufsrang(int verkaufsrang) {
		Verkaufsrang = verkaufsrang;
	}

	public String getBild() {
		return Bild;
	}

	public void setBild(String bild) {
		Bild = bild;
	}

	public void addSimilar(String sim) {
		if (sim.length() > 0)
			similar.add(sim);

	}

	public float getPreis() {
		return Preis;
	}

	public void setPreis(float preis) {
		if (preis < 0)
			this.setError("Invalid price! Price has to be positive! Price = "
					+ preis);
		Preis = preis;
	}

	public String getZustand() {
		return Zustand;
	}

	public void setZustand(String zustand) {
		Zustand = zustand;
	}

	public String getVerfuegbarkeit() {
		return Verfuegbarkeit;
	}

	public void setVerfuegbarkeit(String verfuegbarkeit) {
		Verfuegbarkeit = verfuegbarkeit;
	}

	public void setHatAngebot(boolean b) {
		this.hatAngebot = b;
	}
}
