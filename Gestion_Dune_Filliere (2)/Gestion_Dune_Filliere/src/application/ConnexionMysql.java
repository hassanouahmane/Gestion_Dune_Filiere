package application;

import java.sql.*;

public class ConnexionMysql {
	
	private static final String DB_NAME = "hassan";
	private static final String DB_IP= "127.0.0.1:3306";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "";
	
	public static Connection ConnexionDB() {
		Connection connection = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://"+DB_IP+"/"+DB_NAME, DB_USER,DB_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return connection;
	}	
	
}
