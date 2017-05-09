package master17;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import Freekick.FreeKick;
import markov2.State;
import markov2.StateAction;
import markov2.StateTransition;
import markov2.StateActionNext;

public class DatabaseHandler {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://mysql.stud.ntnu.no:3306/haakosh_markovmodel2";

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

	public static void insertStates(ArrayList<master17.State> stateList) throws ClassNotFoundException, SQLException{
		Statement stmt = conn.createStatement();
		int home;
		String sql="";
		for(master17.State s : stateList){
			if (s.isHome()){
				home = 1;
			}
			else{
				home = 0;
			}
			sql="INSERT INTO State (StateID,Zone,Action,Home,Period,MatchStatus,Occurrence,Reward)"+
				"\n"+"VALUES "+"("+s.getStateID()+","+s.getZone()+",'"+s.getAction()+"',"+home+","+s.getPeriod()+","+
				s.getMatchStatus()+","+s.getOccurrence()+","+s.getReward()+")"+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		//closeConnection();
	}

	public static void insertStateTransitions(ArrayList<master17.StateTransition> stateTransList) throws SQLException{
		Statement stmt = conn.createStatement();
		int home;
		String sql="";
		for(master17.StateTransition st : stateTransList){

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
		String query = "SELECT E.EventID, E.Action, E.Minute, E.Period, E.GoalDifference, E.TeamID, E.Xstart, E.Ystart,"
				+ "G.HomeID, G.AwayID FROM Event AS E INNER JOIN Game AS G ON E.GameID = G.GameID";
		ResultSet rs = stmt.executeQuery(query);
		return rs;

	}

	public static ResultSet getDatabaseStateAction() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM StateAction2";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static ResultSet getDatabaseEventsModel2() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.EventID, E.Action, E.Outcome, E.Minute, E.Period, E.GoalDifference, E.TeamID, E.Xstart, E.Ystart, E.Xend, E.Yend, E.Number, "
				+ "G.HomeID, G.AwayID FROM Event AS E INNER JOIN Game AS G ON E.GameID = G.GameID WHERE E.Action != 'Ball received' AND E.Action != 'Fouled' AND !(Action = 'Take on' AND Outcome = 0) "
				+ "AND !(Action = 'Aerial duel' AND Outcome=0);";
		ResultSet rs = stmt.executeQuery(query);
		return rs;

	}

	public static ResultSet getDatabaseStates() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM State2";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	public static ResultSet getDatabaseStateTrans() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM StateTransition2";
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

	public static ResultSet getOrderedEventsJoinTrans() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM Event AS E INNER JOIN StateTransition2 AS ST ON E.StateTransitionID=ST.TransitionID "
				+ "INNER JOIN State as End ON ST.EndID = End.StateID WHERE E.StateTransitionID IS NOT NULL ORDER BY EventID ASC";
		ResultSet rs = stmt.executeQuery(query);
		return rs;

	}

	public static void updateQValues(ArrayList<master17.State> stateArray) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for(master17.State s : stateArray){
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

	public static ResultSet getFullPlayers() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM Player";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static ResultSet getEventsAndValues() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.TeamID, E.EventID, E.PlayerID, E.Action, E.GameID, E.StateID, S.QValue, S.Zone, S.Period, S.Home, S.MatchStatus, G.HomeID, G.AwayID \n"
				+ "FROM Event AS E \n"
				+ "INNER JOIN State AS S ON E.StateID=S.StateID \n"
				+ "INNER JOIN Game AS G ON E.GameID=G.GameID \n"
				+ "WHERE G.SeasonID=2014 \n"
				+ "ORDER BY EventID ASC;";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static ResultSet getEventsAndValuesMod2() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.Number, E.EventID, E.Action, E.TeamID, E.PlayerID, E.GameID, E.StateTransitionID, "
				+ "ST.TransitionID, ST.StartID, ST.EndID, SA.StateID, SA.Action, SA.Value AS QValue, StartS.Value AS StartValue, EndS.Value AS EndValue, EndS.Reward AS Endreward, G.HomeID, G.AwayID "
				+ "FROM `Event` AS E "
				+ "INNER JOIN StateTransition2 AS ST ON E.StateTransitionID=ST.TransitionID "
				+ "INNER JOIN State2 AS StartS ON ST.StartID=StartS.StateID "
				+ "INNER JOIN StateAction2 AS SA ON ST.StartID=SA.StateID "
				+ "INNER JOIN State2 AS EndS	ON ST.EndID=EndS.StateID "
				+ "INNER JOIN Game AS G	ON E.GameID=G.GameID WHERE SA.Action = E.Action "
				+ "ORDER BY E.EventID ASC";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static void insertPlayerValues (ArrayList<PlayerValues> playerValueList) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for(PlayerValues pv : playerValueList){
			pv.setAverageActionValues();
			pv.setTotal();
			sql = "INSERT INTO PlayerValues VALUES ("+pv.getPlayerID()+"," + pv.getGameID() + "," + pv.getTeamID() + "," +pv.getTotal() +","+pv.getPass() +","+ pv.getLongPass() + "," + pv.getBallCarry() + "," + pv.getBallRecovery() + "," + pv.getBallReceived() +
					"," + pv.getAerialDuel() + "," + pv.getClearance() + "," + pv.getThrowInTaken() + "," + pv.getBallTouch() + "," + pv.getInterception() + "," + pv.getBlockedShot() + "," + pv.getSavedShot() + "," + pv.getCross()
					+ "," + pv.getTackle() + "," + pv.getShot() + "," + pv.getHeadedShot() + "," + pv.getTakeOn() + "," + pv.getFreekickPass() + "," + pv.getFoulCommitted() + "," + pv.getFouled()
					+"," +pv.getDispossessed() + "," + pv.getCornerTaken()+");\n";
			System.out.println(sql);
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertPlayerGameTime (Hashtable<Integer, Hashtable<Integer, PlayerGameTime>> teamGameTimeTable) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;

		Set<Integer> teamKeys = teamGameTimeTable.keySet();

		for(int teamID : teamKeys){
			Hashtable<Integer, PlayerGameTime> playerGameTime = teamGameTimeTable.get(teamID);
			Set<Integer> playerKeys = playerGameTime.keySet();
			for (int playerID : playerKeys){
				int time14 = playerGameTime.get(playerID).getSeason2014();
				int time15 = playerGameTime.get(playerID).getSeason2015();
				int time16 = playerGameTime.get(playerID).getSeason2016();
				int time17 = playerGameTime.get(playerID).getSeason2017();
				int total = time14+time15+time16+time17;
				sql = "INSERT INTO PlayerGameTime VALUES ("+ playerID +"," + teamID + "," + time14 + "," + time15 + ","+ time16 + "," + time17 + "," + total +");\n";
				stmt.addBatch(sql);
			}


		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertPlayerRatingAOF(Hashtable<Integer, PlayerRating> playerRatingTable, int season) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		Set<Integer> keys = playerRatingTable.keySet();
		switch (season){
			case 14:
				for(int id : keys){
					float aof14 = playerRatingTable.get(id).getAof14();
					sql = "UPDATE PlayerRating SET AOF14 = " + aof14 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
			case 15:
				for(int id : keys){
					float aof15 = playerRatingTable.get(id).getAof15();
					sql = "UPDATE PlayerRating SET AOF15 = " + aof15 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
			case 16:
				for(int id : keys){
					float aof16 = playerRatingTable.get(id).getAof16();
					sql = "UPDATE PlayerRating SET AOF16 = " + aof16 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
			case 17:
				for(int id : keys){
					float aof17 = playerRatingTable.get(id).getAof17();
					sql = "UPDATE PlayerRating SET AOF17 = " + aof17 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertPlayerRatingVG(Hashtable<Integer, PlayerRating> playerRatingTable, int season) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		Set<Integer> keys = playerRatingTable.keySet();
		switch (season){
			case 14:
				for(int id : keys){
					float vg14 = playerRatingTable.get(id).getVg14();
					sql = "UPDATE PlayerRating SET VG14 = " + vg14 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
			case 15:
				for(int id : keys){
					float vg15 = playerRatingTable.get(id).getVg15();
					sql = "UPDATE PlayerRating SET VG15 = " + vg15 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
			case 16:
				for(int id : keys){
					float vg16 = playerRatingTable.get(id).getVg16();
					sql = "UPDATE PlayerRating SET VG16 = " + vg16 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
			case 17:
				for(int id : keys){
					float vg17 = playerRatingTable.get(id).getVg17();
					sql = "UPDATE PlayerRating SET VG17 = " + vg17 +" WHERE PlayerID = " + id + ";\n";
					stmt.addBatch(sql);
				}
				break;
		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertFreeKicks(ArrayList<FreeKick> fkList) throws ClassNotFoundException, SQLException{

		openConnection();
		Statement stmt = conn.createStatement();

		String sql="";
		for(FreeKick fk : fkList){
			sql="INSERT INTO FreeKick (OptaID, GameID, TeamID, PlayerID, Inswing, Xstart, Ystart, Xend, Yend, Goal, Shot) VALUES ("+fk.getOptaID() +"," + fk.getGameID() + "," + fk.getTeamID() + ","
					+ fk.getPlayerID() + "," + fk.getInswing() + "," + fk.getXstart() + "," + fk.getYstart() + ","
					+ fk.getXend() + "," + fk.getYstart() + "," + fk.getGoal() + "," + fk.getShot()	+");\n";
			stmt.addBatch(sql);

		}

		int [] updateCounts = stmt.executeBatch();
		closeConnection();

	}

	public static void insertStatesAndTrans(ArrayList<State> stateList, ArrayList<StateTransition> transitionArray) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		for (State s : stateList){
			sql = "INSERT INTO State2 VALUES (" + s.getStateID() + "," + s.getZone() + "," + "'"+s.getTeam()+"'" + "," + s.getPeriod() + "," + s.getMatchStatus() + ","
					+ s.getOccurrence() + "," + s.getValue() + ","+s.getReward() + ");\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
		System.out.println("States inserted");
		for (StateTransition st : transitionArray){
			sql = "INSERT INTO StateTransition2 (TransitionID, StartID, EndID, Action, Occurrence) VALUES (" + st.getStateTransitionID() + "," +st.getStartState().getStateID() +"," + st.getEndState().getStateID() + "," + "'" + st.getAction()
			+ "'" + "," + st.getOccurrence() + ");\n";
			stmt.addBatch(sql);
		}

		updateCounts = stmt.executeBatch();
		System.out.println("StateTransitions inserted");
		closeConnection();
	}

	public static void insertStateAction(Hashtable<Integer, Hashtable<String, Integer>> stateActions) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		Set<Integer> stateIDs = stateActions.keySet();
		for (int stateID : stateIDs){
			Set<String> actions = stateActions.get(stateID).keySet();
			for(String action : actions){
				int occurrence = stateActions.get(stateID).get(action);
				sql = "INSERT INTO StateAction2 (StateID, Action, Occurrence, Value) VALUES ( "+ stateID + ",'"+ action + "'," + occurrence + "," + 0 +");\n";
				stmt.addBatch(sql);
			}
		}
		int[] updateCounts = stmt.executeBatch();
		System.out.println("StateActions inserted");
		closeConnection();
	}

	public static void updateStateActionQ(Hashtable<String, StateAction> stateActions) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		Set<String> keys = stateActions.keySet();

		for(String key: keys ){
			int stateID = Integer.parseInt(key.replaceAll("[^\\d.]", ""));
			String action = key.replaceAll(Integer.toString(stateID), "");
			double qValue = stateActions.get(key).getValue();
			sql = "UPDATE StateAction2 SET Value = "+ qValue + " WHERE StateID = " + stateID + " AND Action = '" + action+ "';\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
		System.out.println("StateActions Qvalues updated ");
		closeConnection();
	}

	public static void updateStateActionQAlt(Hashtable<String, StateAction> stateActions) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		Set<String> keys = stateActions.keySet();

		for(String key: keys ){
			int stateID = Integer.parseInt(key.replaceAll("[^\\d.]", ""));
			String action = key.replaceAll(Integer.toString(stateID), "");
			double qValue = stateActions.get(key).getValue();
			sql = "UPDATE StateAction2 SET Value = "+ qValue + " WHERE StateID = " + stateID + " AND Action = '" + action+ "';\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
		System.out.println("StateActions Qvalues updated ");
		closeConnection();
	}
	public static void updateStateValues(ArrayList<State> stateList) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for(State s : stateList){
			sql = "UPDATE State SET Value=" + s.getValue()+" WHERE StateID = "+s.getStateID()+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void updateStateValuesMod2(Hashtable<Integer, State> states) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		Set<Integer> keys = states.keySet();
		for(Integer stateID : keys){
			State state = states.get(stateID);
			double stateValue = state.getValue();
			sql = "UPDATE State2 SET Value=" + stateValue +" WHERE StateID = "+stateID+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		System.out.println("StateValues updated on State");
		closeConnection();
	}

	public static void updateStateValuesMod2Alt(Hashtable<Integer, State> states) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		Set<Integer> keys = states.keySet();
		for(Integer stateID : keys){
			State state = states.get(stateID);
			double stateValue = state.getValue();
			sql = "UPDATE State2 SET Value=" + stateValue +" WHERE StateID = "+stateID+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		System.out.println("StateValues updated on State");
		closeConnection();
	}

	public static void insertStateActionNext(ArrayList<StateActionNext> stateActionList) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for (StateActionNext sa: stateActionList){
			sql = "INSERT INTO StateActionNext2 (StartID, StartAction, NextID, NextAction, Occurrence) VALUES ("+ sa.getStateID() + ",'"+sa.getAction()+"'," + sa.getNextStateID() +",'"+sa.getNextAction()
			+"',"+ sa.getOccurrence()+");\n";
			System.out.println(sql);
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static ResultSet getStateActionNext() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT SA.StartID, SA.StartAction, SA.NextID, SA.NextAction, SA.Occurrence FROM StateActionNext AS SA";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static ResultSet getStatesMod1() throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM State";
		ResultSet rs = stmt.executeQuery(query);
		return rs;

	}
	public static void insertPlayerValuesMod1(ArrayList<PlayerValues> playerValues) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0 ; i < playerValues.size() ; i++){
			PlayerValues pv = playerValues.get(i);
			pv.setAverageActionValues();
			for (int j = 0; j < 2 ; j++){
				int index = j+1;
				
				String sql = "INSERT INTO PlayerValues"+index+" VALUES ("+pv.getPlayerID()+"," + pv.getGameID() + "," + pv.getTeamID() + "," +pv.getTotal().get(j) +","+pv.getPass().get(j) +","+ pv.getLongPass().get(j) + "," + pv.getBallCarry().get(j) + "," + pv.getBallRecovery().get(j) + "," + pv.getBallReceived().get(j) +
						"," + pv.getAerialDuel().get(j) + "," + pv.getClearance().get(j) + "," + pv.getThrowInTaken().get(j) + "," + pv.getBallTouch().get(j) + "," + pv.getInterception().get(j) + "," + pv.getBlockedShot().get(j) + "," + pv.getSavedShot().get(j) + "," + pv.getCross().get(j)
						+ "," + pv.getTackle().get(j) + "," + pv.getShot().get(j) + "," + pv.getHeadedShot().get(j) + "," + pv.getTakeOn().get(j) + "," + pv.getFreekickPass().get(j) + "," + pv.getFoulCommitted().get(j) + "," + pv.getFouled().get(j)
						+"," +pv.getDispossessed().get(j) + "," + pv.getCornerTaken().get(j)+");\n";
				stmt.addBatch(sql);
				System.out.println(sql);
			}
		}
		int[] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertPlayerValuesMod2(ArrayList<PlayerValues> playerValues) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0 ; i < playerValues.size() ; i++){
			PlayerValues pv = playerValues.get(i);
			for (int j = 0; j < 2 ; j++){
				int index = j+1;
				
				String sql = "INSERT INTO PlayerValues"+index+" VALUES ("+pv.getPlayerID()+"," + pv.getGameID() + "," + pv.getTeamID() + "," +pv.getTotal().get(j) +","+pv.getPass().get(j) +","+ pv.getLongPass().get(j) + "," + pv.getBallCarry().get(j) + "," + pv.getBallRecovery().get(j) + "," + pv.getBallReceived().get(j) +
						"," + pv.getAerialDuel().get(j) + "," + pv.getClearance().get(j) + "," + pv.getThrowInTaken().get(j) + "," + pv.getBallTouch().get(j) + "," + pv.getInterception().get(j) + "," + pv.getBlockedShot().get(j) + "," + pv.getSavedShot().get(j) + "," + pv.getCross().get(j)
						+ "," + pv.getTackle().get(j) + "," + pv.getShot().get(j) + "," + pv.getHeadedShot().get(j) + "," + pv.getTakeOn().get(j) + "," + pv.getFreekickPass().get(j) + "," + pv.getFoulCommitted().get(j) + "," + pv.getFouled().get(j)
						+"," +pv.getDispossessed().get(j) + "," + pv.getCornerTaken().get(j)+");\n";
				stmt.addBatch(sql);
				System.out.println(sql);
			}
		}
		int[] updateCounts = stmt.executeBatch();
		closeConnection();
	}
	
	public static void insertStartingEleven(ArrayList<StartingEleven> startingElevens) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < startingElevens.size(); i++){
			String sql = "INSERT INTO StartingEleven VALUES ("+startingElevens.get(i).getGameID()+","+startingElevens.get(i).getTeamID() +"," + startingElevens.get(i).getPlayers().get(0)+","+
					startingElevens.get(i).getPlayers().get(1)+","+startingElevens.get(i).getPlayers().get(2)+","+startingElevens.get(i).getPlayers().get(3)+","+
					startingElevens.get(i).getPlayers().get(4)+","+startingElevens.get(i).getPlayers().get(5)+","+startingElevens.get(i).getPlayers().get(6)+","+
					startingElevens.get(i).getPlayers().get(7)+","+startingElevens.get(i).getPlayers().get(8)+","+startingElevens.get(i).getPlayers().get(9)+","+
					startingElevens.get(i).getPlayers().get(10)+");\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
	}
	
	public static void updateMatchResults(Hashtable<Integer, Integer> results) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		Set<Integer> gameIDs = results.keySet();
		for (int gameID: gameIDs){
			String sql = "UPDATE Game SET Score="+results.get(gameID) +" WHERE GameID="+gameID;
			System.out.println(sql);
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
	}

	public static ResultSet getGames() throws SQLException, ClassNotFoundException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT GameID, Score FROM Game";			ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

	public static ResultSet getPV1() throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT GameID, PlayerID, Total FROM PlayerValues1";
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public static ResultSet getPV2() throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT GameID, PlayerID, Total FROM PlayerValues2";
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	

	public static ResultSet getStartingElevens() throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT* FROM Game"
				+ " INNER JOIN StartingEleven as H on (H.GameID=Game.GameID AND Game.HomeID=H.TeamID) "
				+ "INNER JOIN StartingEleven as A on (A.GameID=Game.GameID AND Game.AwayID=A.TeamID)";
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

	public static ResultSet getGamesAndTeams() throws SQLException, ClassNotFoundException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT* FROM Game WHERE SeasonID=2016";
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

	public static void insertGameOutcomes(ArrayList<GameOutcome> gameList) throws SQLException, ClassNotFoundException {
		openConnection();
		Statement stmt = conn.createStatement();
		for (GameOutcome go: gameList){
			String sql = "UPDATE Game SET HomeProb="+go.getHomep() +",DrawProb="+go.getDrawp()+",AwayProb="+go.getAwayp()+" WHERE GameID="+go.getGameID()+";\n";
			System.out.println(sql);
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
	}

	public static ResultSet getPlayerPerTeam() throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT P.FirstName, P.LastName, P.PlayerID, PV.TeamID, T.Name, SUM(PV.Total), PGT.Total/90 AS Matches, SUM(PV.Total)/(PGT.Total/90) AS Total "                     
                                    + "FROM PlayerValues1 AS PV "
                                    + "INNER JOIN Player AS P ON PV.PlayerID=P.PlayerID "
                                    + "INNER JOIN PlayerGameTime AS PGT ON (PV.PlayerID=PGT.PlayerID AND PV.TeamID=PGT.TeamID) "
                                    + "INNER JOIN Game AS G ON PV.GameID=G.GameID "
                                    + "INNER JOIN Team AS T ON PV.TeamID = T.TeamID "
                                    + "WHERE PGT.Total>250 AND P.Position!='Goalkeeper' AND P.PlayerID IN ("
                                    	+ "SELECT PGT.PlayerID "
                                    	+ "FROM PlayerGameTime AS PGT "
                                    	+ "WHERE PGT.Total>250 "
                                    	+ "GROUP BY  PGT.PlayerID "
                                    	+ "HAVING Count(*)>1) "
                                    + "GROUP BY PV.PlayerID, PV.TeamID "
                                    + "ORDER BY PV.PlayerID;";
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

	public static ResultSet getGameImpacts(int p, int t) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT PV.PlayerID, PV.GameID, PV.TeamID, "
				+ "I1.PlayerID AS P1, I1.Total AS I1, " 
				+ "I2.PlayerID AS P2, I2.Total AS I2, "
				+ "I3.PlayerID AS P3, I3.Total AS I3, "
				+ "I4.PlayerID AS P4, I4.Total AS I4, "
				+ "I5.PlayerID AS P5, I5.Total AS I5, "
				+ "I6.PlayerID AS P6, I6.Total AS I6, "
				+ "I7.PlayerID AS P7, I7.Total AS I7, "
				+ "I8.PlayerID AS P8, I8.Total AS I8, "
				+ "I9.PlayerID AS P9, I9.Total AS I9, "
				+ "I10.PlayerID AS P10, I10.Total AS I10 "
				+ "FROM `PlayerValues1` AS PV "
				+ "INNER JOIN StartingEleven AS SE ON (PV.TeamID=SE.TeamID AND PV.GameID=SE.GameID) "
				+ "INNER JOIN PlayerValues1 AS I1 ON (SE.P1=I1.PlayerID AND SE.GameID=I1.GameID) "
				+ "INNER JOIN PlayerValues1 AS I2 ON (SE.P2=I2.PlayerID AND SE.GameID=I2.GameID) "
				+ "INNER JOIN PlayerValues1 AS I3 ON (SE.P3=I3.PlayerID AND SE.GameID=I3.GameID) "
				+ "INNER JOIN PlayerValues1 AS I4 ON (SE.P4=I4.PlayerID AND SE.GameID=I4.GameID) "
				+ "INNER JOIN PlayerValues1 AS I5 ON (SE.P5=I5.PlayerID AND SE.GameID=I5.GameID) "
				+ "INNER JOIN PlayerValues1 AS I6 ON (SE.P6=I6.PlayerID AND SE.GameID=I6.GameID) "
				+ "INNER JOIN PlayerValues1 AS I7 ON (SE.P7=I7.PlayerID AND SE.GameID=I7.GameID) "
				+ "INNER JOIN PlayerValues1 AS I8 ON (SE.P8=I8.PlayerID AND SE.GameID=I8.GameID) "
				+ "INNER JOIN PlayerValues1 AS I9 ON (SE.P9=I9.PlayerID AND SE.GameID=I9.GameID) "
				+ "INNER JOIN PlayerValues1 AS I10 ON (SE.P10=I10.PlayerID AND SE.GameID=I10.GameID)"
				+ "WHERE PV.PlayerID = " +p + " AND PV.TeamID=" +t+";";
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	

}
