package markov2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import master17.DatabaseHandler;

import master17.StateBuilder;

public class Builder {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		buildFromEvents();
	}

	public static void buildFromEvents() throws ClassNotFoundException, SQLException{
		ResultSet eventSet = DatabaseHandler.getDatabaseEventsModel2();
		ArrayList<State> stateList = new ArrayList<State>();
		ArrayList<StateTransition> transList = new ArrayList<StateTransition>();
		Hashtable<Integer, Hashtable<Integer, Hashtable<String, StateTransition>>> transitions = new Hashtable<Integer, Hashtable<Integer, Hashtable<String, StateTransition>>>();
		State startState = null;
		State endState = null;
		boolean prevEndOfPeriod = false;
		String prevAction = "";
		int stateID = 1;
		System.out.println(eventSet.getFetchSize());
		
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
			int eventNumber = eventSet.getInt("Number");
			System.out.println(action);
			int startZone = StateBuilder.getZoneFromCoordinates(xStart, yStart);
			int endZone = StateBuilder.getZoneFromCoordinates(xEnd, yEnd);
			int statePeriod = StateBuilder.getPeriod(minute, period);
			int matchStatus = StateBuilder.getMatchStatus(goalDifference, homeID, teamID);
			int reward = StateBuilder.getReward(action, teamID==homeID);
			String team = "";
			String otherTeam="";



			if (teamID == homeID){
				team = "Home";
				otherTeam = "Away";
			}
			else {
				team = "Away";
				otherTeam = "Home";
			}
			if (action.equals("Goal")){
				prevAction = action;
				continue;
			}
			else if (action.equals("End of period")){
				prevEndOfPeriod = true;
				continue;
			}
			prevEndOfPeriod = false;



			if (eventNumber == 1 || prevEndOfPeriod || prevAction.equals("Goal")) { //lager kun startState for event når det er første event i en omgang!
				if (stateList.size() == 0){ //hvis statelist tom -> legg til ny state
					startState = new State(stateID, startZone, team, statePeriod, matchStatus, 0);
					stateList.add(startState);
					stateID++;
				}
				else { //hvis statelist ikke tom
					boolean startStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == startZone && s.getPeriod() == statePeriod //hvis state finnes fra før
								&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
								&& s.getReward() == 0){
							s.incrementOccurrence();
							startState = s;
							startStateExists = true;
							break;
						}
						if (!startStateExists){
							startState = new State(stateID, startZone, team, statePeriod, matchStatus, 0);
							stateList.add(startState);
							stateID++;
						}
					}
				}
			}
			eventSet.next();
			String nextAction = eventSet.getString("Action");
			int nextZone = StateBuilder.getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
			int nextTeamID = eventSet.getInt("TeamID");
			String nextTeam = "";
			if (homeID==eventSet.getInt("TeamID")){
				nextTeam = "Home";
			}
			else {
				nextTeam = "Away";
			}
			int nextReward = StateBuilder.getReward(nextAction, nextTeamID==homeID);
			eventSet.previous();

			if (action.equals("Pass") || action.equals("Long pass") || action.equals("Cross") || action.equals("Free kick pass") || action.equals("Throw in taken") || action.equals("Corner taken")){
				if (!nextAction.equals("Aerial duel")){
					if (outcome == 1){ // hvis outcome er 1 -> start og end state har samme lag
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
								break;
							}
						}
						if (!endStateExists){
							endState = new State(stateID, endZone, team, statePeriod, matchStatus, reward);
							stateList.add(endState);
							stateID++;
						}
					}
					else {//outcome ==0 -> start state hos team som slo pasning, end state har ikke noe team
						boolean endStateExists = false;
						for (int i = 0; i < stateList.size(); i++){
							State s = stateList.get(i);
							if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra før
									&& s.getTeam().equals("None") && s.getMatchStatus() == StateBuilder.getMatchStatus(goalDifference, homeID, homeID)
									&& s.getReward() == reward){
								s.incrementOccurrence();
								endState = s;
								endStateExists = true;
								break;
							}
						}
						if (!endStateExists){
							endState = new State(stateID, endZone, "None", period, StateBuilder.getMatchStatus(goalDifference, homeID, homeID), reward);
							stateList.add(endState);
							stateID++;
						}
					}
				}
				else { //nextAction er hodeduell
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra før
								&& s.getTeam().equals("None") && s.getMatchStatus() == StateBuilder.getMatchStatus(goalDifference, homeID, homeID)
								&& s.getReward() == reward){
							s.incrementOccurrence();
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, endZone, "None", period, StateBuilder.getMatchStatus(goalDifference, homeID, homeID), reward);
						stateList.add(endState);
						stateID++;
					}
				}
			}
			else if (action.equals("Ball carry") || action.equals("Aerial duel") || action.equals("Ball recovery")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra før
							&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
							&& s.getReward() == reward){
						s.incrementOccurrence();
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, endZone, team, statePeriod, matchStatus, reward);
					stateList.add(endState);
					stateID++;
				}
			}
			else if (action.equals("Foul committed")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra før
							&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == matchStatus
							&& s.getReward() == reward){
						s.incrementOccurrence();
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, endZone, otherTeam, statePeriod, matchStatus, reward);
					stateList.add(endState);
					stateID++;
				}
			}
			
			else if(nextAction.equals("Goal")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra før
							&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, 0, nextTeam, 0, 0, nextReward);
					stateList.add(endState);
					stateID++;
				}
			}
			else if (nextAction.equals("End of period")){ //neste event er ikke mål
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra før
							&& s.getTeam().equals("None") && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, 0, "None", 0, 0, nextReward);
					stateList.add(endState);
					stateID++;
				}
			}
			else {
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra før
							&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == matchStatus
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, nextZone, nextTeam, period, matchStatus, nextReward);
					stateList.add(endState);
					stateID++;
				}
			}

			//lager eller oppdaterer transitions
			StateTransition thisTransition = new StateTransition(startState, endState, action); 
			boolean transitionExists = false;
			if (transitions.containsKey(startState.getStateID())){
				Hashtable<Integer, Hashtable<String, StateTransition>> endStates = transitions.get(startState.getStateID());
				if (endStates.containsKey(endState.getStateID())){
					Hashtable<String, StateTransition> actiontrans = endStates.get(endState.getStateID());
					if (actiontrans.containsKey(action)){
						thisTransition = actiontrans.get(action);
						thisTransition.incrementOccurence();
						actiontrans.put(action, thisTransition);
						endStates.put(endState.getStateID(), actiontrans);
						transitions.put(startState.getStateID(), endStates);
						transitionExists = true;
					}
				}
			}
			if (!transitionExists){
				Hashtable<String, StateTransition> actiontrans = new Hashtable<String, StateTransition>();
				actiontrans.put(action, thisTransition);
				Hashtable<Integer,Hashtable<String,StateTransition>> endStates = new Hashtable<Integer, Hashtable<String, StateTransition>>();
				endStates.put(endState.getStateID(), actiontrans);
				transitions.put(startState.getStateID(), endStates);
			}
			startState = endState;
			prevAction = action;
		}
		Set<Integer> startIDS = transitions.keySet();
		Set<Integer> endIDS;
		Set<String> actions;
		ArrayList<StateTransition> transitionArray = new ArrayList<StateTransition>();
		for (Integer start: startIDS){
			Hashtable<Integer, Hashtable<String, StateTransition>> endIDhash = new Hashtable<Integer, Hashtable<String, StateTransition>>();
			endIDhash = transitions.get(start);
			endIDS = endIDhash.keySet();
			for (Integer endID: endIDS){
				Hashtable<String, StateTransition> actiontransHash = new Hashtable<String, StateTransition>();
				actiontransHash = endIDhash.get(endID);
				actions = actiontransHash.keySet();
				for (String action: actions){
					transitionArray.add(actiontransHash.get(action));
				}
			}
		}
		DatabaseHandler.insertStatesAndTrans(stateList, transitionArray);
	}
	

}
