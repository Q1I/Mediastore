import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Category {

	private String hCat;
	private ArrayList<String> query;
	private String key;
	private Category oCat;
	private String cat;
	private String error;
	private Set<String> items;
	
	private boolean okay=true;
	
	public Category(){
		oCat =null;
		cat=null;
		query = new ArrayList<String>();
		this.items=new HashSet<String>();
		this.error= "";
		this.okay=true;
		
	}
	

	public void reset(){
		oCat =null;
		query = new ArrayList<String>();
		this.error= "";
		this.okay=true;
	}
	
	public void commit(Connection con) throws SQLException {
		
		Statement stmt = con.createStatement();

		initQuery();
		for(int i=0; i<this.query.size();i++){
			try{
				stmt.executeUpdate(this.query.get(i));
			}catch(SQLException e){
				if(e.getErrorCode()!=-803) // ignore duplicate entry error
					throw e;
			}
		}
		stmt.close();

	}
	
	
	private void initQuery() {
		//String key = oCat+"_"+cat;
		query.add("insert into Kategorie  VALUES('"+getKey()+"','"+cat+"')");
		query.add("insert into Kategorie  VALUES('"+oCat.getKey()+"','"+oCat.getCat()+"')");
		query.add("insert into Kategorie  VALUES('null_"+hCat+"','"+hCat+"')");
		
		query.add("insert into Oberkategorie  VALUES('"+getKey()+"','"+oCat.getKey()+"')");
		query.add("insert into Hauptkategorie  VALUES('null_"+hCat+"','"+getKey()+"')");
		// items
		Iterator<String> it = items.iterator();
		while (it.hasNext()) {
		    // Get element
		    String element = it.next();
		    query.add("insert into Produkt_Kategorie VALUES('"+element+"' , '"+getKey()+"')");
		}
	}



	public Category getoCat() {
		return oCat;
	}

	public void setoCat(Category oCat) {
		this.oCat = oCat;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		if(cat.length()>0)
			this.cat = parse(cat);
		else
			setError("Invalid category: "+this.cat);
	}

	public void addItem(String it){
		if(it.length()>6)
			this.items.add(it);
		else
			setError("Invalid Item (length)! "+it);
	}
	public boolean isOkay(){
		return okay;
	}
	public void setError(String msg){
		this.okay=false;
		this.error+=msg+"\n";
	}

	public String getQuery(){
		String queryString="Query:\n";
		for(int i =0;i<query.size();i++)
			queryString+=query.get(i)+"\n";
		queryString+="@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
		return queryString;
	}


	public String getKey() {
		String o="null";
		String h="null";
		if(oCat!=null)
			o=oCat.getCat();
		if(hCat!=null)
			h=hCat;
		return h+"_"+o+"_"+cat;
	}


	public void setKey(String key) {
		this.key = key;
	}
	
	public String getError(){
		return "------------------------------\n" +
				"ERROR protocol for Category "+this.cat+"\n"+this.error;
	}
	
	private String parse(String trim) {
		if(trim ==null)
			return null;
		String parsed = trim.replaceAll("'", "`");
		parsed = parsed.replace('"', '`');
		return parsed;
	}


	public String gethCat() {
		return hCat;
	}


	public void sethCat(String hCat) {
		this.hCat = hCat;
	}
}
