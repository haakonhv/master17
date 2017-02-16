package master17;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class DatabaseHandler {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://mysql.stud.ntnu.no:3306/haakosh_markovtest";

	static final String USER = "haakosh_master";
	static final String PASS = "project16";
	static Connection conn = null;

	public void insertEvents(ArrayList<Event> eventList) throws ClassNotFoundException, SQLException{

		openConnection();
		Statement stmt = conn.createStatement();

		String sql="";
		for(Event e : eventList){
			sql="INSERT INTO Event (Action,Outcome,TeamID,PlayerID,GameID,XStart,YStart,Xend,Yend,Number,Sequence,Minute,Second,"
					+"GoalDifference,Period,OptaEventID)"+"\n"+"VALUES "+"('"+e.getAction_type()+"',"+e.getOutcome()+","+e.getTeam_id()+","+e.getPlayer_id()+","
					+e.getGame_id()+","+e.getXstart()+","+e.getYstart()+","+e.getXend()+","+e.getYend()+","+e.getNumber()+","+e.getSequence()+","+e.getMinute()+","+e.getSecond()+","
					+e.getGoaldifference()+","+e.getPeriod()+","+e.getEvent_id()+")"+";\n";
			stmt.addBatch(sql);

		}

		int [] updateCounts = stmt.executeBatch();
		closeConnection();

	}

	public static void insertStates(ArrayList<State> stateList) throws ClassNotFoundException, SQLException{
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
				"\n"+"VALUES "+"("+s.getStateID()+","+s.getZone()+",'"+s.getAction()+"',"+home+","+s.getPeriod()+","+
				s.getMatchStatus()+","+s.getOccurrence()+","+s.getReward()+")"+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		//closeConnection();
	}

	public static void insertStateTransitions(ArrayList<StateTransition> stateTransList) throws SQLException{
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



	public static ResultSet getDatabaseEvents() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.EventID, E.Action, E.Minute, E.Period, E.GoalDifference, E.TeamID, E.ManpowerDifference, E.Xstart, E.Ystart,"
				+ "G.HomeID, G.AwayID FROM Event AS E INNER JOIN Game AS G ON E.GameID = G.GameID";
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

	public static ResultSet getOrderedEvents() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM Event ORDER BY EventID ASC";
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
		String query = "SELECT E.TeamID, E.EventID, E.PlayerID, E.Action, E.GameID, E.StateID, S.QValue, S.Zone, G.HomeID, G.AwayID \n"
				+ "FROM Event AS E \n"
				+ "INNER JOIN State AS S ON E.StateID=S.StateID \n"
				+ "INNER JOIN Game AS G ON E.GameID=G.GameID \n"
				+ "ORDER BY EventID ASC;";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static void insertPlayerValues (ArrayList<PlayerValues> playerValueList) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for(PlayerValues pv : playerValueList){
			pv.setTotal();
			sql = "INSERT INTO PlayerValues VALUES ("+pv.getPlayerID()+"," +pv.getTotal() +","+pv.getPass() +","+ pv.getLongPass() + "," + pv.getBallCarry() + "," + pv.getBallRecovery() + "," + pv.getBallReceived() +
					"," + pv.getAerialDuel() + "," + pv.getClearance() + "," + pv.getThrowInTaken() + "," + pv.getBallTouch() + "," + pv.getInterception() + "," + pv.getBlockedShot() + "," + pv.getSavedShot() + "," + pv.getCross()
					+ "," + pv.getTackle() + "," + pv.getShot() + "," + pv.getTakeOn() + "," + pv.getFreekickPass() + "," + pv.getFoulCommitted() + "," + pv.getFouled()
					+"," +pv.getDispossessed() + "," + pv.getCornerTaken()+");\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertPlayerGameTime (Hashtable<Integer, PlayerGameTime> playerGameTimeTable) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		Set<Integer> keys = playerGameTimeTable.keySet();
		for(int id : keys){
			int time14 = playerGameTimeTable.get(id).getSeason2014();
			int time15 = playerGameTimeTable.get(id).getSeason2015();
			int time16 = playerGameTimeTable.get(id).getSeason2016();
			int time17 = playerGameTimeTable.get(id).getSeason2017();
			int total = time14+time15+time16+time17;
			sql = "UPDATE PlayerGameTime SET S2014 = "+time14+ ", S2015 = "+time15+ ", S2016 = "+time16+ ", S2017 = "+time17+ ", Total = "+total+
					" WHERE PlayerID = "+id+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}
}
