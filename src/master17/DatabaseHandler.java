package master17;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHandler {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://mysql.stud.ntnu.no:3306/haakosh_markovgame17";

	static final String USER = "haakosh_master";
	static final String PASS = "project16";
	static Connection conn = null;

	public void insertEvents(ArrayList<Event> eventList) throws ClassNotFoundException, SQLException{

		openConnection();
		Statement stmt = conn.createStatement();

		String sql="";
		for(Event e : eventList){
			sql="INSERT INTO Event (EventID,Action,TeamID,PlayerID,GameID,XStart,YStart,Number,Sequence,Minute,Second,"
					+"ManpowerDifference,GoalDifference,Period)"+"\n"+"VALUES "+"("+e.getEvent_id()+",'"+e.getAction_type()+"',"+e.getTeam_id()+","+e.getPlayer_id()+","
					+e.getGame_id()+","+e.getXstart()+","+e.getYstart()+","+e.getNumber()+","+e.getSequence()+","+e.getMinute()+","+e.getSecond()+","+e.getManpowerdifference()+
					","+e.getGoaldifference()+","+e.getPeriod()+")"+";\n";
			stmt.addBatch(sql);

		}


		int [] updateCounts = stmt.executeBatch();


		closeConnection();

	}




	public static void openConnection() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Connected database successfully...");

	}

	public static void closeConnection() throws SQLException{
		conn.close();
		System.out.println("Connection closed...");
	}

	public static Connection returnConnection() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");

		System.out.println("Connecting to database...");

		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Connected database successfully...");
		return conn;




	}

}
