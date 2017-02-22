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
		
		Hashtable<Integer,Hashtable<Integer, PlayerValues>> playerValues = new Hashtable<Integer, Hashtable<Integer, PlayerValues>>();
//		while (players.next()){ //bygger playerValues objekter for alle players i databasen
//			playerValueList.add(new PlayerValues(players.getInt("PlayerID")));
//		}
//		System.out.println("Laget playervalue list");

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
			//System.out.println(currGameID);
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

				currTeamID = nextTeamID;
				currPlayerID = nextPlayerID;
				currGameID = nextGameID;
				currStateID = nextStateID;
				currAction = nextAction;
				currQvalue = nextQvalue;
				currHomeID = nextHomeID;
				currAwayID = nextAwayID;
				currZone = nextZone;
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

					currTeamID = nextTeamID;
					currPlayerID = nextPlayerID;
					currGameID = nextGameID;
					currStateID = nextStateID;
					currAction = nextAction;
					currQvalue = nextQvalue;
					currHomeID = nextHomeID;
					currAwayID = nextAwayID;
					currZone = nextZone;
					continue;
				}
				double eventValue;
				if (currTeamID == currHomeID){ //hjemmelags event
					if (prevZone == 0) { //forrige event endte en sekvens -> m� se p� neste event
						eventValue = nextQvalue - currQvalue;
					}
					else if (prevTeamID == currTeamID){ //current event er samme lag som previous
						eventValue = nextQvalue - currQvalue;
					}
					else { // current event er ikke av samme lag som previous (alts� ballvinning e.l.)
						eventValue = nextQvalue - currQvalue;
						//eventValue = nextQvalue - prevQvalue;
					}
				}
				else { //bortelags event
					if (prevZone == 0){ //forrige event endte en sekvens -> m� se p� neste event
						eventValue = - (nextQvalue - currQvalue);
					}
					else if (prevTeamID == currTeamID){//current event er samme lag som previous
						eventValue = - (nextQvalue - currQvalue);
					}
					else { //current event er fra annet lag enn previous (alts� ballvinning e.l.)
						eventValue = - (nextQvalue - currQvalue);
						//eventValue = - (nextQvalue - prevQvalue);
					}
				}
				
				if(gameValues.containsKey(currPlayerID)){
					PlayerValues pv = gameValues.get(currPlayerID);
					pv.updateValue(currAction, eventValue);
					gameValues.put(currPlayerID, pv);
				}
				else {
					PlayerValues pv = new PlayerValues(currPlayerID, currGameID, currTeamID);
					pv.updateValue(currAction, eventValue);
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

				currTeamID = nextTeamID;
				currPlayerID = nextPlayerID;
				currGameID = nextGameID;
				currStateID = nextStateID;
				currAction = nextAction;
				currQvalue = nextQvalue;
				currHomeID = nextHomeID;
				currAwayID = nextAwayID;
				currZone = nextZone;
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
		DatabaseHandler.insertPlayerValues(playerValueList);

	}
}
