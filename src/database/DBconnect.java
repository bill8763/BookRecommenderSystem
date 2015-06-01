package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnect {
	String user = "mgtadmin";
    String pass = "1qa2ws3ed";
    String database = "bookrating";
    String url = "jdbc:mysql://localhost/bookrating";
    String driver = "com.mysql.jdbc.Driver";
    private static Connection conn;
	protected Statement stmt;
	public static Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public DBconnect() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		try{
	        
			Class.forName(driver);
			setConn(DriverManager.getConnection(url,user,pass));
			stmt = getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
	                      
	    }
	    catch(SQLException sqle){
			System.out.println("SQL Exception : " + sqle);
	    }
	}
}