package master17;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

public class Validation {
	public static void gameAverageImpact(Hashtable<Integer, Hashtable<Integer, Double>> playerImpact) throws ClassNotFoundException, SQLException, IOException{
		ResultSet startingElevenRS = DatabaseHandler.getStartingElevens();
		ArrayList<ArrayList<Game>> gameGroups = createGroups();
		Hashtable<Integer, Integer> gameGroup = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Game> games = new Hashtable<Integer, Game>();	
		for (int i = 0; i < gameGroups.size(); i++){
			for (Game game: gameGroups.get(i)){
				gameGroup.put(game.getGame_id(), i);
				games.put(game.getGame_id(), game);
			}
		}


		for (int i = 0; i < 10; i++){ //outer loop, k-folding with k=10
			Hashtable<Integer, Double> playerGroupCount = new Hashtable<Integer, Double>();
			Hashtable<Integer, Double> impact = new Hashtable<Integer, Double>(); //hver players gjennomsnittlige impact for de 9 gruppene som ikke er hold out
			for (int j = 0; j < 10; j++){ //itererer hver av de 10 gruppene
				if (j==i) continue;
				Set<Integer> players = playerImpact.get(j).keySet(); 
				for (Integer player: players){	
					//går gjennom alle players som har rating i gruppen
					if (impact.containsKey(player)){

						impact.put(player, impact.get(player)+playerImpact.get(j).get(player));
						playerGroupCount.put(player, playerGroupCount.get(player)+1.0);
					}
					else {
						impact.put(player, playerImpact.get(j).get(player));
						playerGroupCount.put(player, 1.0);
					}	
				} 
			}
			Set<Integer> players = impact.keySet();
			for (int player: players){ // normaliserer impact i forhold til hvor mange grupper spilleren fantes i
				impact.put(player, impact.get(player)/playerGroupCount.get(player));
			}
			ResultSet rs = startingElevenRS; //kortere variabelnavn
			FileWriter writerIn = new FileWriter("C:/Users/Håkon/Documents/Skole/NTNU/Vår 2017/Master/kfolding/inI2_"+i);
			FileWriter writerOut = new FileWriter("C:/Users/Håkon/Documents/Skole/NTNU/Vår 2017/Master/kfolding/outI2_"+i);
			int count = 0;
			while (rs.next()){ //går gjennom hver game
				count ++;
				double homeCount = 0.0; //antall hjemmespillere med rating
				double awayCount = 0.0; //antall bortespillere med rating
				double homeRating = 0.0;
				double awayRating = 0.0;
				double ratingDifference;
				int gameID = rs.getInt("Game.GameID");
				Integer[] homePlayers = {rs.getInt("H.P2"), rs.getInt("H.P3"), rs.getInt("H.P4"), rs.getInt("H.P5"), 
						rs.getInt("H.P6"), rs.getInt("H.P7"), rs.getInt("H.P8"), rs.getInt("H.P9"),
						rs.getInt("H.P10"), rs.getInt("H.P11")};
				Integer[] awayPlayers = {rs.getInt("A.P2"), rs.getInt("A.P3"), rs.getInt("A.P4"), rs.getInt("A.P5"), 
						rs.getInt("A.P6"), rs.getInt("A.P7"), rs.getInt("A.P8"), rs.getInt("A.P9"),
						rs.getInt("A.P10"), rs.getInt("A.P11")};
				for (int player: homePlayers){
					if (impact.containsKey(player)){
						homeCount += 1.0;
						homeRating += impact.get(player);
					}
				}
				for (int player: awayPlayers){
					if (impact.containsKey(player)){
						awayCount += 1.0;
						awayRating += impact.get(player);
					}
				}
				homeRating = homeRating/homeCount;
				awayRating = awayRating/awayCount;
				ratingDifference = homeRating - awayRating;
				System.out.println(gameID+ ", Score: " + games.get(gameID).getScore() + ", ratingDiff: " + ratingDifference);
				System.out.println(i);
				String toWrite = gameID +"," + games.get(gameID).getScore() +"," + ratingDifference + "\n";
				if (gameGroup.get(gameID)==i) writerOut.write(toWrite);
				else writerIn.write(toWrite);
			}
			writerIn.close();
			writerOut.close();
			startingElevenRS.beforeFirst();
			System.out.println(count);
		}
	}



	public static Hashtable<Integer, Hashtable<Integer, Double>> playerImpactPerGroup(ArrayList<ArrayList<Game>> gameGroups) throws ClassNotFoundException, SQLException{
		Hashtable<Integer, Integer> gameGroup = new Hashtable<Integer, Integer>(); //gameID peker på gruppenummer (k-folding gruppe)
		Hashtable<Integer, Game> games = new Hashtable<Integer, Game>();			//gameID peker på Game-instans
		Hashtable<Integer, Hashtable<Integer, Double>> playerImpact = new Hashtable<Integer, Hashtable<Integer, Double>>(); //groupNumber peker på PlayerID som peker på Rating
		//groupNumber peker på PlayerID som peker på antall games spilleren har i denne gruppen
		Hashtable<Integer, Hashtable<Integer, Double>> groupPlayerCount = new Hashtable<Integer, Hashtable<Integer, Double>>(); 
		ResultSet pv1RS = DatabaseHandler.getPV2();

		for (int i = 0; i < gameGroups.size(); i++){
			for (Game game: gameGroups.get(i)){
				gameGroup.put(game.getGame_id(), i);
				games.put(game.getGame_id(), game);
			}
			playerImpact.put(i, new Hashtable<Integer, Double>());
			groupPlayerCount.put(i, new Hashtable<Integer, Double>());
		}
		while (pv1RS.next()){
			int gameID = pv1RS.getInt("GameID");
			int playerID = pv1RS.getInt("PlayerID");
			double total = pv1RS.getDouble("Total");
			int group = gameGroup.get(gameID);
			Hashtable<Integer, Double> impact = playerImpact.get(group);
			Hashtable<Integer, Double> count = groupPlayerCount.get(group);

			if (impact.containsKey(playerID)){
				impact.put(playerID, (impact.get(playerID)*count.get(playerID)+total)/(count.get(playerID)+1));
				playerImpact.put(group, impact);
				count.put(playerID, count.get(playerID)+1.0);
				groupPlayerCount.put(group, count);
			}
			else {
				impact.put(playerID, total);
				playerImpact.put(group, impact);
				count.put(playerID, 1.0);
				groupPlayerCount.put(group, count);
			}
		}
		return playerImpact;
	}



	public static ArrayList<ArrayList<Game>> createGroups() throws ClassNotFoundException, SQLException{
		ResultSet gameRS = DatabaseHandler.getGames();
		ArrayList<Game> games = new ArrayList<Game>();
		while (gameRS.next()){
			Game game = new Game(gameRS.getInt("GameID"), gameRS.getInt("Score"));
			games.add(game);
		}
		Collections.shuffle(games, new Random(2));
		ArrayList<ArrayList<Game>> gameGroups = new ArrayList<ArrayList<Game>>();
		int prev = 0;
		for (int i = 0; i < 10; i++){
			ArrayList<Game> group =  new ArrayList<Game>();
			group.addAll(games.subList(prev, games.size()*(i+1)/10));
			gameGroups.add(group);
			prev = games.size()*(i+1)/10;
		}
		return gameGroups;
	}

}
