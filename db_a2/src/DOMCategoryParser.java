import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class DOMCategoryParser {
	
	private ArrayList<Category> categoryList = new ArrayList<Category>();
	
	private String url="jdbc:mysql://localhost/Mediastore";
	private String userName="root";
	private String password="root";
//	
//	private static final String userName = "dbprak20";
//	private static final String password = ".P$RAK20";
//	private static final String url = "jdbc:db2://leutzsch.informatik.uni-leipzig.de:50001/dbprak12";

	
	private Category curCat;
	private String curHCat=null;
	
	private String logError;
	private String log;
	private String logOk;
	
	public int countError=0;
	public int countOk=0;
	
	public DOMCategoryParser(){
		logError="\n\n===========================================================" +
		"\nError-Log:\n";
		log="Log:\n";
		logOk="\n\n===========================================================" +
		"\nOk-Log:\n";
		
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
		    builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();  
		}
		try {
		    Document document = builder.parse(
		            new FileInputStream("resource/data_ms/categories.xml"));
		    traverse(document);
		    
		    // commit
		    commit();
		    
		    // Write logs
		    writeErrorLog(logError);
		    writeOkLog(logOk);

		    System.out.println("\n\nDONE!");
		} catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}finally{
			categoryList=null;
		}

	}

	
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
			con = DriverManager.getConnection(url, userName, password);
			//con = DriverManager.getConnection(this.url);
			System.out.println("Connected to database");
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return con;
	}
	
	private void commit() {
		Connection con = getConnection();
	    System.out.println("Commit Categories! Size: "+categoryList.size());
	    
	    
//		for(int i=0;i<50;i++)
		for(int i=0;i<categoryList.size();i++)
		{
			curCat=categoryList.get(i);
			if(curCat.isOkay()){
				try {
					
					curCat.commit(con);
					logOk("\nSUCCESS: Commit Category: "+ curCat.getCat()+"\nPrint SQL query: "+curCat.getQuery());
					countOk++;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logError("ERROR Commit: "+e.getMessage()+ "\nSQL Query: "+curCat.getQuery());
					countError++;
				}
			}else{
				logError(curCat.getError());
				countError++;
			}
				
		
		}
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Count error: "+countError);
		System.out.println("Count success: "+countOk);
	}

	public void logOk(String s) {
		if(s.length()!=0){
			System.out.println(s);
			// log normal 
			// log error in file
			logOk+="## "+s+"\n";
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


	private void traverse(Document document) {
		Element rootElement = document.getDocumentElement(); // categories
//		String parentName = rootElement.getFirstChild().getNodeValue().trim();
		
	    NodeList mainCategory = rootElement.getChildNodes(); // 
//	    for(int i=0; i<50; i++)
	    for(int i=0; i<mainCategory.getLength(); i++)
	    {
	      Node node = mainCategory.item(i);
	      if(node instanceof Element){
	        //a child element to process
//	        Element child = (Element) node;
//	        String attribute = child.getAttribute("width");
	    	  
	    	  processCategory(node,null);
	        
		      
	      }
	    }
	}

	private void processCategory(Node node, Category parent) {
		
		String name = node.getFirstChild().getNodeValue().trim();
		System.out.println("Category Name: "+name+ ", Parent: "+parent);
//		if(node.getFirstChild() instanceof Element){
//			System.out.println("isso");
//		}else System.out.println("noot");
		
		// set name and add to list
		if (parent ==null){
			curHCat=name;
			parent= new Category();
//			parent.setCat(null);
		}
		
		Category cat = new Category();
		cat.setCat(parse(name));
		cat.setoCat(parent);
		cat.sethCat(curHCat);
		categoryList.add(cat);
		
		// children can be category or items
		processChildren(node.getChildNodes(),cat);
		
	}

	private String parse(String trim) {
		if(trim ==null)
			return null;
		String parsed = trim.replaceAll("'", "`");
		parsed = parsed.replace('"', '`');
		return parsed;
	}


	/** children can be category or items */
	private void processChildren(NodeList childNodes, Category c) {
		 for(int i=0; i<childNodes.getLength(); i++)
		    {
		      Node node = childNodes.item(i);
		      if(node instanceof Element){
		    	  if(node.getNodeName().trim().equals("category"))
		    		  processCategory(node,c);
		    	  else
		    		  processItem(node,c);
			      
		      }
		    }
	}

	private void processItem(Node node,Category c) {
		String item = node.getTextContent();
		System.out.println("item: "+item);
		c.addItem(item);
		
	}

	private void writeErrorLog(String s) {
		System.out.println("Write error log");
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter("resource/logs/errorLog_Category.txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(s);
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		
	}

	private void writeOkLog(String s) {
		System.out.println("Write ok log");
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter("resource/logs/okLog_Category.txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(s);
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		
	}
	
	public static void main(String args[]){
		DOMCategoryParser d = new DOMCategoryParser();
	}
	
}
