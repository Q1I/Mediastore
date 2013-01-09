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
public class XMLParserLeipzig extends DefaultHandler {

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

//	private static final String userName = "dbprak20";
//	private static final String password = ".P$RAK20";
//	private static final String url = "jdbc:db2://leutzsch.informatik.uni-leipzig.de:50001/dbprak12";

	// local test
	 private String url="jdbc:mysql://localhost/Mediastore";
	 private String userName="root";
	 private String password="root";

	private Connection con;

	// Current products
	private Book curBook;
	private Music curMusic;
	private DVD curDVD;

	/** Cache for current product*/
	private String similarCache;
	
	/** list of all products with similar products*/
	private ArrayList<String> sim;
	
	// Log
	private String logError;
	private String logOk;
	
	// Statistics
	public int countError = 0;
	public int countOk = 0;




	/**
	 * Connects to database
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {
		System.out.println("Connecting to database..");
		Connection con = null;
		try {
//			Class.forName("com.ibm.db2.jcc.DB2Driver");
			 Class.forName("com.mysql.jdbc.Driver"); //JDBC Driver
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(url, userName, password);
			// con = DriverManager.getConnection(this.url);
			System.out.println("Connected to database");
		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return con;
	}


	/** Constructor */
	public XMLParserLeipzig() {
		curBook = new Book("Leipzig");
		curMusic = new Music("Leipzig");
		curDVD = new DVD("Leipzig");
		con = getConnection();
		sim = new ArrayList<String>();

		logError = "\n\n==========================================================="
				+ "\nError-Log:\n";

		logOk = "\n\n==========================================================="
				+ "\nOk-Log:\n";

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

		// gesonderte Behandlungsroutinen je nach Elementtyp
		// Book
		if (rawName.equals("Book")) { // neues Buch gefunden
			curBook.reset();
			System.out.println("###" + localName + "\nasin: "
					+ atts.getValue("asin"));
			curBook.processAttributes(atts);
			return;
		}

		if (rawName.equals("DVD")) { // neues Buch gefunden
			curDVD.reset();
			System.out.println("###" + localName + "\nasin: "
					+ atts.getValue("asin"));
			curDVD.processAttributes(atts);
			return;
		}
		if (rawName.equals("Music")) { // neue Musik gefunden
			curMusic.reset();
			System.out.println("###" + localName + "\nasin: "
					+ atts.getValue("asin"));
			curMusic.processAttributes(atts);
			return;
		}

		// Get Attribute nur in start element moeglich
		// Label
		if (rawName.equals("label")) {
			// Music
			if (getParent().equals("labels")) { // muss Kindelement von labels
												// sein
				curMusic.setLabel(parse(atts.getValue("name"))); // Pars
																	// attributes
				log("Parse Label: " + curMusic.getLabel());
				return;
			}
			// Error
			else {
				// Erroronly
				curMusic.setError("Label in invalid Entity: " + getParent());
				return;
			}
		}

		// Kuenstler
		if (rawName.equals("artist")) {
			// Music
			if (getParent().equals("artists")) { // muss Kindelement von artists
													// sein
				curMusic.addKuenstler(parse(atts.getValue("name"))); // Pars
																		// attributes
				log("Parse Kuenstler: " + parse(atts.getValue("name")));
				// log("Parse Kuenstler: "+curMusic.getKuenstler().get(curMusic.getKuenstler().size()-1));
				return;
			}
			// Error
			else {
				// Error
				curMusic.setError("Label in invalid Entity: " + getParent());
				return;
			}
		}

		// Book
		if (rawName.equals("publication")) {
			// Music
			if (getParent().equals("bookspec")) { // muss Kindelement von
													// bookspec sein
				curBook.setErscheinungsdatum((parse(atts.getValue("date")))); // Parse
																				// date
				log("Parse Erscheinungsdatum: "
						+ curBook.getErscheinungsdatum());
				return;
			}
			// Error
			else {
				// Error
				curMusic.setError("Ershceinungsdatum in invalid Entity: "
						+ getParent());
				return;
			}
		}
		// ISBN
		if (rawName.equals("isbn")) {
			if (getParent().equals("bookspec")) { // muss Kindelement von
													// bookspec sein
				if (atts.getValue("val").length() != 0) {
					curBook.setISBN(((parse(atts.getValue("val"))))); // Parse
																		// date
					log("Parse ISBN: " + curBook.getISBN());
					return;
				} else {
					curBook.setError("ERROR: ISBN is empty!");
				}
			}
			// Error
			else {
				// Error
				curMusic.setError("ISBN in invalid Entity: " + getParent());
				return;
			}
		}

		// Verlag
		if (rawName.equals("publisher")) {
			if (getParent().equals("publishers")) { // muss Kindelement von
													// publishers sein
				curBook.setVerlag(((parse(atts.getValue("name"))))); // Parse
																		// date
				log("Parse Verlag: " + curBook.getVerlag());
				return;
			}
			// Error
			else {
				// Error
				curMusic.setError("Verlag in invalid Entity: " + getParent());
				return;
			}
		}

		// Author
		if (rawName.equals("author")) {
			if (getParent().equals("authors")) { // muss Kindelement von artists
													// sein
				curBook.addAutor(parse(atts.getValue("name"))); // Pars
																// attributes
				log("Parse Kuenstler: "
						+ curBook.getAutor().get(curBook.getAutor().size() - 1));
				return;
			}
			// Error
			else {
				// Error
				curMusic.setError("Label in invalid Entity: " + getParent());
				return;
			}
		}

		// DVD -----------------------------------------------------
		// Rolle
		if (rawName.equals("director")) {
			if (getParent().equals("directors")) {
				curDVD.addRolle("director", (parse(atts.getValue("name"))));
				echo("Parse director: "
						+ curDVD.getRolle().get(curDVD.getRolle().size() - 1));
				return;
			} else {
				curDVD.setError("ERROR: director is not child of directors!");
			}
		}
		if (rawName.equals("creator")) {
			if (getParent().equals("creators")) {
				curDVD.addRolle("creator", (parse(atts.getValue("name"))));
				echo("Parse creator: " + parse(atts.getValue("name")));
				// echo("Parse creator: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
				return;
			} else {
				curDVD.setError("ERROR: creator is not child of creators!");
			}
		}
		if (rawName.equals("actor")) {
			if (getParent().equals("actors")) {
				curDVD.addRolle("actor", (parse(atts.getValue("name"))));
				echo("Parse actor: " + parse(atts.getValue("name")));
				// echo("Parse actor: "+curDVD.getRolle().get(curDVD.getRolle().size()-1));
				return;
			} else {
				curDVD.setError("ERROR: creator is not child of creators!");
			}
		}

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

			// gesonderte Behandlungsroutinen je nach Elementtyp
			if (rawName.equals("Book") && curBook.isOkay()) { // Commit book
				try {
					addSim(curBook.commit(con));
					logOk("###Daten eingelesen zu Book: " + curBook.getTitel());
					logOk("\nSUCCESS: Commit Book  for book:\nPrint SQL query: "
							+ curBook.getQuery());

					countOk++;
				} catch (SQLException e) {
					countError++;
					// curBook.initQuery();
					logError(curBook.getError()
							+ "\nERROR: Commit Book failed for book:\n"
							+ e.getMessage() + "\nPrint SQL query: "
							+ curBook.getQuery());
					e.printStackTrace();
				}
				curBook.reset();
				return;
			}
			if (rawName.equals("Book") && !curBook.isOkay()) { // Error while committing
				countError++;
				logError(curBook.getError());
				return;
			}

			// DVD
			if (rawName.equals("DVD") && curDVD.isOkay()) { // Commit
				try {
					addSim(curDVD.commit(con));
					logOk("###Daten eingelesen zu DVD: " + curDVD.getTitel());
					logOk("\nSUCCESS!\nPrint SQL query: " + curDVD.getQuery());
					countOk++;
				} catch (SQLException e) {
					countError++;
					// curDVD.initQuery();
					logError(curDVD.getError()
							+ "\nERROR: Commit DVD failed.\n" + e.getMessage()
							+ "\nPrint SQL query: " + curDVD.getQuery());
				}
				curDVD.reset();
				return;
			}
			if (rawName.equals("DVD") && !curDVD.isOkay()) {
				countError++;
				logError(curDVD.getError());
				return;
			}

			// Music
			if (rawName.equals("Music") && curMusic.isOkay()) { // Commit
				try {
					addSim(curMusic.commit(con));
					logOk("###Daten eingelesen zu Music: "
							+ curMusic.getTitel());
					logOk("\nSUCCESS!\nPrint SQL query: " + curMusic.getQuery());

					countOk++;
				} catch (SQLException e) {
					countError++;
					logError(curMusic.getError()
							+ "\nERROR: Commit Music failed! " + e.getMessage()
							+ "\nPrint SQL query: " + curMusic.getQuery());
					e.printStackTrace();
				}
				return;
			}
			if (rawName.equals("Music") && !curMusic.isOkay()) {
				// logError("===>Didn't commit Music: ID:"+curMusic.getProdukt_ID()+", Titel: "+curMusic.getTitel());
				countError++;
				logError(curMusic.getError());
				return;
			}

			// Titel als Name von einem Produkt
			if (rawName.equals("title")
					&& (getParent().equals("Book") || getParent().equals("DVD") || getParent()
							.equals("Music"))) {
				// Book title
				if (getParent().equals("Book")) {
					curBook.setTitel(parse(elementText));
					System.out.println("Parse Titel: " + curBook.getTitel());
					return;
				}
				// DVD title
				if (getParent().equals("DVD")) {
					curDVD.setTitel(parse(elementText));
					System.out.println("Parse Titel: " + curDVD.getTitel());
					return;
				}
				// Musik title
				if (getParent().equals("Music")) {
					curMusic.setTitel(parse(elementText));
					System.out.println("Parse Titel: " + curMusic.getTitel());
					return;
				}
			}

			// Aehnlich
			if (rawName.equals("similars")) {
				// Book title
				if (getParent().equals("Book")) {
					setSimilarCache("Book");
					System.out
							.println("Set Similar for: " + curBook.getTitel());
					return;
				}
				// DVD title
				if (getParent().equals("DVD")) {
					setSimilarCache("DVD");
					System.out.println("Set Similar for: " + curDVD.getTitel());
					return;
				}
				// Musik title
				if (getParent().equals("Music")) {
					setSimilarCache("Music");
					System.out.println("Set Similar for: "
							+ curMusic.getTitel());
					return;
				}
			}
			// asin
			if (rawName.equals("asin")) {
				if (elementText.length() > 0) {
					if (this.similarCache == "Book")
						curBook.addSimilar(parse(elementText));
					else if (this.similarCache == "DVD")
						curDVD.addSimilar(parse(elementText));
					else if (this.similarCache == "Music")
						curMusic.addSimilar(parse(elementText));
					log("Parse Erscheinungsjahr: " + elementText);
					return;
				} else {
					if (this.similarCache == "Book") {
						curBook.setError("ERROR: similar asin is empty!");
						// logError("ERROR: similar asin is empty for: "+curBook.getTitel());
					} else if (this.similarCache == "DVD") {
						curDVD.setError("ERROR: similar asin is empty!");
						// logError("ERROR: similar asin is empty for: "+curDVD.getTitel());
					} else if (this.similarCache == "Music") {
						curMusic.setError("ERROR: similar asin is empty!");
						// logError("ERROR: similar asin is empty for: "+curMusic.getTitel());
					}

				}
			}

			// Music -------------------------------------------------------
			// // Musicspec
			// if (rawName.equals("musicspec")) {
			// // Music
			// if (getParent().equals("Music")) {
			// // continue
			// return;
			// }
			// // Error
			// else{
			// // Error
			// if(getParent().equals("DVD"))
			// curDVD.setError();
			// else if (getParent().equals("DVD"))
			// curBook.setError();
			// logError("Musicspec in invalid Entity: "+getParent());
			// return;
			// }
			// }
			
			// Erscheinungsjahr
			if (rawName.equals("releasedate")) {
				// if (getParent().equals("musicspec") &&
				// elementText.length()>0) {
				if (elementText.length() > 0) {
					curMusic.setErscheinungsjahr(parse(elementText));
					log("Parse Erscheinungsjahr: " + elementText);
					return;
				} else {
//					curMusic.setError("ERROR: Releasedate is empty");
					// logError("ERROR: Releasedate is empty for music: "+curMusic.getProdukt_ID()+", Titel: "+curMusic.getTitel());
					// if(!getParent().equals("musicspec"))
					// logError("ERROR: Releasedate is not child of musicspec!");
				}
			}

			// Lieder
			if (rawName.equals("title") && getParent().equals("tracks")) {
				if (elementText.length() > 0) {
					curMusic.addLied(parse(elementText));
					log("Parse Lied: "
							+ curMusic.getLieder().get(
									curMusic.getLieder().size() - 1));
					return;
				} else {
//					curMusic.setError("ERROR: title from track is empty!");
					// if(!getParent().equals("musicspec"))
					// logError("ERROR: Releasedate is not child of musicspec!");
				}
			}

			// Book -------------------------------------------------------

			// Erscheinungsdatum
			if (rawName.equals("releasedate")) {
				// if (getParent().equals("musicspec") &&
				// elementText.length()>0) {
				if (elementText.length() > 0) {
					curBook.setErscheinungsdatum(parse(elementText));
					log("Parse Erscheinungsdatum: " + elementText);
					return;
				} else {
//					curBook.setError("ERROR: Releasedate is empty!");
					// if(!getParent().equals("musicspec"))
					// logError("ERROR: Releasedate is not child of musicspec!");
				}
			}

			// Seiten
			if (rawName.equals("pages")) {
				if (getParent().equals("bookspec")) {
					if (elementText.length() > 0) {
						curBook.setSeitenzahl(Integer
								.parseInt((parse(elementText))));
						log("Parse Seitenanzahl: " + curBook.getSeitenzahl());
						return;
					} else {
//						curBook.setError("ERROR: Seitenanzahl is empty!");
						// if(!getParent().equals("musicspec"))
						// logError("ERROR: Releasedate is not child of musicspec!");
					}
				} else {
					curBook.setError("ERROR: publisher is not child of publishers");
					// logError("ERROR: publisher is not child of publishers for book: "+curBook.getTitel()+", title: "+curBook.getTitel());
				}
			}

			// DVD -------------------------------------------------------

			// Laufzeit
			if (rawName.equals("runningtime")) {
				if (getParent().equals("dvdspec")) {
					if (elementText.length() > 0) {
						curDVD.setLaufzeit(Integer
								.parseInt((parse(elementText))));
						echo("Parse Laufzeit: " + curDVD.getLaufzeit());
						return;
					} else {
//						curDVD.setError("ERROR: Laufzeit is empty!");
						// if(!getParent().equals("musicspec"))
						// logError("ERROR: Releasedate is not child of musicspec!");
					}
				} else {
					curDVD.setError("ERROR: runningtime is not child of dvdspec!");
				}
			}

			// Format
			if (rawName.equals("format")) {
				// if(getParent().equals("dvdspec"))
				{
					if (elementText.length() > 0) {
						curDVD.setFormat((parse(elementText)));
						echo("Parse Format: " + curDVD.getFormat());
						return;
					} else {
//						curDVD.setError("ERROR: Format is empty!");
						// if(!getParent().equals("musicspec"))
						// logError("ERROR: Releasedate is not child of musicspec!");
					}
				}
				// else{
				// curDVD.setError();
				// }
			}

			// Regioncode
			if (rawName.equals("regioncode")) {
				if (getParent().equals("dvdspec")) {
					if (elementText.length() > 0) {
						curDVD.setRegioncode((parse(elementText)));
						echo("Parse Regioncode: " + curDVD.getRegioncode());
						return;
					} else {
//						curDVD.setError("ERROR: Regioncode is empty!");
						// logError("ERROR: Regioncode is empty!");
						// if(!getParent().equals("musicspec"))
						// logError("ERROR: Releasedate is not child of musicspec!");
					}
				} else {
					curDVD.setError("ERROR: Regioncode is not child of dvdspec!");
					// logError("ERROR: Regioncode is not child of dvdspec!");
				}
			}

		} finally {
			// Element zu ende -> Textinhalt zuruecksetzen
			// notwendig bei mixed content
			elementTextBuf.setLength(0);
			// Element vom Pfad entfernen
			xmlPath.setSize(xmlPath.size() - 1);
		}

	}

	/** Add to sim list*/
	private void addSim(ArrayList<String> commit) {
		for (int i = 0; i < commit.size(); i++)
			this.sim.add(commit.get(i));

	}

	/** Commit similarities
	 * Commit sim after all products were committed -> foreign key issue*/
	private void commitSim() {
		System.out.println("Commit sim");
		// iterate sim list
		for (int i = 0; i < sim.size(); i++) {
			System.out.println(i + ". " + sim.get(i));
			try {
				// execute commit query
				Statement stmt = con.createStatement();
				stmt.executeUpdate(sim.get(i));
				stmt.close();
			} catch (SQLException e) {
				countError++;
				logError("##-----------------\nERROR inserting similiars\n"
						+ e.getMessage() + "\nquery: " + sim.get(i));
				e.printStackTrace();
			}
		}

	}

	/** Cache latest product, for similar check*/
	private void setSimilarCache(String string) {
		this.similarCache = string;

	}

	/** Remove invalid chars */
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

		// Insert Filiale
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("INSERT INTO Filiale VALUES ('Leipzig')");
		} catch (SQLException e) {
			countError++;
			logError("##-----------------\nERROR inserting filiale 'Leipzig'\n"
					+ e.getMessage());
			e.printStackTrace();
		}

		// Parser starten
		try {
			parser.parse(dataFilename);
		} catch (SAXException e) {
			System.err.println("Parser Exception:\n" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Parser IOException:\n" + e.getMessage());
		}

		// Commit similarities
		commitSim();

		// Write
		System.out.println(logError);
		System.out.println("\n\nCount Error: " + countError);
		System.out.println("Count Ok: " + countOk);

		writeErrorLog(logError);
		writeOkLog(logOk);
	}

	/** write error log to file*/
	private void writeErrorLog(String s) {
		System.out.println("Write error log");
		try {
			// Create file
			FileWriter fstream = new FileWriter(
					"resource/logs/errorLog_leipzig.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(s);
			out.write("\nCount Error: " + countError);
			out.write("\nCount OK: " + countOk);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	/** write ok log to file*/
	private void writeOkLog(String s) {
		System.out.println("Write ok log");
		try {
			// Create file
			FileWriter fstream = new FileWriter(
					"resource/logs/okLog_leipzig.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(s);
			out.write("\nCount Error: " + countError);
			out.write("\nCount OK: " + countOk);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	// -------------------------------------------------------------------------

	// debug
	public void echo(String s) {
		System.out.println(s);

	}
	
	// print to sys out
	public void log(String s) {
		System.out.println(s);

	}

	/** Log succesfull datasets*/
	public void logOk(String s) {
		if (s.length() != 0) {
			System.out.println(s);
			logOk += "## " + s + "\n";
		}
	}

	/** Log error datasets*/
	public void logError(String s) {
		if (s.length() != 0) {
			System.out.println(s);
			logError += "## " + s + "\n";
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
		XMLParserLeipzig prg = new XMLParserLeipzig();
		// ausfuehren
		prg.doit("resource/data_ms/leipzig.xml");

	}

}
