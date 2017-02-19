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



	}



	public static void setVG(){

	}

	public static void setAOF(Hashtable<Integer, PlayerRating> rt) throws IOException, SQLException, ClassNotFoundException{
		BufferedReader in = new BufferedReader(new FileReader("player_rating/aof16.txt"));
		ResultSet rs = DatabaseHandler.getFullPlayers();
		String line;
		while((line = in.readLine()) != null){
			String[] player = line.split("\t");
			String name = player[1];
			Float rate = Float.parseFloat(player[5].replaceAll(",", "."));
			int playerID = getIDFromName(name, rs);
			if(playerID != 0){
				rt.remove(playerID);
				PlayerRating pr = new PlayerRating(playerID);
				pr.setAof16(rate);
				rt.put(playerID, pr);
			}
			else{
				System.out.println(name);
			}
		}
		in.close();
//		DatabaseHandler.insertPlayerRating(rt);
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
		}
		return playerID;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		setRankings();
	}

}
