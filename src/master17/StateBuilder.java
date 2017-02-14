package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StateBuilder {

	public static void getStatesFromEvents() throws ClassNotFoundException, SQLException{
		ResultSet rs = DatabaseHandler.getDatabaseEvents();
		ArrayList<String> sql = new ArrayList<String>();
		int stateID = 1;
		ArrayList<State> stateList = new ArrayList<State>();
		int counter = 0;
		long startTime = System.nanoTime();
		while (rs.next()){
			counter++;
			String eventID = rs.getString("EventID"); // event id
			int zone;
			String action = rs.getString("Action");
			int period;
			int matchStatus;
			int manpowerDifference;
			boolean home;
			if (action.equals("Goal") || action.equals("Goalkeeper")){
				period = 0;
				matchStatus = 0;
				manpowerDifference = 0;
				zone = 0;
				home = rs.getInt("HomeID") == rs.getInt("TeamID");
			}
			else if (action.equals("Out of play")){
				period = 0;
				matchStatus = 0;
				manpowerDifference = 0;
				zone = 0;
				home = false;
			}
			else if(action.equals("End of period")){
				period = getPeriod(rs.getInt("Minute"), rs.getInt("Period"));
				matchStatus = 0;
				manpowerDifference = 0;
				zone = 0;
				home = false;
			}
			else {
				period = getPeriod(rs.getInt("Minute"), rs.getInt("Period"));
				matchStatus = getMatchStatus(rs.getInt("GoalDifference"), rs.getInt("HomeID"), rs.getInt("TeamID"));
				manpowerDifference = getManpowerDifference(rs.getInt("ManpowerDifference"), rs.getInt("HomeID"), rs.getInt("TeamID"));
				zone = getZoneFromCoordinates(rs.getFloat("Xstart"),rs.getFloat("Ystart"));
				home = rs.getInt("HomeID") == rs.getInt("TeamID");
			}
			int reward = getReward(action, home);

			if (stateList.size() == 0){ //stateList er tom
				stateList.add(new State(stateID,zone, home, action, period, manpowerDifference, matchStatus, reward));
				sql.add("UPDATE Event SET StateID="+stateID+" WHERE EventID="+eventID);
				stateID++;
			}
			else { //stateList ikke tom
				boolean stateExists = false;
				for (int i =0; i<stateList.size();i++){
					State s = stateList.get(i);
					if(s.getAction().equals(action) && s.getZone() == zone && s.getPeriod() == period
							&& s.isHome()==home && s.getMatchStatus() == matchStatus
							&& s.getManpowerDiff() == manpowerDifference && s.getReward() == reward){
						s.incrementOccurrence(); //Oppdaterer occurence i state og legger til StateID på eventet i db
						sql.add("UPDATE Event SET StateID="+s.getStateID()+" WHERE EventID="+eventID);
						stateExists = true;
						break;
					}
				}
				if (!stateExists){ //Hvis staten ikke finnes fra før i db, legg til passende state
					stateList.add(new State(stateID,zone, home, action, period, manpowerDifference, matchStatus, reward));
					sql.add("UPDATE Event SET StateID="+stateID+" WHERE EventID="+eventID);
					stateID++;
				}
			}
			if (counter%1000==0){
				long endTime = System.nanoTime();
				System.out.println("Oppdatert "+counter+ " events. Tid for siste 1000 events: "+ (endTime-startTime)/Math.pow(10, 9)+" sekunder" );
				startTime = System.nanoTime();
			}
		}
		DatabaseHandler.insertStates(stateList);
		DatabaseHandler.updateEventStateID(sql);
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


