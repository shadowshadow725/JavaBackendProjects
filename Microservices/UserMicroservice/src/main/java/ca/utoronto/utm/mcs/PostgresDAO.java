package ca.utoronto.utm.mcs;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgresDAO {
	
	public Connection conn;
    public Statement st;

	public PostgresDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("POSTGRES_ADDR");
        String url = "jdbc:postgresql://" + addr + ":5432/root";
		try {
            Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(url, "root", "123456");
            this.st = this.conn.createStatement();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// *** implement database operations here *** //



    public ResultSet registerUser(String email, String name, String password) throws SQLException {
	    String query = "INSERT INTO users ( email, password, prefer_name, rides, isdriver) VALUES( '%s', '%s', '%s', 0, false) RETURNING uid;";
	    query = String.format(query, email, password, name);
	    return this.st.executeQuery(query);
    }

    public boolean checkEmailExist(String email) throws SQLException {
	    String query = "SELECT COUNT(*) FROM users WHERE email = '%s';";
	    query = String.format(query, email);
        ResultSet r = this.st.executeQuery(query);
        r.next();

        if (r.getInt("count") > 0){
            return true;
        }
        return false ;

    }

    public int loginUser(String email, String password) throws SQLException{
	    String query = "SELECT uid FROM users WHERE email = '%s' AND password = '%s';";
	    query = String.format(query, email, password);
        ResultSet r = this.st.executeQuery(query);
        r.next();
        int uid;
        try {
            uid = r.getInt("uid");
        }
        catch (Exception e){
            return -1;
        }
        return uid;


    }


    public ResultSet getUsersFromUid(int uid) throws SQLException {
        String query = "SELECT * FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public ResultSet getUserData(int uid) throws SQLException {
        String query = "SELECT prefer_name as name, email, rides, isdriver FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides, Boolean isDriver) throws SQLException {

        String query;
        if (email != null) {
            query = "UPDATE users SET email = '%s' WHERE uid = %d";
            query = String.format(query, email, uid);
            this.st.execute(query);
        }
        if (password != null) {
            query = "UPDATE users SET password = '%s' WHERE uid = %d";
            query = String.format(query, password, uid);
            this.st.execute(query);
        }
        if (prefer_name != null) {
            query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
            query = String.format(query, prefer_name, uid);
            this.st.execute(query);
        }
        if ((rides != null)) {
            query = "UPDATE users SET rides = %d WHERE uid = %d";
            query = String.format(query, rides, uid);
            this.st.execute(query);
        }
        if (isDriver != null) {
            query = "UPDATE users SET isdriver = %s WHERE uid = %d";
            query = String.format(query, isDriver.toString(), uid);
            this.st.execute(query);
        }
    }
}
