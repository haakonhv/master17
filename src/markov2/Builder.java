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
			int endZone = StateBuilder.getZoneFromCoordinates(xEnd, yEnd);
			int statePeriod = StateBuilder.getPeriod(minute, period);
			int matchStatus = StateBuilder.getMatchStatus(goalDifference, homeID, teamID);
			int reward = StateBuilder.getReward(action, teamID==homeID);
			String team = "";
			State startState;
			State endState;

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
					if (outcome == 1){ // hvis outcome er 1 -> start og end state har samme lag
						//lager start state
						if (stateList.size() == 0){ //hvis statelist tom -> legg til ny state
							startState = new State(stateID, startZone, team, statePeriod, matchStatus, reward);
							stateList.add(startState);
							stateID++;
						}
						else { //hvis statelist ikke tom
							boolean startStateExists = false;
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == startZone && s.getPeriod() == statePeriod //hvis state finnes fra før
										&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
										&& s.getReward() == reward){
									s.incrementOccurrence();
									startState = s;
									startStateExists = true;
								}
								if (!startStateExists){
									startState = new State(stateID, startZone, team, statePeriod, matchStatus, reward);
									stateList.add(startState);
									stateID++;
								}
							}
						}
						//lager end state
						boolean endStateExists = false;
						for (int i = 0; i < stateList.size(); i++){
							State s = stateList.get(i);
							if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra før
									&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
									&& s.getReward() == reward){
								s.incrementOccurrence();
								endState = s;
								endStateExists = true;
							}
						}
						if (!endStateExists){
							endState = new State(stateID, endZone, team, statePeriod, matchStatus, reward);
							stateList.add(endState);
							stateID++;
						}
					}
					else {//outcome ==0 -> start state hos team som slo pasning, end state har ikke noe team
						
					}
				}
			}


		}


	}


}
