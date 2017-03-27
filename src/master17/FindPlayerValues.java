package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class FindPlayerValues {
	public static void findValues() throws ClassNotFoundException, SQLException {
		ResultSet players = DatabaseHandler.getPlayers();
		ResultSet events = DatabaseHandler.getEventsAndValues();
		ArrayList<PlayerValues> playerValueList= new ArrayList<PlayerValues>();
		ResultSet states = DatabaseHandler.getStatesMod1();
		
		Hashtable<Integer,Hashtable<Integer, PlayerValues>> playerValues = new Hashtable<Integer, Hashtable<Integer, PlayerValues>>();
		//gameID->PlayerID->PlayerValues
		Hashtable<String, Double> stateNoAction = getStateNoAction(states);
		
		
		events.next();
		int prevTeamID = events.getInt("TeamID"); //lagrer previous events variabler. N�dvendig for � sjekke n�r game er ferdig, sjekke ballvinning osv
		int prevPlayerID =  events.getInt("PlayerID");
		int prevGameID = events.getInt("GameID");
		int prevStateID = events.getInt("StateID");
		String prevAction = events.getString("Action");
		double prevQvalue = events.getDouble("QValue");
		int prevHomeID = events.getInt("HomeID");
		int prevAwayID = events.getInt("AwayID");
		int prevZone = events.getInt("Zone");
		int prevPeriod = events.getInt("Period");
		int prevMatchStatus = events.getInt("MatchStatus");
		int prevHome = events.getInt("Home");
		

		events.next();
		int currTeamID = events.getInt("TeamID"); //lagrer current events variabler
		int currPlayerID =  events.getInt("PlayerID");
		int currGameID = events.getInt("GameID");
		int currStateID = events.getInt("StateID");
		String currAction = events.getString("Action");
		double currQvalue = events.getDouble("QValue");
		int currHomeID = events.getInt("HomeID");
		int currAwayID = events.getInt("AwayID");
		int currZone = events.getInt("Zone");
		int currPeriod = events.getInt("Period");
		int currMatchStatus = events.getInt("MatchStatus");
		int currHome = events.getInt("Home");
		
		
		Hashtable<Integer, PlayerValues> gameValues = new Hashtable<Integer, PlayerValues>();
		playerValues.put(currGameID, gameValues);


		while (events.next()){ //traverserer alle events
			int nextTeamID = events.getInt("TeamID"); //lagrer current events variabler
			int nextPlayerID =  events.getInt("PlayerID");
			int nextGameID = events.getInt("GameID");
			int nextStateID = events.getInt("StateID");
			String nextAction = events.getString("Action");
			double nextQvalue = events.getDouble("QValue");
			int nextHomeID = events.getInt("HomeID");
			int nextAwayID = events.getInt("AwayID");
			int nextZone = events.getInt("Zone");
			int nextPeriod = events.getInt("Period");
			int nextMatchStatus = events.getInt("MatchStatus");
			int nextHome = events.getInt("Home");
			
			//System.out.println("Current Q= " + currQvalue + " previous Q= "+prevQvalue+" nextQ = "+nextQvalue);
			if (prevGameID!=currGameID){ //sjekker om forrige event er fra en annen game enn current
				gameValues = new Hashtable<Integer, PlayerValues>();
				playerValues.put(currGameID, gameValues);
				System.out.println("Ferdig med game " +currGameID);
				prevTeamID = currTeamID;
				prevPlayerID = currPlayerID;
				prevGameID = currGameID;
				prevStateID = currStateID;
				prevAction = currAction;
				prevQvalue = currQvalue;
				prevHomeID = currHomeID;
				prevAwayID = currAwayID;
				prevZone = currZone;
				prevPeriod = currPeriod;
				prevMatchStatus = currMatchStatus;
				prevHome = currHome;

				currTeamID = nextTeamID;
				currPlayerID = nextPlayerID;
				currGameID = nextGameID;
				currStateID = nextStateID;
				currAction = nextAction;
				currQvalue = nextQvalue;
				currHomeID = nextHomeID;
				currAwayID = nextAwayID;
				currZone = nextZone;
				currPeriod = nextPeriod;
				currMatchStatus = nextMatchStatus;
				currHome = nextHome;
				continue;

			}
			else { //prev og current event er fra samme game
				if (currAction.equals("Out of play") || currAction.equals("Goal")|| currAction.equals("End of period") || currAction.equals("Goalkeeper")){ //alts� ingen verdier tildeles spiller fra current
					prevTeamID = currTeamID;
					prevPlayerID = currPlayerID;
					prevGameID = currGameID;
					prevStateID = currStateID;
					prevAction = currAction;
					prevQvalue = currQvalue;
					prevHomeID = currHomeID;
					prevAwayID = currAwayID;
					prevZone = currZone;
					prevPeriod = currPeriod;
					prevMatchStatus = currMatchStatus;
					prevHome = currHome;

					currTeamID = nextTeamID;
					currPlayerID = nextPlayerID;
					currGameID = nextGameID;
					currStateID = nextStateID;
					currAction = nextAction;
					currQvalue = nextQvalue;
					currHomeID = nextHomeID;
					currAwayID = nextAwayID;
					currZone = nextZone;
					currPeriod = nextPeriod;
					currMatchStatus = nextMatchStatus;
					currHome = nextHome;
					continue;
				}
				ArrayList<Double> eventValues = new ArrayList<Double>();
				String stateKey = "" + currZone + currHome + currPeriod + currMatchStatus;
				if (currTeamID == currHomeID){ //hjemmelags event
					eventValues.add(currQvalue);
					eventValues.add(nextQvalue - currQvalue);
					eventValues.add(nextQvalue - prevQvalue);
					eventValues.add(currQvalue - stateNoAction.get(stateKey));
				}
				else { //bortelags event
					eventValues.add(-currQvalue);
					eventValues.add(- (nextQvalue - currQvalue));
					eventValues.add(-(nextQvalue - prevQvalue));
					eventValues.add(-(currQvalue - stateNoAction.get(stateKey)));
				}
				
				if(gameValues.containsKey(currPlayerID)){
					PlayerValues pv = gameValues.get(currPlayerID);
					pv.updateValue(currAction, eventValues);
					gameValues.put(currPlayerID, pv);
				}
				else {
					PlayerValues pv = new PlayerValues(currPlayerID, currGameID, currTeamID);
					pv.updateValue(currAction, eventValues);
					gameValues.put(currPlayerID, pv);
				}
				
				prevTeamID = currTeamID;
				prevPlayerID = currPlayerID;
				prevGameID = currGameID;
				prevStateID = currStateID;
				prevAction = currAction;
				prevQvalue = currQvalue;
				prevHomeID = currHomeID;
				prevAwayID = currAwayID;
				prevZone = currZone;
				prevPeriod = currPeriod;
				prevMatchStatus = currMatchStatus;
				prevHome = currHome;

				currTeamID = nextTeamID;
				currPlayerID = nextPlayerID;
				currGameID = nextGameID;
				currStateID = nextStateID;
				currAction = nextAction;
				currQvalue = nextQvalue;
				currHomeID = nextHomeID;
				currAwayID = nextAwayID;
				currZone = nextZone;
				currPeriod = nextPeriod;
				currMatchStatus = nextMatchStatus;
				currHome = nextHome;
			}
		}

		
		Set<Integer> gameIDs = playerValues.keySet();
		for (Integer gameID: gameIDs){
			Hashtable<Integer, PlayerValues> playervals = playerValues.get(gameID);
			Set<Integer> playerIDs = playervals.keySet();
			for (Integer playerID: playerIDs){
				playerValueList.add(playervals.get(playerID));
			}
		}
		DatabaseHandler.insertPlayerValuesMod1(playerValueList);


	}

	private static Hashtable<String, Double> getStateNoAction(ResultSet states) throws SQLException {
		Hashtable<String, Double> stateNoAction = new Hashtable<String, Double>();
		Hashtable<String, Integer> stateCount = new Hashtable<String, Integer>();
		while(states.next()){
			String key;
			int zone = states.getInt("Zone"); 
			int home = states.getInt("Home");
			int period = states.getInt("Period"); 
			int matchStatus = states.getInt("matchStatus");
			key = "" + zone+home+period+matchStatus;
			if (stateNoAction.containsKey(key)){
				stateCount.put(key, stateCount.get(key) + 1);
				stateNoAction.put(key, stateNoAction.get(key) + states.getDouble("QValue"));
			}
			else{
				stateCount.put(key, 1);
				stateNoAction.put(key, states.getDouble("QValue"));
			}
		}
		Set<String> keyset = stateNoAction.keySet();
		for (String key : keyset){
			stateNoAction.put(key, stateNoAction.get(key)/stateCount.get(key));
		}
		return stateNoAction;
	}
}
