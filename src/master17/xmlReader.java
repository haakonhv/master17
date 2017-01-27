package master17;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class xmlReader {
	public static Document getDocument(String fileName) throws ParserConfigurationException, SAXException, IOException{ //lager Document object fra xml-fil med navn fileName som input (Opta-fil)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileName);
        return doc;
    }
	public static Game getGame(Document doc){
        Node gameNode = doc.getElementsByTagName("Game").item(0); //Game-noden er det første og eneste elementet med TagName "Game"
        Element gameElement = (Element) gameNode;  //Node castes til Element for å kunne bruke getAttribute()
        int game_id = Integer.parseInt(gameElement.getAttribute("id"));
        int home_team_id = Integer.parseInt(gameElement.getAttribute("home_team_id"));
        int away_team_id = Integer.parseInt(gameElement.getAttribute("away_team_id"));
        int matchday = Integer.parseInt(gameElement.getAttribute("matchday"));
        int season = Integer.parseInt(gameElement.getAttribute("season_id"));
        Game game = new Game(game_id, home_team_id, away_team_id, matchday, season);
        return game;
    }
	public static ArrayList<Event> getEventList(Document doc, Game game){
		int game_id = game.getGame_id();
		NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
		ArrayList<Event> eventList = new ArrayList<Event>(); //forel�pig tom liste som skal inneholde alle eventer fra XML-filen
		int gd = 0; //goal difference
		int mp = 0; //manpower difference
		int tempID=0;
		int sequence=1; //sequence nummer internt i en game
		int number = 0;
		for (int i=0; i<xmlEventList.getLength();i++){ //l�kke som g�r gjennom hver event-node og lager event-objekter
			Element xmlEvent = (Element) xmlEventList.item(i);
			int event_id = Integer.parseInt(xmlEvent.getAttribute("id")); //setter opta-event id;
			String action_type = getActionType(xmlEvent);
			if (action_type.equals("skip")){
				continue;
			}
			int team_id = Integer.parseInt(xmlEvent.getAttribute("team_id"));
			if (action_type.equals("Red card")){
				if (team_id == game.getHome_team_id()){
					mp = mp - 1;
				}
				else {
					mp = mp + 1;
				}
				continue;
			}
			number = number+1;
			int period = 0;
		  	if(Integer.parseInt(xmlEvent.getAttribute("period_id"))==1){ //f�rste omgang
         		period = 1;
         	}
         	else if(Integer.parseInt(xmlEvent.getAttribute("period_id"))==2){ //andre omgang
         		period = 2;
         	}
         	else {
         		period = 16;//optas pre-match period-kode
         	}
		  	int minute = Integer.parseInt(xmlEvent.getAttribute("min"));
		  	int second = Integer.parseInt(xmlEvent.getAttribute("sec"));
		  	float xstart = Float.parseFloat(xmlEvent.getAttribute("x"));
         	float ystart = Float.parseFloat(xmlEvent.getAttribute("y"));
         	float[] endCoordinates = getEndCoordinates(xmlEvent);
         	float xend = endCoordinates[0];
         	float yend = endCoordinates[1];
         	int outcome = Integer.parseInt(xmlEvent.getAttribute("outcome"));
         	int player_id = Integer.parseInt(xmlEvent.getAttribute("player_id"));
         	if (!action_type.equals("Goal")){
         		eventList.add(new Event(event_id, action_type, outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
         	}
         	else {
         		eventList.add(new Event(event_id, "Shot", outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
         		number = number + 1;
         		eventList.add(new Event(tempID+1, action_type, outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
         		sequence += 1;
         		tempID+=1;
         		if (team_id == game.getHome_team_id()){
         			gd = gd+1;
         		}
         		else {
         			gd = gd - 1;
         		}
         	}
         	if (action_type.equals("Out of play")){
         		sequence += 1;
         	}
		}
		return eventList;
	}

	private static String getActionType(Element xmlEvent){ //finner actiontype til et event

		int typeid = Integer.parseInt(xmlEvent.getAttribute("type_id"));
		String actiontype;
		if (typeid == 1){
			boolean cross = false; //hjelpevariabel for � ikke klassifisere cross som langpasning
			boolean longpass = false; //hjelpevariabel, som over
			NodeList qualifierList = xmlEvent.getChildNodes(); //liste over alle qualifiers til eventet, brukes for � skille cross, long ball, corner, free kick og vanlig pasning
			for(int i=0; i<qualifierList.getLength();i++){
				if(qualifierList.item(i).getNodeType() == Node.ELEMENT_NODE){
					Element q = (Element) qualifierList.item(i);
					int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));
					if (qualifier_id == 5){
						actiontype = "Free kick pass";
						return actiontype;
					}
					else if (qualifier_id == 6){
						actiontype = "Corner taken";
						return actiontype;
					}
					else if (qualifier_id == 1){
						longpass = true;
					}
					else if (qualifier_id == 107){
						actiontype = "Throw in taken";
						return actiontype;
					}
					else if (qualifier_id == 2){
						cross = true;
					}
				}
			}
			if (cross == true){
				actiontype = "Cross";
				return actiontype;
			}
			else if (longpass == true){
				actiontype = "Long pass";
				return actiontype;
			}
			else {
				actiontype = "Pass";
				return actiontype;
			}
		}
		else if (typeid == 3){
			actiontype = "Take on";
			return actiontype;
		}
		else if (typeid == 61){
			actiontype = "Ball touch";
			return actiontype;
		}
		else if (typeid == 50){
			actiontype = "Dispossessed";
			return actiontype;
		}
		else if (typeid == 7){
			actiontype = "Tackle";
			return actiontype;
		}
		else if (typeid == 8){
			actiontype = "Interception";
			return actiontype;
		}
		else if (typeid == 12){
			actiontype = "Clearance";
			return actiontype;
		}
		else if (typeid == 49){
			actiontype = "Ball recovery";
			return actiontype;
		}
		else if (typeid == 44){
			actiontype = "Aerial duel";
			return actiontype;
		}
		else if (typeid == 4){
			if (Integer.parseInt(xmlEvent.getAttribute("outcome"))==0){
				actiontype = "Foul committed";
			}
			else {
				actiontype = "Fouled";
			}
			return actiontype;
		}
		else if (typeid == 5){
			if (Integer.parseInt(xmlEvent.getAttribute("outcome")) == 0){
				if (Float.parseFloat(xmlEvent.getAttribute("x"))>100){
					actiontype = "Out of play";
				}
				else {
					actiontype = "skip";
				}
			}
			else {
				actiontype = "skip";
			}
			return actiontype;
		}
		else if (typeid == 17){
			NodeList qualifierList = xmlEvent.getChildNodes();
			for(int i=0; i<qualifierList.getLength();i++){
				if(qualifierList.item(i).getNodeType() == Node.ELEMENT_NODE){
					Element q = (Element) qualifierList.item(i);
		    		int qid = Integer.parseInt(q.getAttribute("qualifier_id"));
		    		if (qid == 32 || qid== 33){
		    			actiontype = "Red card";
		    			return actiontype;
		    		}
		    		else {
		    			actiontype = "skip";
		    			return actiontype;
		    		}
				}


			}
			actiontype = "skip";
			return actiontype;
		}
		else if (typeid == 13 || typeid == 14 || typeid == 15){
			actiontype = "Shot";
			return actiontype;
		}
		else if (typeid == 16){
			actiontype = "Goal";
			return actiontype;
		}
		else {
			actiontype = "skip";
			return actiontype;
		}
	}

	private static float[] getEndCoordinates(Element xmlEvent){
		float xEnd = 0;
		float yEnd = 0;
		boolean xUpdated = false;
		boolean yUpdated = false;
		float[] endCoordinates = {0,0};
		System.out.println("getEndCoords");
		NodeList qualifierList = xmlEvent.getChildNodes();
		for(int i=0; i<qualifierList.getLength();i++){
			if(qualifierList.item(i).getNodeType() == Node.ELEMENT_NODE){
				Element q = (Element) qualifierList.item(i);
	    		int qid = Integer.parseInt(q.getAttribute("qualifier_id"));
	    		if (qid == 140){
	    			xEnd = Float.parseFloat(q.getAttribute("value"));
	    			xUpdated = true;
	    			System.out.println("fant xend");
	    		}
	    		else if (qid == 141){
	    			yEnd = Float.parseFloat(q.getAttribute("value"));
	    			yUpdated = true;
	    			System.out.println("fant yend");
	    		}
			}
		}
		if (xUpdated & yUpdated) {
			endCoordinates[0] = xEnd;
			endCoordinates[1] = yEnd;
		}
		else {
			endCoordinates[0] = Float.parseFloat(xmlEvent.getAttribute("x"));
			endCoordinates[1] = Float.parseFloat(xmlEvent.getAttribute("y"));
		}
		return endCoordinates;
	}

}


