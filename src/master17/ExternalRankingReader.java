package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ExternalRankingReader {

	public static void setRankings() throws ClassNotFoundException, SQLException, IOException{
		ResultSet rs1 = DatabaseHandler.getFullPlayers();
		Hashtable<Integer,PlayerRating> ratingTable = new Hashtable<Integer, PlayerRating>();
		while (rs1.next()){
			int playerID = rs1.getInt("PlayerID");
			PlayerRating pr = new PlayerRating(rs1.getInt("PlayerID"));
			ratingTable.put(playerID, pr);
		}
		setAOF(ratingTable);
//		setVG(ratingTable);
	}

	public static void setVG(Hashtable<Integer, PlayerRating> rt) throws ClassNotFoundException, SQLException, NumberFormatException, IOException{
		String fileName = "player_rating/vg14.txt";
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		ResultSet rs = DatabaseHandler.getFullPlayers();
		String line;
		int season = 0;
		switch (fileName){
			case "player_rating/vg14.txt":
				season = 14;
				break;
			case "player_rating/vg15.txt":
				season = 15;
				break;
			case "player_rating/vg16.txt":
				season = 16;
				break;
			case "player_rating/vg17.txt":
				season = 17;
				break;
		}
		int lineNumber = 0;
		float rate = 0;
		if (season == 16 || season == 17){
			String name = "";
			while((line = in.readLine()) != null){
				lineNumber++;
				if (lineNumber == 1 || lineNumber == 2){
					continue;
				}
				if (lineNumber == 3){
					String [] player = line.split(",");
					name = player[0];
					continue;
				}
				if (lineNumber == 4){
					rate = Float.parseFloat(line.substring(0,4).replace(",", "."));
					lineNumber = 0;
				}
				int playerID = getIDFromName(name, rs);
				if(playerID != 0){
					rt.remove(playerID);
					PlayerRating pr = new PlayerRating(playerID);
					pr.setVg(rate,season);
					rt.put(playerID, pr);
				}
				else if (playerID == 0){
					System.out.println(name);
				}
			}
			in.close();
		}
		else{
			String name = "";
			while((line = in.readLine()) != null){
				if (lineNumber == 0){
					lineNumber++;
				}
				if (lineNumber == 1){
					String [] player = line.replace(".",",").split(",");
					if (player.length == 3){
						name = player[1];
						lineNumber++;
						continue;
					}
					else if (player.length == 2){
						name = player[0];
						name = name.substring(1);
						lineNumber++;
						continue;
					}
				}
				if (lineNumber == 2){
					rate = Float.parseFloat(line.substring(0,4).replaceAll(",", "."));
					lineNumber = 0;
				}
				int playerID = getIDFromName(name, rs);
				if(playerID != 0){
					rt.remove(playerID);
					PlayerRating pr = new PlayerRating(playerID);
					pr.setVg(rate,season);
					rt.put(playerID, pr);
				}
				else if (playerID == 0){
					System.out.println(name);
				}
			}
			in.close();
		}

		DatabaseHandler.insertPlayerRatingVG(rt, season);
	}

	public static void setAOF(Hashtable<Integer, PlayerRating> rt) throws IOException, SQLException, ClassNotFoundException{
		String fileName = "player_rating/aof16.txt";
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		ResultSet rs = DatabaseHandler.getFullPlayers();
		String line;
		int season = 0;
		switch (fileName){
			case "player_rating/aof14.txt":
				season = 14;
				break;
			case "player_rating/aof15.txt":
				season = 15;
				break;
			case "player_rating/aof16.txt":
				season = 16;
				break;
			case "player_rating/aof17.txt":
				season = 17;
				break;
		}
		while((line = in.readLine()) != null){
			String[] player = line.split("\t");
			String name = player[1];
			Float rate = Float.parseFloat(player[5].replaceAll(",", "."));
			int playerID = getIDFromName(name, rs);
			if(playerID != 0){
				rt.remove(playerID);
				PlayerRating pr = new PlayerRating(playerID);
				pr.setAof(rate,season);
				rt.put(playerID, pr);
			}
			else{
				System.out.println(name);
			}
		}
		in.close();
		DatabaseHandler.insertPlayerRatingAOF(rt, season);
	}

	public static int getIDFromName(String name, ResultSet rs) throws SQLException, ClassNotFoundException{
		int playerID = 0;
		rs.beforeFirst();
		while(rs.next()){
			String dbName = (rs.getString("FirstName") + " " + rs.getString("LastName"));
			if(name.equals(dbName)){
				playerID = rs.getInt("PlayerID");
				return playerID;
			}
			else {
				int countEqual = 0;
				if (name.length()==dbName.length()){
					for (int i = 0; i < name.length(); i++){
						boolean equal = name.charAt(i)==dbName.charAt(i);
						if (equal){
							countEqual++;
						}
					}
					if (countEqual >= name.length()-2){
						playerID = rs.getInt("PlayerID");
						return playerID;
					}
				}
			}
		}
		return playerID;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		setRankings();
	}

}
