package master17;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class GameTimeReader {

	public static void setPlayerGameTime() throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException{
		Hashtable<Integer, PlayerGameTime> fullTimeTable = new Hashtable<Integer, PlayerGameTime>();
		Hashtable<Integer, Integer> gameTime = new Hashtable<Integer, Integer>();
		ResultSet playerIDs = DatabaseHandler.getPlayers();

		while (playerIDs.next()){
			int playerID = playerIDs.getInt("PlayerID");
			PlayerGameTime playerGameTime = new PlayerGameTime(playerID);
			fullTimeTable.put(playerID, playerGameTime);
		}

		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++){
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Node gameNode = doc.getElementsByTagName("Game").item(0); //Game-noden er det første og eneste elementet med TagName "Game"
	        Element gameElement = (Element) gameNode;  //Node castes til Element for å kunne bruke getAttribute()
	        int season = Integer.parseInt(gameElement.getAttribute("season_id"));
			NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
			for (int j = 0; j < xmlEventList.getLength(); j++){
				Element event = (Element) xmlEventList.item(j);
				int typeID = Integer.parseInt(event.getAttribute("type_id"));

				if (typeID == 34){
					NodeList qualifiers = event.getChildNodes();
					for (int k = 0; k < qualifiers.getLength(); k++){
						Node qualifier = qualifiers.item(k);
						if (qualifier.getNodeType()== Node.ELEMENT_NODE){
							Element qElement = (Element) qualifier;
							if((Integer.parseInt(qElement.getAttribute("qualifier_id")) == 30)){
								String value = qElement.getAttribute("value");
								List<String> idList = Arrays.asList(value.split("\\s*,\\s*"));
								for (int l = 0; l < 11; l++){
									int id = Integer.parseInt(idList.get(l));
									gameTime.put(id ,0);
								}
							}
						}
					}
				}

				else if (typeID == 18 || typeID == 20){ //spiller byttes ut eller går ut av banen pga skade etter antall minutter
					int id = Integer.parseInt(event.getAttribute("player_id"));
					int minute = Integer.parseInt(event.getAttribute("min"));
					PlayerGameTime ptg = fullTimeTable.get(id);
					ptg.incrementGameTime(minute-gameTime.get(id), season);
					fullTimeTable.put(id, ptg);
					gameTime.remove(id);

				}
				else if (typeID == 19 || typeID == 21){ // spiller byttes inn eller kommer inn igjen etter antall minutter
					int id = Integer.parseInt(event.getAttribute("player_id"));
					int minute = Integer.parseInt(event.getAttribute("min"));
					gameTime.put(id, minute);
				}

				else if (typeID == 17){ //Spiller går ut pga rødt kort
					NodeList qualifierList = event.getChildNodes();
					for(int m = 0; m < qualifierList.getLength(); m++){
						if(qualifierList.item(m).getNodeType() == Node.ELEMENT_NODE){
							Element qElement = (Element) qualifierList.item(m);
				    		int qid = Integer.parseInt(qElement.getAttribute("qualifier_id"));
				    		if (qid == 32 || qid== 33){
				    			int id = Integer.parseInt(event.getAttribute("player_id"));
								int minute = Integer.parseInt(event.getAttribute("min"));
								PlayerGameTime ptg = fullTimeTable.get(id);
								ptg.incrementGameTime(minute-gameTime.get(id), season);
								fullTimeTable.put(id, ptg);
								gameTime.remove(id);
				    		}
						}
					}
				}

			}
			Set<Integer> keys = gameTime.keySet();
			for (int id : keys){
				PlayerGameTime ptg = fullTimeTable.get(id);
				ptg.incrementGameTime(90-gameTime.get(id), season);
				fullTimeTable.put(id, ptg);
			}
			gameTime.clear();
			System.out.println(listOfFiles[i].toString());
		}

		DatabaseHandler.insertPlayerGameTime(fullTimeTable);

	}

}
