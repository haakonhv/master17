package master17;

import java.sql.ResultSet;
import java.util.ArrayList;

public class FindPlayerValues {
	public static void findValues() {
		ResultSet players = DatabaseHandler.getPlayers();
		ResultSet events = DatabaseHandler.getEventsAndValues();
		ArrayList<PlayerValues> playerValueList= new ArrayList<PlayerValues>();
		while (players.next()){ //bygger playerValues objekter for alle players i databasen
			playerValueList.add(new PlayerValues(players.getInt("PlayerID")));
		}
		
		events.next();
		int prevTeamID = events.getInt("TeamID"); //lagrer previous events variabler. Nødvendig for å sjekke når game er ferdig, sjekke ballvinning osv
		int prevPlayerID =  events.getInt("PlayerID");
		int prevGameID = events.getInt("GameID");
		int prevStateID = events.getInt("StateID");
		String prevAction = events.getString("Action");
		double prevQvalue = events.getInt("QValue");
		int prevHomeID = events.getInt("HomeID");
		int prevAwayID = events.getInt("AwayID");
		int prevZone = events.getInt("Zone");
		
		events.next();
		int currTeamID = events.getInt("TeamID"); //lagrer current events variabler
		int currPlayerID =  events.getInt("PlayerID");
		int currGameID = events.getInt("GameID");
		int currStateID = events.getInt("StateID");
		String currAction = events.getString("Action");
		double currQvalue = events.getInt("QValue");
		int currHomeID = events.getInt("HomeID");
		int currAwayID = events.getInt("AwayID");
		int currZone = events.getInt("Zone");
		
		
		while (events.next()){ //traverserer alle events
			int nextTeamID = events.getInt("TeamID"); //lagrer current events variabler
			int nextPlayerID =  events.getInt("PlayerID");
			int nextGameID = events.getInt("GameID");
			int nextStateID = events.getInt("StateID");
			String nextAction = events.getString("Action");
			double nextQvalue = events.getInt("QValue");
			int nextHomeID = events.getInt("HomeID");
			int nextAwayID = events.getInt("AwayID");
			int nextZone = events.getInt("Zone");
			
			if (prevGameID!=currGameID){ //sjekker om forrige event er fra en annen game enn current
				//TODO: Legg til logikk
				
				
			}
			else { //prev og current event er fra samme game
				if (currAction.equals("Out of play") || currAction.equals("Goal") || currAction.equals("End of period")){ //altså ingen verdier tildeles spiller fra current
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
					if (prevZone == 0) { //forrige event endte en sekvens -> må se på neste event
						eventValue = nextQvalue - currQvalue;
					}
					else if (prevTeamID == currTeamID){ //current event er samme lag som previous
						eventValue = nextQvalue - currQvalue;
					}
					else { // current event er ikke av samme lag som previous (altså ballvinning e.l.)
						eventValue = nextQvalue - prevQvalue;
					}
				}
				else { //bortelags event
					if (prevZone == 0){ //forrige event endte en sekvens -> må se på neste event
						eventValue = - (nextQvalue - currQvalue);
					}
					else if (prevTeamID == currTeamID){//current event er samme lag som previous
						eventValue = - (nextQvalue - currQvalue);
					}
					else { //current event er fra annet lag enn previous (altså ballvinning e.l.)
						eventValue = - (nextQvalue - prevQvalue);
					}
				}
				for (int j = 1; j < playerValueList.size(); j++){
					if (playerValueList.get(j).getPlayerID() == currPlayerID){
						playerValueList.get(j).updateValue(currAction, eventValue);
					}
				}
			}	
		}
		DatabaseHandler.insertPlayerValues(playerValueList);
		
	}
}
