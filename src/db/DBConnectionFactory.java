package db;

import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";

	public static DBConnection getDBConnection(String db) {
		switch (db) {
			case "mysql":
				return MySQLConnection.getInstance();
			default:
				throw new IllegalArgumentException("invalid db" + db);
		}
	}

	public static DBConnection getDBConnection() {
		return MySQLConnection.getInstance();
	}

}
