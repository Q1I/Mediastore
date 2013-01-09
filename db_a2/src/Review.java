import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class Review {

	private String Produkt_ID=null;
	private String Kunden_ID=null;
	private int Punkte=0;
	private String Rezension=null;
	private String Inhalt = null;
	
	/**Error protocoll for this review*/
	private String error="";
	
	/**List of sql queries to commit*/
	private ArrayList<String> query;
	
	/**Status of review*/
	private boolean okay=true;
	
	public Review(){
		query=new ArrayList<String>();
	}
	
	/**Reset infos*/
	public void reset(){
		Produkt_ID = null;
		Kunden_ID=null;
		Punkte=0;
		Rezension = null;
		Inhalt=null;
		okay=true;
		error="";
	}
	
	/** Commit SQL queries. Connects to db and execute queries.*/
	public void commit(Connection con) throws SQLException {

		Statement stmt = con.createStatement();
		initQuery();
		getQuery();
		for(int i=0; i<this.query.size();i++)
			stmt.executeUpdate(this.query.get(i));
		stmt.close();
	}
	
	/** Init sql queries*/
	public void initQuery(){
		query = new ArrayList<String>();
		query.add("insert into Kunde (K_ID) VALUES ('"+Kunden_ID+"')");		
		query.add("insert into Review (Produkt_ID, Kunden_ID, Punkte, Rezension, Inhalt) VALUES ('"+Produkt_ID+"' , '"+Kunden_ID+"' , "+Punkte+", '"+Rezension+"','"+Inhalt+"')");		
	}
	
	/** Return sql queries*/
	public String getQuery(){
		String queryString="Query:\n";
		for(int i =0;i<query.size();i++)
			queryString+=query.get(i)+"\n";
		queryString+="@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
		return queryString;
	}
	
	/** Set error and error message for this review*/
	public void setError(String er){
		this.okay=false;
		this.error+=er;
	}
	
	/** Get errors protocoll*/
	public String getError(){
		return "------------------------------\n" +
		"ERROR protocol for Book "+this.Produkt_ID+"\n"+this.error;
	}
	
	/** Review has no errors*/
	public boolean isOkay(){
		return okay;
	}

	public String getProdukt_ID() {
		return Produkt_ID;
	}

	public void setProdukt_ID(String produkt_ID) {
		Produkt_ID = produkt_ID;
	}

	public String getKunden_ID() {
		return Kunden_ID;
	}

	public void setKunden_ID(String kunden_ID) {
		Kunden_ID = kunden_ID;
	}

	public int getPunkte() {
		return Punkte;
	}

	public void setPunkte(int punkte) {
		// Check if points are valid
		if(0<=punkte && punkte<=5){
			Punkte = punkte;
		} else{
			String msg="ERROR: Invalid points! Value has to be in [0,5]. Instead Points = "+punkte;
			this.setError(msg);
		}
	}

	public String getRezension() {
		return Rezension;
	}

	public void setRezension(String rezension) {
		Rezension = rezension;
	}

	public String getInhalt() {
		return Inhalt;
	}

	public void setInhalt(String inhalt) {
		Inhalt = inhalt;
	}
}
