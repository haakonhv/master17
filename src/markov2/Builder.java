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
		ArrayList<String> sqlList = new ArrayList<String>();
		State startState = null;
		State endState = null;
		boolean prevEndOfPeriod = false;
		String prevAction = "";
		int stateID = 1;
		int stateTransitionID = 1;

		int stateincCount = 0;

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
			int eventID = eventSet.getInt("EventID");

			int startZone = StateBuilder.getZoneFromCoordinates(xStart, yStart);
			int endZone = StateBuilder.getZoneFromCoordinates(xEnd, yEnd);
			int statePeriod = StateBuilder.getPeriod(minute, period);

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
			int matchStatus = getMatchStatus(goalDifference);
			if (action.equals("Goal") || action.equals("Out of play")){
				//				System.out.println(action);
				prevAction = action;
				continue;
			}
			else if (action.equals("End of period")){
				prevEndOfPeriod = true;
				continue;
			}



			if (eventNumber == 1 || prevEndOfPeriod || prevAction.equals("Goal")) { //lager kun startState for event n�r det er f�rste event i en omgang!
				if (prevEndOfPeriod) prevEndOfPeriod = false;
				if (stateList.size() == 0){ //hvis statelist tom -> legg til ny state
					startState = new State(stateID, startZone, team, statePeriod, matchStatus, 0);
					stateList.add(startState);
					stateID++;
				}
				else { //hvis statelist ikke tom
					boolean startStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == startZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
								&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
								&& s.getReward() == 0){
							s.incrementOccurrence();
							startState = s;
							startStateExists = true;
							stateincCount++;
							break;
						}
					}
					if (!startStateExists){
						startState = new State(stateID, startZone, team, statePeriod, matchStatus, 0);
						stateList.add(startState);
						stateID++;
					}
				}
			}
			eventSet.next();
			String nextAction = eventSet.getString("Action");
			int nextZone = StateBuilder.getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
			int nextTeamID = eventSet.getInt("TeamID");
			String nextTeam = "";
			String nextOtherTeam = "";

			if (homeID==nextTeamID){
				nextTeam = "Home";
				nextOtherTeam ="Away";
			}
			else {
				nextTeam = "Away";
				nextOtherTeam = "Home";
			}
			int nextMatchStatus = getMatchStatus(goalDifference);
			int nextReward = StateBuilder.getReward(nextAction, nextTeamID==homeID);
			eventSet.previous();

			if (action.equals("Pass") || action.equals("Long pass") || action.equals("Cross") || action.equals("Free kick pass") || action.equals("Throw in taken") || action.equals("Corner taken")){
				if (!nextAction.equals("Aerial duel")){
					if (outcome == 1){ // hvis outcome er 1 -> start og end state har samme lag
						//lager end state
						boolean endStateExists = false;
						for (int i = 0; i < stateList.size(); i++){
							State s = stateList.get(i);
							if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
									&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
									&& s.getReward() == reward){
								s.incrementOccurrence();
								stateincCount++;
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
						if (nextAction.equals("Out of play")){
							if (eventID== 6472650) System.out.println(eventID + " " + nextAction);
							eventSet.next();
							eventSet.next();
							nextZone = StateBuilder.getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
							eventSet.previous();
							eventSet.previous();
							boolean endStateExists = false;
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == matchStatus
										&& s.getReward() == reward){
									s.incrementOccurrence();
									stateincCount++;
									endState = s;
									endStateExists = true;
									break;
								}
							}
							if (!endStateExists){
								endState = new State(stateID, nextZone, otherTeam, period, matchStatus, reward);
								stateList.add(endState);
								stateID++;
							}
						}
						else if (nextAction.equals("Shot") || nextAction.equals("Headed shot") || nextAction.equals("Pass") || nextAction.equals("Ball carry") || nextAction.equals("Take on")
								|| nextAction.equals("Long pass") || nextAction.equals("Cross")){
							//if (eventID== 6472650) System.out.println(eventID + " " + "123" +nextAction) ;
							boolean endStateExists = false;
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == nextMatchStatus
										&& s.getReward() == reward){
									s.incrementOccurrence();
									stateincCount++;
									endState = s;
									endStateExists = true;
									break;
								}
							}
							if (!endStateExists){
								endState = new State(stateID, nextZone, nextTeam, period, nextMatchStatus, reward);
								stateList.add(endState);
								stateID++;
							}
						}
						else {

//							if (eventID == 6472637 || eventID == 6472650) System.out.println(eventID + " " + stateList.size());
							boolean endStateExists = false;
							for (int i = 0; i < stateList.size(); i++){
								if (eventID==6472650) System.out.println(6472650);
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals("None") && s.getMatchStatus() == getMatchStatus(goalDifference)
										&& s.getReward() == reward){
									s.incrementOccurrence();
									stateincCount++;
									endState = s;
									endStateExists = true;

									break;
								}
							}
							if (!endStateExists){
								if (eventID == 6472650) System.out.println(6472650);
								endState = new State(stateID, nextZone, "None", period, getMatchStatus(goalDifference), reward);
								stateList.add(endState);
								stateID++;
							}

						}
					}
				}
				else { //nextAction er hodeduell
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == endZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals("None") && s.getMatchStatus() == getMatchStatus(goalDifference)
								&& s.getReward() == reward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, endZone, "None", period, getMatchStatus(goalDifference), reward);
						stateList.add(endState);
						stateID++;
					}
				}
			}
			else if (nextAction.equals("Ball recovery")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == nextZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == getMatchStatus(goalDifference)
							&& s.getReward() == reward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, nextZone, "None", statePeriod, getMatchStatus(goalDifference), reward);
					stateList.add(endState);
					stateID++;
				}
			}
			else if (action.equals("Ball carry") || action.equals("Aerial duel") || action.equals("Ball recovery")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
							&& s.getReward() == reward){
						s.incrementOccurrence();
						stateincCount++;
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
					if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == nextMatchStatus
							&& s.getReward() == reward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, endZone, otherTeam, statePeriod, nextMatchStatus, reward);
					stateList.add(endState);
					stateID++;
				}
			}

			else if(nextAction.equals("Goal")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						stateincCount++;
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
			else if (nextAction.equals("End of period")){ //neste event er ikke m�l
				prevEndOfPeriod = true;
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						stateincCount++;
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
			else { //action er en av de som ikke er sjekket eksplisitt (i.e. tackle, interception etc)

				if (nextAction.equals("Out of play")){ //neste er out of play
					eventSet.next();
					eventSet.next();
					nextZone = StateBuilder.getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
					eventSet.previous();
					eventSet.previous();
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == matchStatus
								&& s.getReward() == nextReward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, nextZone, otherTeam, period, matchStatus, nextReward);
						stateList.add(endState);
						stateID++;
					}
				}
				else if (nextAction.equals("Foul committed")){
					eventSet.next();
					eventSet.next();
					nextZone = StateBuilder.getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
					eventSet.previous();
					eventSet.previous();
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(nextOtherTeam) && s.getMatchStatus() == matchStatus
								&& s.getReward() == nextReward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, nextZone, nextOtherTeam, period, matchStatus, nextReward);
						stateList.add(endState);
						stateID++;
					}
				}
				else {
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == nextMatchStatus
								&& s.getReward() == nextReward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, nextZone, nextTeam, period, nextMatchStatus, nextReward);
						stateList.add(endState);
						stateID++;
					}
				}

			}

			//lager eller oppdaterer transitions
			StateTransition thisTransition = new StateTransition(startState, endState, action);
			boolean transitionExists = false;
			if (transList.size()==0){
				thisTransition.setStateTransitionID(stateTransitionID);
				transList.add(thisTransition);
				String sql = "UPDATE Event SET StateTransitionID="+stateTransitionID+" WHERE EventID="+eventID;
				stateTransitionID++;
				sqlList.add(sql);
			}
			else {
				for (int i = 0; i < transList.size(); i++){
					StateTransition st = transList.get(i);
					if (st.getAction().equals(thisTransition.getAction()) && st.getStartState().getStateID() == thisTransition.getStartState().getStateID() && st.getEndState().getStateID() == thisTransition.getEndState().getStateID()){
						st.incrementOccurence();
						transitionExists = true;
						String sql = "UPDATE Event SET StateTransitionID="+st.getStateTransitionID()+" WHERE EventID="+eventID;
						sqlList.add(sql);
					}
				}
				if (!transitionExists){
					thisTransition.setStateTransitionID(stateTransitionID);
					transList.add(thisTransition);
					String sql = "UPDATE Event SET StateTransitionID="+stateTransitionID+" WHERE EventID="+eventID;
					stateTransitionID++;
					sqlList.add(sql);
				}
			}

			startState = endState;
			prevAction = action;
		}
		System.out.println(stateincCount);

		DatabaseHandler.updateEventStateID(sqlList);
		DatabaseHandler.insertStatesAndTrans(stateList, transList);

	}
	private static int getMatchStatus(int goaldifference){
		if (goaldifference>0) return 1;
		else if (goaldifference<0) return -1;
		else return 0;
	}

	public static void setStateAction() throws ClassNotFoundException, SQLException{
		ResultSet stateTransSet = DatabaseHandler.getDatabaseStateTrans();
		Hashtable<Integer, Hashtable<String, Integer>> stateAction = new Hashtable<Integer, Hashtable<String, Integer>>();
		while (stateTransSet.next()){
			int stateID = stateTransSet.getInt("StartID");
			String action = stateTransSet.getString("Action");
			int occurrence = stateTransSet.getInt("Occurrence");
			if(stateAction.containsKey(stateID)){
				Hashtable<String, Integer> actionOccurrence = stateAction.get(stateID);
				if (actionOccurrence.containsKey(action)){
					int prevOcc = actionOccurrence.get(action);
					actionOccurrence.put(action, prevOcc+occurrence);
					stateAction.put(stateID, actionOccurrence);
				}
				else{
					Hashtable<String, Integer> actionOcc = new Hashtable<String, Integer>();
					actionOcc.put(action, occurrence);
					stateAction.put(stateID, actionOcc);
				}
			}
			else{
				Hashtable<String, Integer> actionOcc = new Hashtable<String, Integer>();
				actionOcc.put(action, occurrence);
				stateAction.put(stateID, actionOcc);
			}
		}
		DatabaseHandler.insertStateAction(stateAction);
	}

}
