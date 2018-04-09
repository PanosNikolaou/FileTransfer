package mypackage;

import java.sql.DriverManager;
import com.mysql.jdbc.*;

public class DBFunctions {

	
	public Connection openDBConnection() throws Exception{
		
		String sURL="jdbc:mysql://localhost:3306/transfile";
		String sUserName="root";
		String sPwd="testtest!";
		Connection conn = null;
		
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		conn = (Connection) DriverManager.getConnection(sURL,sUserName,sPwd);
			
		return conn;
	} //openDBConnection
 
 
	public void closeDBConnection(Connection conn) throws Exception{
		conn.close();
	} //closeDBConnection
	
} //DBFunctions
