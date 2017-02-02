package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StateBuilder {

	public static void setStateFromEvent(Game game) throws ClassNotFoundException, SQLException{
		ResultSet rs = DatabaseHandler.getDatabaseEvents(game.getGame_id());
		ArrayList<State> stateList = new ArrayList<State>();
		while (rs.next()){
			int zone = getZoneFromCoordinates(rs.getFloat("Xstart"),rs.getFloat("Ystart"));
			String action = rs.getString("Action");
			int period = getPeriod(rs.getInt("Minute"), rs.getInt("Period"));
			boolean home = game.getHome_team_id() == rs.getInt("TeamID");
			int matchStatus = getMatchStatus(rs.getInt("GoalDifference"), game.getHome_team_id(), rs.getInt("TeamID"));
			int manpowerDifference = getManpowerDifference(rs.getInt("ManpowerDifference"), game.getHome_team_id(), rs.getInt("TeamID"));
			int reward = getReward(action);
			if (stateList.size() == 0){
				stateList.add(new State(zone, home, action, period, manpowerDifference, matchStatus, reward));
			}
			else { //stateList ikke tom
				boolean stateExists = false;
				for (int i =0; i<stateList.size();i++){
					State s = stateList.get(i);

					if(s.getZone() == zone && s.getAction().equals(action) && s.getPeriod() == period
							&& s.isHome()==home && s.getMatchStatus() == matchStatus && s.getManpowerDiff() == manpowerDifference){
						s.incrementOccurrence();
						stateExists = true;
						break;
					}
				}
				if (!stateExists){
					stateList.add(new State(zone, home, action, period, manpowerDifference, matchStatus, reward));
				}
			}
		}
	}

	public static int getZoneFromCoordinates(float x, float y){
		int zone = 0;
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

	public static int getReward(String action){
		if (action.equals("Goal")) return 1;
		else return 0;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Game game = new Game(839685,305,197,1,2016);
		setStateFromEvent(game);
	}
}


