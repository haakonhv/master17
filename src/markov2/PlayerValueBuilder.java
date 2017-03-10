package markov2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import master17.DatabaseHandler;
import master17.PlayerValues;

public class PlayerValueBuilder {

	public static void buildPlayerValues() throws ClassNotFoundException, SQLException{

		ResultSet events = DatabaseHandler.getEventsAndValuesMod2();

		Hashtable<Integer,Hashtable<Integer, PlayerValues>> playerValues = new Hashtable<Integer, Hashtable<Integer, PlayerValues>>();
		Hashtable<Integer, PlayerValues> gameValues = new Hashtable<Integer, PlayerValues>();
		ArrayList<PlayerValues> playerValueList= new ArrayList<PlayerValues>();
		int prevGameID = 0;
		int c = 0;

		while(events.next()){
			int teamID = events.getInt("TeamID");
			int gameID = events.getInt("GameID");
			int playerID = events.getInt("PlayerID");
			int homeID =  events.getInt("HomeID");
			int awayID = events.getInt("AwayID");
			double qValue = events.getDouble("QValue");
			double endStateVal =  events.getDouble("EndValue");
			String action = events.getString("E.Action");

			if(gameID != prevGameID){
				System.out.println("Begynner med gameID: " + gameID);
				c++;
				gameValues = new Hashtable<Integer, PlayerValues>();
				playerValues.put(gameID, gameValues);
			}

			double eventValue;
			if (teamID == homeID){ //hjemmelags event
				eventValue = endStateVal - qValue;

			}
			else { //bortelags event
				eventValue = -(endStateVal - qValue);
			}

			if(gameValues.containsKey(playerID)){
				PlayerValues pv = gameValues.get(playerID);
				pv.updateValue(action, eventValue);
				gameValues.put(playerID, pv);
			}
			else {
				PlayerValues pv = new PlayerValues(playerID, gameID,teamID);
				pv.updateValue(action, eventValue);
				gameValues.put(playerID, pv);
			}

			prevGameID = gameID;



		}
		Set<Integer> gameIDs = playerValues.keySet();
		for (Integer gID: gameIDs){
			Hashtable<Integer, PlayerValues> playervals = playerValues.get(gID);
			Set<Integer> playerIDs = playervals.keySet();
			for (Integer pID: playerIDs){
				playerValueList.add(playervals.get(pID));
			}
		}

		System.out.println(c);
		DatabaseHandler.insertPlayerValues(playerValueList);



	}

}
