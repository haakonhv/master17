package master17;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
			sql="INSERT INTO Event (Action,Outcome,TeamID,PlayerID,GameID,XStart,YStart,Xend,Yend,Number,Sequence,Minute,Second,"
					+"ManpowerDifference,GoalDifference,Period,OptaEventID)"+"\n"+"VALUES "+"('"+e.getAction_type()+"',"+e.getOutcome()+","+e.getTeam_id()+","+e.getPlayer_id()+","
					+e.getGame_id()+","+e.getXstart()+","+e.getYstart()+","+e.getXend()+","+e.getYend()+","+e.getNumber()+","+e.getSequence()+","+e.getMinute()+","+e.getSecond()+","+e.getManpowerdifference()+
					","+e.getGoaldifference()+","+e.getPeriod()+","+e.getEvent_id()+")"+";\n";
			stmt.addBatch(sql);

		}

		int [] updateCounts = stmt.executeBatch();
		closeConnection();

	}

	public void insertStates(ArrayList<State> stateList) throws ClassNotFoundException, SQLException{
		Statement stmt = conn.createStatement();
		int home;
		String sql="";
		for(State s : stateList){
			if (s.isHome()){
				home = 1;
			}
			else{
				home = 0;
			}
			sql="INSERT INTO State (StateID,Zone,Action,Home,Period,ManpowerDifference,MatchStatus,Occurrence,Reward)"+
				"\n"+"VALUES "+"("+s.getStateID()+","+s.getZone()+",'"+s.getAction()+"',"+home+","+s.getPeriod()+","+s.getManpowerDiff()+","+
				s.getMatchStatus()+","+s.getOccurrence()+","+s.getReward()+")"+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		//closeConnection();
	}

	public void insertStateTransitions(ArrayList<StateTransition> stateTransList) throws SQLException{
		Statement stmt = conn.createStatement();
		int home;
		String sql="";
		for(StateTransition st : stateTransList){

			sql="INSERT INTO StateTransition (StartID, EndID, Occurrence) VALUES ("+st.getStartStateID()+","+st.getEndStateID()+","+st.getOccurrence()+")";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		//closeConnection();
	}

	public void updatePlayers(ArrayList<Player> playerlist) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		for (Player p : playerlist){
			sql = "UPDATE Player SET Weight=" + p.getWeight() + ",Side=" + "'" +p.getSide() + "'" + ",Position=" + "'"+p.getPosition()+"'" + " WHERE PlayerID=" + p.getId() + ";\n";
			stmt.addBatch(sql);
		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
		System.out.println(updateCounts);
	}



	public static void openConnection() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Connected database successfully...");

	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}


	public static void closeConnection() throws SQLException{
		conn.close();
		System.out.println("Connection closed...");
	}



	public static ResultSet getDatabaseEvents(int gameID) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM Event WHERE GameID="+gameID;
		ResultSet rs = stmt.executeQuery(query);
		return rs;

	}
	public static ResultSet getDatabaseStates() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM State";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	public static ResultSet getDatabaseStateTrans() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM StateTransition";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}


	public static void updateEventStateID(ArrayList<String> sqlList) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		for(String s : sqlList){
			stmt.addBatch(s);

		}
		int [] updateCounts = stmt.executeBatch();

	}

	public static ResultSet getOrderedEvents(int gameID) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM Event WHERE GameID="+gameID+" ORDER BY Number ASC";
		ResultSet rs = stmt.executeQuery(query);
		return rs;

	}

	public static void updateQValues(ArrayList<State> stateList) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for(State s : stateList){
			sql = "UPDATE State SET QValue=" + s.getqValue()+" WHERE StateID = "+s.getStateID()+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertGames(ArrayList<Game> gameList) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for(Game g : gameList){

			sql = "INSERT INTO Game VALUES ("+g.getGame_id()+","+g.getHome_team_id()+","+g.getAway_team_id() + "," + g.getMatchday() + "," + g.getSeason() +");\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static ResultSet getPlayers() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT PlayerID FROM Player";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static ResultSet getEventsAndValues() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.TeamID, E.EventID, E.PlayerID, E.Action, E.GameID, E.StateID, S.QValue, G.HomeID, G.AwayID \n"
				+ "FROM Event AS E \n"
				+ "INNER JOIN State AS S ON E.StateID=S.StateID \n"
				+ "INNER JOIN Game AS G ON E.GameID=G.GameID \n"
				+ "ORDER BY EventID ASC;";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
}
