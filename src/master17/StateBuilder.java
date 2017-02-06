package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StateBuilder {

	public static ArrayList<State> getStatesFromEvents(Game game, ArrayList<State> stateList) throws ClassNotFoundException, SQLException{
		ResultSet rs = DatabaseHandler.getDatabaseEvents(game.getGame_id());
		ArrayList<String> sql = new ArrayList<String>();
		int stateID = stateList.size()+1;
		while (rs.next()){
			String eventID = rs.getString("OptaEventID"); //opta event id
			int zone;
			String action = rs.getString("Action");
			int period;
			int matchStatus;
			int manpowerDifference;
			boolean home;
			if (action.equals("Out of play")){
				period = 0;
				matchStatus = 0;
				manpowerDifference = 0;
				zone = 0;
				home = false;
			}	
			else {
				period = getPeriod(rs.getInt("Minute"), rs.getInt("Period"));
				matchStatus = getMatchStatus(rs.getInt("GoalDifference"), game.getHome_team_id(), rs.getInt("TeamID"));
				manpowerDifference = getManpowerDifference(rs.getInt("ManpowerDifference"), game.getHome_team_id(), rs.getInt("TeamID"));
				zone = getZoneFromCoordinates(rs.getFloat("Xstart"),rs.getFloat("Ystart"));
				home = game.getHome_team_id() == rs.getInt("TeamID");
			}
			int reward = getReward(action, home);
			
			if (stateList.size() == 0){ //stateList er tom
				if (reward!=0){ //Hvis første event som sjekkes er mål
					stateList.add(new State(stateID,0,home,action,0,0,0,reward));
					sql.add("UPDATE Event SET StateID="+stateID+" WHERE OptaEventID="+eventID);
					stateID++;
				}
				else if (action.equals("Out of play")){
					stateList.add(new State(stateID,0,false,action,0,0,0,0));
					sql.add("UPDATE Event SET StateID="+stateID+" WHERE OptaEventID="+eventID);
					stateID++;
				}
				else if (action.equals("End of period")){
					continue;
				}
				else{ //Alle andre events
					stateList.add(new State(stateID,zone, home, action, period, manpowerDifference, matchStatus, reward));
					sql.add("UPDATE Event SET StateID="+stateID+" WHERE OptaEventID="+eventID);
					stateID++;
				}
			}
			else { //stateList ikke tom
				boolean stateExists = false;
				for (int i =0; i<stateList.size();i++){
					State s = stateList.get(i);
					if (reward!=0){ //Hvis event er mål, lag state for mål
						if (s.getReward()==reward){
							s.incrementOccurrence();
							sql.add("UPDATE Event SET StateID="+s.getStateID()+" WHERE OptaEventID="+eventID);
							stateExists = true;
							break;
						}
						continue;
					}
					if (action.equals("End of period")){
						break;
					}
					if(s.getZone() == zone && s.getAction().equals(action) && s.getPeriod() == period
							&& s.isHome()==home && s.getMatchStatus() == matchStatus && s.getManpowerDiff() == manpowerDifference){
						s.incrementOccurrence(); //Oppdaterer occurence i state og legger til StateID på eventet i db
						sql.add("UPDATE Event SET StateID="+s.getStateID()+" WHERE OptaEventID="+eventID);
						stateExists = true;
						break;
					}
				}
				if (!stateExists){ //Hvis staten ikke finnes fra før i db, legg til passende state
					if (reward != 0){
						stateList.add(new State(stateID,0,home,action,0,0,0,reward));
						sql.add("UPDATE Event SET StateID="+stateID+" WHERE OptaEventID="+eventID);
						stateID++;
					}
					else if (action.equals("Out of play")){
						stateList.add(new State(stateID,0,false,action,0,0,0,0));
						sql.add("UPDATE Event SET StateID="+stateID+" WHERE OptaEventID="+eventID);
						stateID++;
					}
					else{
						stateList.add(new State(stateID,zone, home, action, period, manpowerDifference, matchStatus, reward));
						sql.add("UPDATE Event SET StateID="+stateID+" WHERE OptaEventID="+eventID);
						stateID++;
					}
				}
			}
		}
		DatabaseHandler.updateEventStateID(sql);
		return stateList;
	}

	public static int getZoneFromCoordinates(float x, float y){
		if (x < 50){
			if (x < 50/3){
				if (y < 100.0/3){
					return 3;
				}
				else if (y < 200.0/3){
					return 2;
				}
				else{
					return 1;
				}
			}
			else if (x < 100.0/3){
				if (y < 100.0/3){
					return 6;
				}
				else if (y < 200.0/3){
					return 5;
				}
				else{
					return 4;
				}
			}
			else{
				if (y < 100.0/3){
					return 9;
				}
				else if (y < 200.0/3){
					return 8;
				}
				else{
					return 7;
				}
			}
		}
		else { //x>=50
			if (x < 4.0/6*100){
				if (y < 100/4){
					return 13;
				}
				else if (y < 200.0/4){
					return 12;
				}
				else if (y < 300.0/4){
					return 11;
				}
				else{
					return 10;
				}
			}
			else if ( x < 5.0/6*100){

				if (y < 100.0/4){
					return 17;
				}
				else if (y < 200.0/4){
					return 16;
				}
				else if (y < 300.0/4){
					return 15;
				}
				else{
					return 14;
				}
			}
			else{
				if (y < 100.0/4){
					return 21;
				}
				else if (y < 200.0/4){
					return 20;
				}
				else if (y < 300.0/4){
					return 19;
				}
				else{
					return 18;
				}
			}
		}
	}

	public static int getPeriod(int minutes, int period){
		if (minutes < 23){
			return 1;
		}
		else if (period == 1){
			return 2;
		}
		else if(minutes < 68){
			return 3;
		}
		else return 4;
	}

	public static int getMatchStatus(int gd, int homeTeamID, int currentTeamID){
		if (gd==0) return 0;
		else if (homeTeamID == currentTeamID){
			if (gd > 0) return 1;
			else return -1;
		}
		else {
			if (gd > 0) return -1;
			else return 1;
		}
	}

	public static int getManpowerDifference(int mpd, int homeTeamID, int currentTeamID){
		if (mpd==0) return 0;
		else if (homeTeamID == currentTeamID){
			if (mpd > 0) return 1;
			else return -1;
		}
		else {
			if (mpd > 0) return -1;
			else return 1;
		}
	}

	public static int getReward(String action, boolean home){
		if (action.equals("Goal")){
			if(home) return 1;
			else return -1;
		}
		else return 0;
	}


}


