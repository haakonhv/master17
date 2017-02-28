package markov2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import master17.DatabaseHandler;
import master17.StateBuilder;

public class Builder {

	public void buildFromEvents() throws ClassNotFoundException, SQLException{
		ResultSet eventSet = DatabaseHandler.getDatabaseEventsModel2();
		ArrayList<State> stateList = new ArrayList<State>();
		int stateID = 1;

		while(eventSet.next()){
			String action = eventSet.getString("Action");
			int outcome = eventSet.getInt("Outcome");
			int minute = eventSet.getInt("Minute");
			int period = eventSet.getInt("Period");
			int goalDifference = eventSet.getInt("GoalDifference");
			int teamID = eventSet.getInt("TeamID");
			int homeID = eventSet.getInt("HomeID");
			int awayID = eventSet.getInt("AwayID");
			float xStart = eventSet.getFloat("Xstart");
			float yStart = eventSet.getFloat("Ystart");
			float xEnd = eventSet.getFloat("Xend");
			float yEnd = eventSet.getFloat("Yend");
			int startZone = StateBuilder.getZoneFromCoordinates(xStart, yStart);
			int statePeriod = StateBuilder.getPeriod(minute, period);
			int matchStatus = StateBuilder.getMatchStatus(goalDifference, homeID, teamID);
			int reward = StateBuilder.getReward(action, teamID==homeID);
			String team = "";

			if (teamID == homeID){
				team = "Home";
			}
			else {
				team = "Away";
			}

			eventSet.next();
			String nextAction = eventSet.getString("Action");
			eventSet.previous();

			if (!nextAction.equals("Aerial duel")){
				if (action.equals("Pass") || action.equals("Long pass") || action.equals("Cross") || action.equals("Free kick pass") || action.equals("Throw in taken") || action.equals("Corner taken")){
					if (outcome == 1){
						if (stateList.size() == 0){
							State startState = new State(stateID, startZone, team, statePeriod, matchStatus, reward);;
						}

					}
				}
			}


		}


	}


}
