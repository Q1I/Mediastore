import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.xml.sax.Attributes;

public class Book extends Produkt{
	private String ISBN="";
	private String Erscheinungsdatum="";
	private int Seitenzahl=0;
	private String Verlag="";
	private ArrayList<String> autor;
		
	public Book(String filiale){
		super(filiale);
		autor=new ArrayList<String>();
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getErscheinungsdatum() {
		return Erscheinungsdatum;
	}

	public void setErscheinungsdatum(String erscheinungsdatum) {
		Erscheinungsdatum = erscheinungsdatum;
	}

	public int getSeitenzahl() {
		return Seitenzahl;
	}

	public void setSeitenzahl(int seitenzahl) {
		Seitenzahl = seitenzahl;
	}

	public String getVerlag() {
		return Verlag;
	}

	public void setVerlag(String verlag) {
		Verlag = verlag;
	}

	@Override
	public void reset() {
		resetParent();
		ISBN="";
		Erscheinungsdatum="";
		Seitenzahl=0;
		Verlag="";
		autor=new ArrayList<String>();
	}

	@Override
	public void processAttributes(Attributes atts) {
		processAttributesParent(atts);
	}
	
	@Override
	public ArrayList<String> initQuery() {
		if(Seitenzahl==0)
			query.add("insert into Buch VALUES ('"+this.getProdukt_ID()+"','"+
					ISBN+"','"+ Erscheinungsdatum+"' , NULL , '"+Verlag+"')");
		else
			query.add("insert into Buch VALUES ('"+this.getProdukt_ID()+"','"+
			ISBN+"','"+ Erscheinungsdatum+"' , "+Seitenzahl+" , '"+Verlag+"')");
		for(int i = 0 ;i<autor.size();i++){
			query.add("insert into Medienperson VALUES ('"+autor.get(i)+"')");
			query.add("insert into Buch_Autor VALUES ('"+this.getProdukt_ID()+"','"+autor.get(i)+"')");
		}
		return query;
	}

	public void addVerlag(String parse) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<String> getAutor() {
		return autor;
	}

	public void addAutor(String autor) {
		this.autor.add(autor);
	}

	public String getError(){
		return "------------------------------\n" +
				"ERROR protocol for Book "+this.Produkt_ID+"\n"+this.error;
	}

}
