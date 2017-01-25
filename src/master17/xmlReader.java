package master17;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

}
