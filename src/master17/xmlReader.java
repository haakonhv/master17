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

import project16.Event;

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
		NodeList xmlEventList = doc.getElementsByTagName("Event");
		ArrayList<Event> eventList = new ArrayList<Event>();
		int GD = 0;
		int sequence=1; //sequence nummer internt i en game
		for (int i=0; i<xmlEventList.getLength();i++){
			Element xmlEvent = (Element) xmlEventList.item(i);
			int event_id = Integer.parseInt(xmlEvent.getAttribute("id")); //setter opta-event id;
			int number = i+1;
		  	if(Integer.parseInt(xmlEvent.getAttribute("period_id"))==1){
         		int period = 1;
         	}
         	else if(Integer.parseInt(xmlEvent.getAttribute("period_id"))==2){
         		int period = 2;
         	}
         	else {
         		int period = 16;//optas pre-match period-kode
         	}
		  	int minute = Integer.parseInt(xmlEvent.getAttribute("min"));
		  	int second = Integer.parseInt(xmlEvent.getAttribute("sec"));
		  	float xstart = Float.parseFloat(xmlEvent.getAttribute("x"));
         	float ystart = Float.parseFloat(xmlEvent.getAttribute("y"));
         	//int typeid = Integer.parseInt(xmlEvent.getAttribute("type_id"));
         	
		}
	}
	
	private String getEventType(Element xmlEvent){
		int typeid = Integer.parseInt(xmlEvent.getAttribute("type_id"));
		if (typeid == 1){
			NodeList qualifierList = xmlEvent.getChildNodes();
			event_type="Pass";
		}
		
		return event_type;
	}

}
