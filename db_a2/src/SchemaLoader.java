import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class SchemaLoader {

//    private static final String USER = "dbprak20";  
//    private static final String PASSWORD = ".P$RAK20";  
//    private static final String URL = "jdbc:db2://leutzsch.informatik.uni-leipzig.de:50001/dbprak12";

    	  private static String URL="jdbc:mysql://localhost/Mediastore";
		private static String USER="root";
		private static String PASSWORD="root";
	  
	  
    public static Connection getConnection(String url){
		System.out.println("Connecting to database..");
		Connection con = null;
		try {
//			Class.forName("com.ibm.db2.jcc.DB2Driver");
			Class.forName("com.mysql.jdbc.Driver");
		} catch(java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			  
           // here change url
			con = DriverManager.getConnection(URL, USER, PASSWORD);
			//con = DriverManager.getConnection(this.url);
			System.out.println("Connected to database");
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return con;
	}
    
    
	public static void loadSchema(String path){
		System.out.println("Load Schema");
		try {
			String line;
			StringBuffer buffer = new StringBuffer();
			
			FileReader fr = new FileReader(new File(path));
			BufferedReader br = new BufferedReader(fr);
			
			// SQL file einlesen
            while((line = br.readLine()) != null)    
            { 
            	buffer.append(line);
            }
            br.close();
            
            // Split bei delimiter=';'
            String[] queries = buffer.toString().split(";");
          
            // test connect to localhost (local test)
            String url = "jdbc:mysql://" + "localhost"; 
            // Verbindung herstellen
            Connection con = getConnection(url);
            // Statement erstellen
            Statement stmt = con.createStatement();
//            stmt.execute("CREATE database Mediastore");
////            
//            url = "jdbc:mysql://" + "localhost"+"/Mediastore"; 
//            // Verbindung herstellen
//            con = getConnection(url);
//            // Statement erstellen
//            stmt = con.createStatement();
            
            // Execute queries 4 create tables and views
            System.out.println("Execute Queries:");
            for (int i=0; i<queries.length;i++){
            	if(queries[i].trim().length()!=0){
            		stmt.execute(queries[i]);
            		System.out.println("Execute Query:\n"+queries[i]);
            	}
            }
            con.close();
            
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: IOException!");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("ERROR: SQL Exception!");
			e.printStackTrace();

			// Local test, drop db if sql error
            String url = "jdbc:mysql://" + "localhost" + "/" + "Mediastore"; 
            Connection con = getConnection(url);
            try {
				Statement stmt = con.createStatement();
				stmt.execute("DROP DATABASE Mediastore");
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		System.out.println("Load Schema Done");
	}

	public static void main(String args[]){
		loadSchema("resource/dbprak20_schema.sql");
	}
}
