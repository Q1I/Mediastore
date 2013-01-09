/**
 * Enables to get a Connection to Database.
 */
package media.mediadbimpl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author Silvio Paschke
 * @since 01/2006
 */
public class DatabaseConnection {

	private Connection conn = null;

	private Driver jdbcdriver = null;

	private DatabaseMetaData dbmd = null;

	private boolean isConnected = false;

	private Properties dbprop = new Properties();


	private String databasename = "";

	/**
	 * Constructor Loads JDBC-Driver for DB2 UDB.
	 *
	 * @throws Exception
	 */
	public DatabaseConnection() throws Exception {

	}

	/**
	 * Retrieving Metadata of db connection.
	 *
	 * @return DatabaseMetadata
	 */
	public DatabaseMetaData getDatabaseMetaData() {
		return this.dbmd;
	}


	/**
	 * Connects to DB and lists some specifications.
	 *
	 * @throws Exception
	 */
	public void connect(String connection_id, Properties prop)
			throws Exception {
		if (connection_id == null || connection_id.trim().equals(""))
			throw new Exception("Cannot connect to empty database identifier.");

		String dump = prop.getProperty(connection_id);
		if (dump == null) throw new Exception("Database identifier is not defined.");
		StringTokenizer tok = new StringTokenizer(dump, ",");
		String driver = null;
		String connectStr = null;
		String userName = null;
		String pw = null;

		try {
			driver = tok.nextToken().trim();
			connectStr = tok.nextToken().trim();
			userName = tok.nextToken().trim();
			pw = tok.nextToken().trim();
		} catch (Exception e) {
			throw new Exception("Connection alias is" +
					" not correctly defined.\n" + e);
		}
		this.jdbcdriver = (Driver) Class.forName(driver)
				.newInstance();

		System.out.println("\n\nConnect with " + driver + " to " + connectStr + " by " + userName
				+ " ...");
		dbprop.put("user", userName);
		dbprop.put("password", pw);
		this.conn = this.jdbcdriver.connect(connectStr, dbprop);

		//this.conn.setHoldability(2*this.conn.getHoldability());

		this.dbmd = this.conn.getMetaData();

		System.out.println("\nDATABASE::\t" + dbmd.getDatabaseProductName()
				+ " " + dbmd.getDatabaseProductVersion());
		System.out.println("DB-VERSION:\t" + dbmd.getDatabaseMajorVersion() +
				"." + dbmd.getDatabaseMinorVersion());
		System.out.println("URL::\t\t" + dbmd.getURL());
		System.out.println("USER::\t\t" + dbmd.getUserName());
		System.out.println("DRIVER::\t" + dbmd.getDriverName() + " "
				+ dbmd.getDriverVersion());
		System.out.println("SQLTYPE::\t" + dbmd.getTypeInfo());
		System.out.println("KEYWORDS::\t" + dbmd.getSQLKeywords());
		System.out.println("SYSFUNC::\t" + dbmd.getSystemFunctions());
		System.out.println("\nCompatibility - Parameters:");
		System.out.println("Core SQL:\t\t" + dbmd.supportsMinimumSQLGrammar());
		System.out.println("Ext. SQL:\t\t" + dbmd.supportsExtendedSQLGrammar());
		System.out.println("Full Outerjoin:\t\t" + dbmd.supportsFullOuterJoins());
		System.out.println("Batch-Updates:\t\t" + dbmd.supportsBatchUpdates());
		System.out.println("All tables selectable:\t" + dbmd.allTablesAreSelectable());
		this.databasename = dbmd.getDatabaseProductName() + " " +
				dbmd.getDatabaseProductVersion();

		this.isConnected = true;
	}

	/**
	 * Disconnects from DB2.
	 *
	 * @throws Exception
	 */
	public void disconnect() throws Exception {
		if ((this.conn != null) && (this.isConnected))
			this.conn.close();
		this.isConnected = false;
		System.out.println("\nDisconnected from " + this.databasename);
	}// end of disconnect

	/**
	 * Return the DB2-Connection.
	 *
	 * @return Connection
	 * @throws Exception
	 */
	public Connection getConn() throws Exception {
		if (!this.isConnected)
			throw new Exception("Currently not connected.");
		return this.conn;
	}// end of getConn


}
