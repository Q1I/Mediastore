import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ReviewParser {

	private Review curReview;
	private Connection con;
	private String url = "jdbc:mysql://localhost/Mediastore";
	private String userName = "root";
	private String password = "root";

//	private static final String userName = "dbprak20";
//	private static final String password = ".P$RAK20";
//	private static final String url = "jdbc:db2://leutzsch.informatik.uni-leipzig.de:50001/dbprak12";

	
	private int countError;
	private int countOk;
	
	private String error="";
	
	public ReviewParser() {
		con = getConnection();
		curReview = new Review();
		countError=0;
		countOk=0;
	}

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
			Class.forName("com.mysql.jdbc.Driver"); // JDBC Driver
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(this.url, this.userName,
					this.password);
			// con = DriverManager.getConnection(this.url);
			System.out.println("Connected to database");
		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return con;
	}

	public void run() {
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("resource/data_ms/reviews.csv");

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {

				// Print the content on the console
				System.out.println(strLine);
				if (strLine.length() > 0) {
					processLine(strLine);
					if (curReview.isOkay()) {
						try {
							curReview.commit(con);
							countOk++;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logError(curReview.getError()+"\nERROR: Commit Book failed for book:\n"+e.getMessage()+"\nPrint SQL query: "+curReview.getQuery());
						}
					}
					else{
						countError++;
						logError(curReview.getError());
					}
				}
			}
			// Close the input stream
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println(error);
		System.out.println("Count ok: "+countOk);
		System.out.println("Count error: "+countError);
		
		writeErrorLog(error);
//		writeOkLog(logOk);
	}

	private void writeErrorLog(String s) {
		System.out.println("Write error log");
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter("resource/logs/errorLog_Review.txt");
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
					"resource/logs/okLog_Review.txt");
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
	
	private void logError(String error) {
		this.error+=error+"\n";
		
	}

	private void processLine(String strLine) {
		curReview.reset();
		String[] split = strLine.split("\",\"");
		if(split.length<5){
			curReview.setError("Error: Invalid syntax! Not in the form of: product,'rating','helpful','reviewdate','user','summary','content'\nLine: "+strLine);
			return;
		}
//		for (int i = 0; i < split.length; i++) {
//			System.out.println(i + ". split: " + split[i]);
//		}
		// Produkt_ID
		if (split[0].length() > 1) {
			String id = split[0].replace("\"","");
			curReview.setProdukt_ID(id);
			log("Parse ID: " + curReview.getProdukt_ID());
		}else{
			curReview.setError("ERROR: Invalid ID = "+split[0]);
			return;
		}
		// Punkte
		if (split[1].length() > 0) {
			try{
			int punkte = Integer.parseInt(split[2]);
			curReview.setPunkte(punkte);
			log("Parse Punkte: " + curReview.getPunkte());
			} catch(Exception e){
				curReview.setError("ERROR: Invalid points! Points = "+split[2]);
			}
		}else
			curReview.setError("ERROR: Points is empty!");
		
		// Kunden_ID
		if (split[4].length() > 0) {
			curReview.setKunden_ID(parse(split[4]));
			log("Parse Kunde: " + curReview.getKunden_ID());
		}
		else
			curReview.setError("ERROR: Kunden_ID is empty!");
			
		
		// Rezension
		if (split[5].length() > 0) {
			String rez = split[5];
			curReview.setRezension(parse(rez));
			log("Parse Rezension: " + curReview.getRezension());
		}
		else
			curReview.setError("ERROR: Rezension is empty!");
		
		// Inhalt
		if (split[6].length() > 0) {
			String inhalt = split[6].substring(0, split[6].lastIndexOf('"'));
			curReview.setInhalt(parse(inhalt));
			log("Parse Inhalt: " + curReview.getInhalt());
		}
		else
			curReview.setError("ERROR: Rezension is empty!");

	}

	private String parse(String s) {
		String parsed = s.replaceAll("'", "`");
		return parsed;
	}

	private void log(String s) {
		// TODO Auto-generated method stub
		System.out.println(s);
	}

	public static void main(String args[]) {
		ReviewParser p = new ReviewParser();
		p.run();
	}
}
