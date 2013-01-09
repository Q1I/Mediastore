

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

public class Music extends Produkt{
	private String Label=null;
	private String Erscheinungsjahr=null;
	private ArrayList<String> Kuenstler;
	private ArrayList<String> Lieder;
	
	public Music(String filiale){
		super(filiale);
		Kuenstler = new ArrayList<String>();
		Lieder = new ArrayList<String>();
	}

	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	public String getErscheinungsjahr() {
		return Erscheinungsjahr;
	}

	public void setErscheinungsjahr(String erscheinungsjahr) {
		Erscheinungsjahr = erscheinungsjahr;
	}

	@Override
	public void reset() {
		resetParent();
		Label=null;
		Erscheinungsjahr=null;
		Kuenstler = new ArrayList<String>();
		Lieder = new ArrayList<String>();
	}

	@Override
	public void processAttributes(Attributes atts) {
		processAttributesParent(atts);
	}

	@Override
	public ArrayList<String> initQuery() {
		// Musik
		query.add("insert into Musik VALUES ('"+this.getProdukt_ID()+"','"+Label+"',"+ Erscheinungsjahr+")");
		// Kuenstler
		String name;
		for(int i = 0; i<Kuenstler.size();i++){
			name = Kuenstler.get(i);
			query.add("insert into Medienperson VALUES ('"+name+"')");
			
			query.add("insert into Musik_Kuenstler VALUES ('"+this.getProdukt_ID()+"','"+name+"')");
//			System.out.println("insert into Musik_Kuenstler VALUES ('"+this.getProdukt_ID()+"','"+name+")");
		}
		// Lieder
		for(int i = 0; i<Lieder.size();i++){
			name = Lieder.get(i);
			query.add("insert into Lieder VALUES ('"+this.getProdukt_ID()+"','"+name+"')");
		}
		return query;
	}

	public ArrayList<String> getKuenstler() {
		return Kuenstler;
	}

	public void addKuenstler(String kuenstler) {
		if(kuenstler.length()>1)
			Kuenstler.add(kuenstler);
		else{
			// dont add
//			String msg="ERROR: Invalid Artist: '"+kuenstler+"'. Name has to be longer than 1 char.";
//			this.setError(msg);
		}
			
	}

	public ArrayList<String> getLieder() {
		return Lieder;
	}

	public void addLied(String lieder) {
		Lieder.add(lieder);
	}

	public String getError(){
		return "------------------------------\n" +
				"ERROR protocol for Music "+this.Produkt_ID+"\n"+this.error;
	}
	
}
