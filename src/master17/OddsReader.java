package master17;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OddsReader {
	public static void readOdds() throws ClassNotFoundException, SQLException, IOException{
		ResultSet gamesRS = DatabaseHandler.getGamesAndTeams();
		String fileName = "C:/Users/Håkon/Documents/Skole/NTNU/Vår 2017/Master/Data/Odds/2016.csv";
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line;
		ArrayList<GameOutcome> gameList = new ArrayList<GameOutcome>();
		while((line=reader.readLine())!=null){
			List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
			while(gamesRS.next()){
				if(items.get(0).equals(gamesRS.getString("HomeID")) && items.get(1).equals(gamesRS.getString("AwayID"))){
					System.out.println(line);
					double totalprob = 1/Double.parseDouble(items.get(2)) + 1/Double.parseDouble(items.get(3)) + 1/Double.parseDouble(items.get(4));
					double homep = (1/Double.parseDouble(items.get(2)))/totalprob;
					double drawp = (1/Double.parseDouble(items.get(3)))/totalprob;
					double awayp = (1/Double.parseDouble(items.get(4)))/totalprob;
					GameOutcome go = new GameOutcome(gamesRS.getInt("GameID"),homep,drawp,awayp);
					gameList.add(go);
					gamesRS.beforeFirst();
					break;
				}
			}
		}
		DatabaseHandler.insertGameOutcomes(gameList);
	}

}
