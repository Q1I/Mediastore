
/*
 * XMLParserDemo.java
 */

// allgemeine Java-Klassen 
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Hashtable;

// Klassen der SAX-Parser-API
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

// Klasse des SAX XML-Parsers (Xerces von Apache)
import org.apache.xerces.parsers.SAXParser;


/**
 * Demoprogramm zum Parsen von XML-Daten.
 * <P>
 * Klasse ist von DefaultHandler abgeleitet. Diese implementiert alle
 * notwendigen Methoden des ContentHandler-Interface, so dass nur die
 * projektspezifischen Methoden &uuml;berladen werden m&uuml;ssen.
 * <P>
 * Diese Demo-Klasse liest alle wichtigen Daten aus der cities.xml Datei ein.
 * F&uuml;r diese Daten fehlt nur noch die Datenbankschnittstelle.<BR>
 * Sie sollte sich aber auch einfach an die Anforderungen der country????.xml
 * Dateien anpassen lassen.
 * 
 * @author Timo B&ouml;hme
 * @version 1.0
 */
public class XMLParserDresden extends DefaultHandler {

	// -- Globale Variablen ----------------------------------------------------
	/** XML Parser (mit SAX API) */
	public final static String parserClass = "org.apache.xerces.parsers.SAXParser";

	/**
	 * enth&auml;lt Zeichendaten eines Elements<BR>
	 * Achtung: mixed content wird nicht bzw. falsch behandelt
	 */
	private StringBuffer elementTextBuf = new StringBuffer();
	/** enth&auml;lt die Elementnamen des aktuellen Pfades */
	private Vector xmlPath = new Vector();

	public int countError=0;
	public int countOk=0;
	public int countAngebot=0;
	
	private String url="jdbc:mysql://localhost/Mediastore";
	private String userName="root";
	private String password="root";
	
//	private static final String userName = "dbprak20";
//	private static final String password = ".P$RAK20";
//	private static final String url = "jdbc:db2://leutzsch.informatik.uni-leipzig.de:50001/dbprak12";

	private Connection con;
	
	private Book curBook;
	private Music curMusic;
	private DVD curDVD;

	private Produkt curProdukt;
	
	private String similarCache;
	private ArrayList<String> sim;
	// Log
	private String logError;
	private String log;
	private String logOk;
	
	private String curProdString;
	
	
	/**
	 * Connects to database
	 * @return Connection
	 */
	public Connection getConnection(){
		System.out.println("Connecting to database..");
		Connection con = null;
		try {
//			Class.forName("com.ibm.db2.jcc.DB2Driver");
			Class.forName("com.mysql.jdbc.Driver"); //JDBC Driver
		} catch(java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(this.url, this.userName, this.password);
			//con = DriverManager.getConnection(this.url);
			System.out.println("Connected to database");
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return con;
	}


	// -------------------------------------------------------------------------
	/** Constructor */
	public XMLParserDresden() {
		sim = new ArrayList<String>();
		
		curBook = new Book("Dresden");
		curMusic = new Music("Dresden");
		curDVD = new DVD("Dresden");
		con=getConnection();
		
		logError="\n\n===========================================================" +
				"\nError-Log:\n";
		log="Log:\n";

		logOk = "\n\n==========================================================="
			+ "\nError-Log:\n";
	}

	
	// == XML spezifische Methoden =============================================
	// -------------------------------------------------------------------------
	/**
	 * Ermittelt den Namen des &uuml;bergeordneten Elementes.
	 * 
	 * @return Namen des &uuml;bergeordneten Elementes oder <code>null</code>
	 *         wenn kein &uuml;bergeordnetes Element vorhanden ist
	 */
	private String getParent() {
		return (xmlPath.size() > 1) ? (String) xmlPath
				.elementAt(xmlPath.size() - 2) : "";
	}

	// == Call back Routinen des Parsers =======================================
	// -------------------------------------------------------------------------
	/**
	 * Wird vom Parser beim Start eines Elements aufgerufen.<BR>
	 * (call back method)
	 */
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) {

		// neues Element -> Textinhalt zuruecksetzen
		elementTextBuf.setLength(0);
		// aktuellen Elementnamen an Pfad anf�gen
		xmlPath.addElement(rawName);

		// Item
		if (rawName.equals("item") & getParent().equals("shop")) { // item gefunden
			curProdString = atts.getValue("pgroup");

//			echo("#####"+curProdString);
			if(curProdString.equals("DVD")){
				curDVD.reset();
				curDVD.processAttributes(atts);
				System.out.println("###DVD" + localName + 
						"\nasin: " + curDVD.getProdukt_ID());
			}
			else if(curProdString.equals("Music")){
				curMusic.reset();
				curMusic.processAttributes(atts);
				System.out.println("###Music" + localName + 
						"\nasin: " + curMusic.getProdukt_ID());
			}else if(curProdString.equals("Book")){
				curBook.reset();
				curBook.processAttributes(atts);
				System.out.println("###Book" + localName + 
						"\nasin: " + curBook.getProdukt_ID());
			}
			return;
		}
		// Bild
		if(rawName.equals("details") && atts.getValue("img").length()>1){
			String img = parse(atts.getValue("img"));
			if(curProdString.equals("DVD"))
				curDVD.setBild(img);
			else if(curProdString.equals("Music"))
				curMusic.setBild(img);
			else if(curProdString.equals("Book"))
				curBook.setBild(img);
			echo("Parse Bild: "+img);
		}
		
		// Zustand
		if(rawName.equals("price") && atts.getValue("state").length()>1){
			if(curProdString.equals("DVD")){
				curDVD.setZustand(parse(atts.getValue("state")));
				echo("Parse Zustand: "+curDVD.getZustand());
			}
			if(curProdString.equals("Book")){
				curBook.setZustand(parse(atts.getValue("state")));
				echo("Parse Zustand: "+curBook.getZustand());
			}
			if(curProdString.equals("Music")){
				curMusic.setZustand(parse(atts.getValue("state")));
				echo("Parse Zustand: "+curMusic.getZustand());
			}	
		} 
//		if(rawName.equals("price") && (atts.getValue("state").length()==0)){
//			if(curProdString.equals("DVD")){
//				curDVD.setError("ERROR: Zustand is empty!");
//			}
//			if(curProdString.equals("Book")){
//				curBook.setError("ERROR: Zustand is empty!");
//			}
//			if(curProdString.equals("Music")){
//				curMusic.setError("ERROR: Zustand is empty!");
//			}
//		}
		
		
		// Aehnlich
		if ( rawName.equals("item") & getParent().equals("similars")) {
			// Book title
			if (curProdString.equals("Book")) {
				curBook.addSimilar(parse(atts.getValue("asin")));
				System.out.println("Set Similar for book: " + parse(atts.getValue("asin")));
				return;
			}
			// DVD title
			if (curProdString.equals("DVD")) {
				curDVD.addSimilar(parse(atts.getValue("asin")));
				System.out.println("Set Similar for DVD: "+parse(atts.getValue("asin")));
				return;
			}
			// Musik title
			if (curProdString.equals("Music")) {
				curMusic.addSimilar(parse(atts.getValue("asin")));
				System.out.println("Set Similar for music: " + parse(atts.getValue("asin")));
				return;
			}
		}
		
		
		
		
		
		// Book
		if (rawName.equals("publication") && curProdString.equals("Book")) {
			// Music 
			if (getParent().equals("bookspec")) { // muss Kindelement von bookspec sein
				curBook.setErscheinungsdatum((parse(atts.getValue("date")))); // Parse date
				echo("Parse Erscheinungsdatum: "+curBook.getErscheinungsdatum());
				return;
			}
			// Error 
			else{
				// Error
				curMusic.setError("Ershceinungsdatum in invalid Entity: "+getParent());
//				logError("Ershceinungsdatum in invalid Entity: "+getParent());
				return;
			}
		}
		// ISBN
		if (rawName.equals("isbn")  && curProdString.equals("Book") ) {
			if (getParent().equals("bookspec")) { // muss Kindelement von bookspec sein
				if(atts.getValue("val").length()!=0){
					curBook.setISBN(((parse(atts.getValue("val"))))); // Parse date
					echo("Parse ISBN: "+curBook.getISBN());
					return;
				}else{
//					logError("ERROR: ISBN is empty for book: "+curBook.getProdukt_ID());
					curBook.setError("ERROR: ISBN is empty!");
				}
			}
			// Error 
			else{
				// Error
				curMusic.setError("ISBN in invalid Entity: "+getParent());
//				logError("ISBN in invalid Entity: "+getParent());
				return;
			}
		}
	
		
		
		
//		// DVD -----------------------------------------------------
//		// Rolle
//		if (rawName.equals("director")) {
//			if(getParent().equals("directors")){
//					curDVD.addRolle("director",(parse(atts.getValue("name"))));
//					echo("Parse director: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
//					return;
//			}
//			else{
//				curDVD.setError("ERROR: director is not child of directors!");
////				logError("ERROR: director is not child of directors!");
//			}
//		}
//		if (rawName.equals("creator")) {
//			if(getParent().equals("creators")){
//					curDVD.addRolle("creator",(parse(atts.getValue("name"))));
//					echo("Parse creator: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
//					return;
//			}
//			else{
//				curDVD.setError("ERROR: creator is not child of creators!");
////				logError("ERROR: creator is not child of creators!");
//			}
//		}
//		if (rawName.equals("actor")) {
//			if(getParent().equals("actors")){
//					curDVD.addRolle("actor",(parse(atts.getValue("name"))));
//					echo("Parse actor: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
//					return;
//			}
//			else{
//				curDVD.setError("ERROR: creator is not child of creators!");
////				logError("ERROR: creator is not child of creators!");
//			}
//		}
		
		
	}

	// -------------------------------------------------------------------------
	/**
	 * Wird vom Parser beim Ende eines Elements aufgerufen.<BR>
	 * (call back method)
	 */
	public void endElement(String namespaceURI, String localName, String rawName) {

		// entferne Whitespace an Zeichendatengrenzen
		String elementText = parse(elementTextBuf.toString().trim());

		try {
			
//			// gesonderte Behandlungsroutinen je nach Elementtyp
			// Book
			if (rawName.equals("item") && curProdString.equals("Book") && getParent().equals("shop") && curBook.isOkay()) {
				if(curBook.isOkay()){
					try {
						addSim(curBook.commit(con));
						logOk("###Daten eingelesen zu Book: "
								+ curBook.getTitel());
						countOk++;
					} catch (SQLException e) {
						countError++;
//						curBook.initQuery();
						logError(curBook.getError()+"\nERROR: Commit Book failed for book:\n"+e.getMessage()+"\nPrint SQL query: "+curBook.getQuery());
						e.printStackTrace();
					}
					curBook.reset();
					return;
				} else {
					countError++;
					logError(curBook.getError());
					return;
				}
			}
			else if (rawName.equals("item") && curProdString.equals("Book") && getParent().equals("shop") && !curBook.isOkay()) {
				countError++;
				logError(curBook.getError());
				return;
			}
			
			// Preis + Zustand
			if (rawName.equals("price")) {
				// DVD 
				if (curProdString.equals("DVD")) { // muss Kindelement von labels sein
					if(elementText.length()>1){
						float price = Float.parseFloat(parse(elementText));
						curDVD.setPreis(price);
						curDVD.setHatAngebot(true);
						echo("Parse Preis: "+curDVD.getPreis());
						countAngebot++;
					}else
						curDVD.setHatAngebot(false);
					return;
				}
				// Music 
				else if (curProdString.equals("Music")) { // muss Kindelement von labels sein
					if(elementText.length()>1){
						float price = Float.parseFloat(elementText);
						curMusic.setPreis(price);
						curMusic.setHatAngebot(true);
						echo("Parse Preis: "+curMusic.getPreis());
						countAngebot++;
					}else
						curMusic.setHatAngebot(false);
					return;
				}
				// Book 
				else if (curProdString.equals("Book")) { // muss Kindelement von labels sein
					if(elementText.length()>1){
						float price = Float.parseFloat(parse(elementText));
						curBook.setPreis(price);
						curBook.setHatAngebot(true);
						echo("Parse Preis: "+curBook.getPreis());
						countAngebot++;
					}else
						curBook.setHatAngebot(false);
					return;
				}
			}
			
			
			// DVD
			if (rawName.equals("item") && curProdString.equals("DVD") && getParent().equals("shop") && curDVD.isOkay()) {
				// TODO Commit
				try {
					addSim(curDVD.commit(con));
					log("###Daten eingelesen zu DVD: "
							+ curDVD.getTitel());
					countOk++;
				} catch (SQLException e) {
					countError++;
					logError(curDVD.getError()+"\nERROR: Commit DVD failed.\n"+e.getMessage()+"\nPrint SQL query: "+curDVD.getQuery());
				}
				curDVD.reset();
				return;
			}
			if ( rawName.equals("item") && curProdString.equals("DVD") && getParent().equals("shop") && !curDVD.isOkay()){ 
				countError++;
				logError(curDVD.getError());
				return;
			}
			
			// Music
			if (rawName.equals("item") && curProdString.equals("Music") && getParent().equals("shop") && curMusic.isOkay()) {
				// Commit
				try {
					addSim(curMusic.commit(con));
					log("###Daten eingelesen zu Music: "
							+ curMusic.getTitel());
					countOk++;
				} catch (SQLException e) {
					countError++;
					logError(curMusic.getError()+"\nERROR: Commit Music failed! "+e.getMessage()+"\nPrint SQL query: "+curMusic.getQuery());
					e.printStackTrace();
				}
				return;
			}
			if (rawName.equals("item") && curProdString.equals("DVD") && getParent().equals("shop") && !curMusic.isOkay()) {
//				logError("===>Didn't commit Music: ID:"+curMusic.getProdukt_ID()+", Titel: "+curMusic.getTitel());
				countError++;
				logError(curMusic.getError());
				return;
			}
			
			
			
			// Titel als Name von einem Produkt
			if ( rawName.equals("title") && 
					(getParent().equals("item") )) {
				// Book title
				if (curProdString.equals("Book")) {
					curBook.setTitel(parse(elementText));
					System.out.println("Parse Titel: " + curBook.getTitel());
					return;
				}
				// DVD title
				if (curProdString.equals("DVD")) {
					curDVD.setTitel(parse(elementText));
					System.out.println("Parse Titel: " + curDVD.getTitel());
					return;
				}
				// Musik title
				if (curProdString.equals("Music")){
					curMusic.setTitel(parse(elementText));
					System.out.println("Parse Titel: " + curMusic.getTitel());
					return;
				}
			}
			

			// TODO Erscheinungsjahr -> aber exaktes datum??
			if (rawName.equals("releasedate") && curProdString.equals("Music")) {
				if (elementText.length()>0) {
//					String jahr = parse(elementText);
//					jahr = jahr.substring(0,jahr.indexOf('-'));
//					curMusic.setErscheinungsjahr(jahr);
					curMusic.setErscheinungsjahr(parse(elementText));
					echo("Parse Erscheinungsjahr: "+curMusic.getErscheinungsjahr());
					return;
				}else{
					
//					curMusic.setError("ERROR: Releasedate is empty");
				}
			}
			
			// Kuenstler
			if (rawName.equals("artist") && curProdString.equals("Music") ) {
				// Music 
				if (getParent().equals("artists")) { // muss Kindelement von artists sein
					curMusic.addKuenstler(parse(elementText)); // Pars attributes
					echo("Parse Kuenstler: "+elementText);
//					echo("Parse Kuenstler: "+curMusic.getKuenstler().get(curMusic.getKuenstler().size()-1));
					return;
				}
				// Error 
				else{
					// Error
					curMusic.setError("artist in invalid Entity: "+getParent());
					return;
				}
			}
			
			// Lieder
			if (rawName.equals("title") && getParent().equals("tracks") && curProdString.equals("Music")) {
				if (elementText.length()>0) {
					curMusic.addLied(parse(elementText));
					echo("Parse Lied: "+curMusic.getLieder().get(curMusic.getLieder().size()-1));
					return;
				}
				else{
//					curMusic.setError("ERROR: title from track is empty!");
				}
			}
			
			// Label TODO nur ein Label??
			if (rawName.equals("label") && getParent().equals("labels") && curProdString.equals("Music")) {
				if (elementText.length()>0) {
					curMusic.setLabel(parse(elementText));
					echo("Parse Label: "+curMusic.getLabel());
					return;
				}
				else{
//					curMusic.setError("ERROR: label is empty!");
				}
			}
			
			// Book -------------------------------------------------------			
			
			// Seiten
			if (rawName.equals("pages")) {
				if(getParent().equals("bookspec")){
					if (elementText.length()>0) {
						curBook.setSeitenzahl(Integer.parseInt((parse(elementText))));
						echo("Parse Seitenanzahl: "+curBook.getSeitenzahl());
						return;
					}
					else{
//						curBook.setError("ERROR: Seitenanzahl is empty!");
//						logError("ERROR: Seitenanzahl is empty for book: "+curBook.getTitel()+", title: "+curBook.getTitel());
	//					if(!getParent().equals("musicspec"))
	//						logError("ERROR: Releasedate is not child of musicspec!");
					}
				}
				else{
					curBook.setError("ERROR: publisher is not child of publishers");
//					logError("ERROR: publisher is not child of publishers for book: "+curBook.getTitel()+", title: "+curBook.getTitel());
				}
			}
			

			// Verlag --> ein Buch kann nur von ienem Verlag ausgegeben werden??
			if (rawName.equals("publisher")) {
				if (getParent().equals("publishers")) { // muss Kindelement von publishers sein
					curBook.setVerlag(((parse(elementText)))); // Parse date
					echo("Parse Verlag: "+curBook.getVerlag());
					return;
				}
				// Error 
				else{
					// Error
					curMusic.setError("Verlag in invalid Entity: "+getParent());
//					logError("Verlag in invalid Entity: "+getParent());
					return;
				}
			}
			
			// Author
			if (rawName.equals("author")) {
				if (getParent().equals("authors")) { // muss Kindelement von artists sein
					curBook.addAutor(parse(elementText)); // Pars attributes
					echo("Parse Author: "+curBook.getAutor().get(curBook.getAutor().size()-1));
					return;
				}
				// Error 
				else{
					// Error
					curMusic.setError("author in invalid Entity: "+getParent());
//					logError("Label in invalid Entity: "+getParent());
					return;
				}
			}
			
			// DVD -------------------------------------------------------
			if(curProdString.equals("DVD")){
			
			// Laufzeit
			if (rawName.equals("runningtime")  ) {
				if(getParent().equals("dvdspec")){
					if (elementText.length()>0) {
						curDVD.setLaufzeit(Integer.parseInt((parse(elementText))));
						echo("Parse Laufzeit: "+curDVD.getLaufzeit());
						return;
					}
					else{
//						curDVD.setError("ERROR: Laufzeit is empty!");
//						logError("ERROR: Laufzeit is empty!");
	//					if(!getParent().equals("musicspec"))
	//						logError("ERROR: Releasedate is not child of musicspec!");
					}
				}
				else{
					curDVD.setError("ERROR: runningtime is not child of dvdspec!");
//					logError("ERROR: runningtime is not child of dvdspec!");
				}
			}
			
			// Format
			if (rawName.equals("format") ) {
//				if(getParent().equals("dvdspec"))
				{
					if (elementText.length()>0) {
						curDVD.setFormat((parse(elementText)));
						echo("Parse Format: "+curDVD.getFormat());
						return;
					}
					else{
//						curDVD.setError("ERROR: Format is empty!");
					}
				}

			}
			
			// Regioncode
			if (rawName.equals("regioncode")  ) {
				if(getParent().equals("dvdspec")){
					if (elementText.length()>0) {
						curDVD.setRegioncode((parse(elementText)));
						echo("Parse Regioncode: "+curDVD.getRegioncode());
						return;
					}
					else{
//						curDVD.setError("ERROR: Regioncode is empty!");
					}
				}
				else{
					curDVD.setError("ERROR: Regioncode is not child of dvdspec!");
				}
			}
			
			// Rolle
			if (rawName.equals("director")) {
				if(getParent().equals("directors")){
						curDVD.addRolle("director",(parse(elementText)));
						echo("Parse director: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
						return;
				}
				else{
					curDVD.setError("ERROR: director is not child of directors!");
//					logError("ERROR: director is not child of directors!");
				}
			}
			if (rawName.equals("creator")) {
				if(getParent().equals("creators")){
						curDVD.addRolle("creator",(parse(elementText)));
						echo("Parse creator: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
						return;
				}
				else{
					curDVD.setError("ERROR: creator is not child of creators!");
//					logError("ERROR: creator is not child of creators!");
				}
			}
			if (rawName.equals("actor")) {
				if(getParent().equals("actors")){
						curDVD.addRolle("actor",(parse(elementText)));
						echo("Parse actor: "+elementText);
						//echo("Parse actor: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
						return;
				}
				else{
					curDVD.setError("ERROR: creator is not child of creators!");
//					logError("ERROR: creator is not child of creators!");
				}
			}
			
			}//DVD-----------------------
			
		} finally {
			// Element zu ende -> Textinhalt zuruecksetzen
			// notwendig bei mixed content
			elementTextBuf.setLength(0);
			// Element vom Pfad entfernen
			xmlPath.setSize(xmlPath.size() - 1);
		}

	}

	private void setSimilarCache(String string) {
		this.similarCache=string;
		
	}

	/** Remove invalid chars*/
	private String parse(String trim) {
		String parsed = trim.replaceAll("'", "`");
		parsed = parsed.replace('"', '`');
		return parsed;
	}

	// -------------------------------------------------------------------------
	/**
	 * Wird vom Parser mit Textinhalt des aktuellen Elements aufgerufen.<BR>
	 * (call back method)
	 * <P>
	 * Achtung: Entities (auch Zeichenreferenzen wie &amp;ouml;) stellen eine
	 * Textgrenze dar und werden durch einen erneuten Aufruf dieser Funktion
	 * &uuml;bergeben.
	 */
	public void characters(char[] ch, int start, int length) {
		elementTextBuf.append(ch, start, length);
		// echo(elementTextBuf.toString());
	}

	// -------------------------------------------------------------------------
	/** Initialisiert Parser und started Proze� */
	public void doit(String dataFilename) {

		// XML Parser
		XMLReader parser = null;

		// Insert Filiale
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("INSERT INTO Filiale VALUES ('Dresden')");
		} catch (SQLException e) {
			countError++;
			logError("##-----------------\nERROR inserting filiale 'Dresden'\n"+e.getMessage());
			e.printStackTrace();
		}
		
		
		// Parser instanziieren
		try {
			parser = XMLReaderFactory.createXMLReader(parserClass);
		} catch (SAXException e) {
			System.err.println("Fehler beim Initialisieren des Parsers ("
					+ parserClass + ")\n" + e.getMessage());
			System.exit(1);
		}

		// Ereignisse sollen von dieser Klasse behandelt werden
		parser.setContentHandler(this);

		// Parser starten
		try {
			parser.parse(dataFilename);
		} catch (SAXException e) {
			System.err.println("Parser Exception:\n" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Parser IOException:\n" + e.getMessage());
		}
		
		// Add sim
		commitSim();
		
		// Write
//		System.out.println(logError);
		System.out.println("\n\nCount Error: "+countError);
		System.out.println("Count Ok: "+countOk);
		System.out.println("Count Angebot: "+countAngebot);
		
		writeErrorLog(logError);
		writeOkLog(logOk);
	}

	
	private void writeErrorLog(String s) {
		System.out.println("Write error log");
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter("resource/logs/errorLog_dresden.txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(s);
			  out.write("\nCount Error: "+countError);
				out.write("\nCount OK: "+countOk);
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		
	}
	
	private void writeOkLog(String s) {
		System.out.println("Write ok log");
		try {
			// Create file
			FileWriter fstream = new FileWriter(
					"resource/logs/okLog_dresden.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(s);
			out.write("\nCount Error: "+countError);
			out.write("\nCount OK: "+countOk);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}
	
	private void addSim(ArrayList<String> commit) {
		for (int i = 0; i < commit.size(); i++){
			this.sim.add(commit.get(i));
		}

	}
	
	private void commitSim() {
		System.out.println("Commit sim: "+sim.size());
		
		
			
			for (int i = 0; i < sim.size(); i++) {
				System.out.println(i + ". " + sim.get(i));
				try {
					Statement stmt = con.createStatement();
					stmt.executeUpdate(sim.get(i));

					stmt.close();
				} catch (SQLException e) {
					countError++;
					logError("##-----------------\nERROR inserting similiars\n"
							+ e.getMessage()+"\nquery: "+sim.get(i));
					e.printStackTrace();
				}
			}
			
			
		
	}
	
	// -------------------------------------------------------------------------
	/** Gibt Aufrufsyntax zur&uuml;ck */
	public static void usage() {
		System.out.println("usage: java XMLParserDemo <XML_FILE>");
		System.exit(1);
	}

	public void echo(String s) {
		System.out.println(s);
		
	}

	public void log(String s) {
//		System.out.println(s);
		// log normal
		
	}
	public void logOk(String s) {
		if (s.length() != 0) {
			System.out.println(s);
			// log normal
			// log error in file
			logOk += "## " + s + "\n";
		}
	}
	
	public void logError(String s) {
		if(s.length()!=0){
			System.out.println(s);
			// log normal 
			// log error in file
			logError+="## "+s+"\n";
		}
	}
	
	// -------------------------------------------------------------------------
	/**
	 * Programmstart
	 * 
	 * @param args
	 *            Aufrufparameter
	 */
	public static void main(String args[]) {

		// Programminstanz erzeugen
		XMLParserDresden prg = new XMLParserDresden();
		// ausfuehren
		prg.doit("resource/data_ms/dresden.xml");

	}

}
