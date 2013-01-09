

import java.util.ArrayList;

import org.xml.sax.Attributes;

public class DVD extends Produkt{
	private int Laufzeit=0;
	private String Format=null;
	private String Regioncode=null;
	private ArrayList<String> Rolle;
	
	public DVD(String filiale){
		super(filiale);
		this.Rolle=new ArrayList<String>();
	}

	public int getLaufzeit() {
		return Laufzeit;
	}

	public void setLaufzeit(int laufzeit) {
		Laufzeit = laufzeit;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	public String getRegioncode() {
		return Regioncode;
	}

	public void setRegioncode(String regioncode) {
		Regioncode = regioncode;
	}

	@Override
	public void reset() {
		resetParent();
		Laufzeit=0;
		Format=null;
		Regioncode=null;
		Rolle=new ArrayList<String>();
		
	}
	
	@Override
	public void processAttributes(Attributes atts) {
		processAttributesParent(atts);
	}
	
	@Override
	public ArrayList<String> initQuery() {
		query.add("insert into DVD VALUES ('"+this.getProdukt_ID()+"',"+
			Laufzeit+",'"+ Format+"' , '"+Regioncode+"')");
		for(int i=0;i<Rolle.size();i++){
			String[] name = Rolle.get(i).split(",");
			query.add("insert into Medienperson VALUES ("+name[0]+")");
			query.add("insert into DVD_Beteiligung VALUES ('"+this.getProdukt_ID()+"' ,"+Rolle.get(i)+")");
		}
		return query;
	}

	public ArrayList<String> getRolle() {
		return Rolle;
	}

	public void addRolle(String rolle, String name) {
		if(rolle.length()>1 && name.length()>1){
			String end = "'"+name +"' , '"+ rolle+"'";
			Rolle.add(end);
		}else{
			// dont add
//			String msg="ERROR: Invalid length for role: '"+rolle+"' or name: '"+name+"'. Has to be longer than 1 char.";
//			this.setError(msg);
			
		}
			
	}
	
	public String getError(){
		return "------------------------------\n" +
				"ERROR protocol for DVD "+this.Produkt_ID+"\n"+this.error;
	}

	public void processKondition(Attributes atts) {
		// Preis
		
		
	}
}
