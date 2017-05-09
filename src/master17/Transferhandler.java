package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Set;

public class Transferhandler {
	public static void averageRatingPerClub() throws ClassNotFoundException, SQLException{
		ResultSet mainRS = DatabaseHandler.getPlayerPerTeam();
		Hashtable<Integer, Hashtable<Integer, Double>> playerTeamImpact = new Hashtable<Integer, Hashtable<Integer, Double>>();
		Hashtable<Integer, Hashtable<Integer, Double>> playerTeamAverage = new Hashtable<Integer, Hashtable<Integer, Double>>();
		while (mainRS.next()){
			int playerID=mainRS.getInt("PlayerID");
			int teamID=mainRS.getInt("TeamID");
			Double impact = mainRS.getDouble("Total");
			if (playerTeamImpact.containsKey(playerID)){
				Hashtable<Integer, Double> teamImpact = playerTeamImpact.get(playerID);
				Hashtable<Integer, Double> average = playerTeamAverage.get(playerID);
				average.put(playerID, 0.0);
				teamImpact.put(teamID, impact);
			}
			else{
				Hashtable<Integer, Double> teamImpact = new Hashtable<Integer, Double>();
				Hashtable<Integer, Double> average = new Hashtable<Integer, Double>();
				teamImpact.put(teamID, impact);
				average.put(teamID, 0.0);
				playerTeamAverage.put(playerID, average);
				playerTeamImpact.put(playerID, teamImpact);		
			}
		}
		Set<Integer> players = playerTeamImpact.keySet();
		for (int p: players){
			Set<Integer> teams = playerTeamImpact.get(p).keySet();
			for (int t: teams){
				ResultSet games = DatabaseHandler.getGameImpacts(p, t);
				double teamImpact=0;
				double gameCount = 0;
				
				while (games.next()){
					double gameImpact = 0;
					gameCount += 1.0;
					for (int i=1; i<11;i++){
						if (games.getInt("P"+i)!=p){
							gameImpact+=games.getDouble("I"+i);
						}
					}
					teamImpact+=gameImpact/9;
				}
				teamImpact=teamImpact/gameCount;
				Hashtable<Integer, Double> average = playerTeamAverage.get(p);
				average.put(t, teamImpact);
				
			}
		}
		for (int p: players){
			Set<Integer> teams = playerTeamImpact.get(p).keySet();
			String output = "" + Integer.toString(p);
			for (int t: teams){
				output += "," + t + "," + playerTeamAverage.get(p).get(t) +"," + playerTeamImpact.get(p).get(t); 
			}
			System.out.println(output);
		}
		
		
	}

}
