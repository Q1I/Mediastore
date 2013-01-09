package media.definitions;

import java.util.LinkedHashSet;
import java.util.Set;

public class Kunde {

	private String id = null; // name

	public Kunde(){
		
	}
	public Kunde(String id){
		this.id=id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public String toString(){
		return id;
	}
	
}
