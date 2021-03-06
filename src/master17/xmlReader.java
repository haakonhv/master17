package master17;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

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
	public static Hashtable<Integer, Integer> getResults(Document doc){
		Hashtable<Integer, Integer> results = new Hashtable<Integer, Integer>();
		NodeList matches = doc.getElementsByTagName("MatchData");
		for (int i = 0; i < matches.getLength(); i++){
			Element match = (Element) matches.item(i);
			int gameID = Integer.parseInt(match.getAttribute("uID").substring(1));
			NodeList children = match.getChildNodes();
			int homeGoals = 0;
			int awayGoals = 0;
			for (int j=0; j<children.getLength(); j++){
				if (children.item(j).getNodeType() == Node.ELEMENT_NODE){
					String nodeName = children.item(j).getNodeName();
					if (nodeName.equals("MatchInfo")){
						Element matchInfo = (Element) children.item(j);
						if (!matchInfo.hasAttribute("MatchWinner")){ //uavgjort
							results.put(gameID, 0);
							break;
						}
					}
					else if (nodeName.equals("TeamData")){
						Element teamInfo = (Element) children.item(j);
						if (teamInfo.getAttribute("Side").equals("Home")){
							homeGoals = Integer.parseInt(teamInfo.getAttribute("Score"));
						}
						else awayGoals = Integer.parseInt(teamInfo.getAttribute("Score"));
					}
					System.out.println(children.item(j).getNodeName());
				}
				if (homeGoals>awayGoals) results.put(gameID, 1);
				else results.put(gameID, -1);
				
			}

		}
		return results;
	}
	
	public static int getResultsFromEvents(Document doc, Game game){
		ArrayList<StartingEleven> seList = new ArrayList<StartingEleven>();
		NodeList xmlEventList = doc.getElementsByTagName("Event");
		int score = 0;
		for (int i = 0; i<xmlEventList.getLength(); i++){
			Element eventElement = (Element) xmlEventList.item(i);
			if (Integer.parseInt(eventElement.getAttribute("type_id")) == 16){
				boolean ownGoal = false;
				NodeList qualifiers = eventElement.getChildNodes();
				for (int j = 0; j < qualifiers.getLength(); j++){
					if (qualifiers.item(j).getNodeType()==Node.ELEMENT_NODE){
						Element q = (Element) qualifiers.item(j);
						if (Integer.parseInt(q.getAttribute("qualifier_id"))==28) ownGoal = true;
					}
				}
				if (Integer.parseInt(eventElement.getAttribute("team_id")) == game.getHome_team_id()){
					if (ownGoal) score -= 1;
					else score +=1;
				}
				
				else {
					if (ownGoal) score += 1;
					else score-=1; 
				}
			}
		}
		System.out.println(score);
		if (score > 0) return 1;
		else if (score < 0) return -1;
		else return 0;
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
	public static ArrayList<StartingEleven> getStartingEleven(Document doc, Game game){
		ArrayList<StartingEleven> seList = new ArrayList<StartingEleven>();
		NodeList xmlEventList = doc.getElementsByTagName("Event");
		for (int i = 0; i<xmlEventList.getLength(); i++){
			Element eventElement = (Element) xmlEventList.item(i);
			if (Integer.parseInt(eventElement.getAttribute("type_id")) == 34){
				NodeList qualifierList = eventElement.getChildNodes();
				for (int j = 0; j<qualifierList.getLength(); j++){
					if (qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
						Element q = (Element) qualifierList.item(j);
						if (Integer.parseInt(q.getAttribute("qualifier_id"))==30){
							String players = q.getAttribute("value");
							List<String> list = new ArrayList<String>(Arrays.asList(players.split(", ")));
							list = list.subList(0, 11);
							StartingEleven se = new StartingEleven(Integer.parseInt(eventElement.getAttribute("team_id")), game.getGame_id(), list);
							seList.add(se);
						}
					}
				}
			}
		}
		return seList;
	}
//	public static ArrayList<Event> getEventList(Document doc, Game game){ //Ikke i bruk. Bruker buildEventList()
//		int game_id = game.getGame_id();
//		NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
//		ArrayList<Event> eventList = new ArrayList<Event>(); //forel�pig tom liste som skal inneholde alle eventer fra XML-filen
//		int gd = 0; //goal difference
//		int mp = 0; //manpower difference
//		int tempID=game_id*1000;
//		int sequence=1; //sequence nummer internt i en game
//		int number = 0;
//		for (int i=0; i<xmlEventList.getLength();i++){ //l�kke som g�r gjennom hver event-node og lager event-objekter
//			Element xmlEvent = (Element) xmlEventList.item(i);
//			long event_id = Integer.parseInt(xmlEvent.getAttribute("id")); //setter opta-event id;
//			String action_type = getActionType(xmlEvent);
//			if (action_type.equals("skip")){
//				continue;
//			}
//			int team_id = Integer.parseInt(xmlEvent.getAttribute("team_id"));
//			if (action_type.equals("Red card")){
//				if (team_id == game.getHome_team_id()){
//					mp = mp - 1;
//				}
//				else {
//					mp = mp + 1;
//				}
//				continue;
//			}
//
//
//			number = number+1;
//			int period = 0;
//		  	if(Integer.parseInt(xmlEvent.getAttribute("period_id"))==1){ //f�rste omgang
//         		period = 1;
//         	}
//         	else if(Integer.parseInt(xmlEvent.getAttribute("period_id"))==2){ //andre omgang
//         		period = 2;
//         	}
//         	else {
//         		period = 16;//optas pre-match period-kode
//         	}
//		  	int player_id = 0;
//
//
//		  	int minute = Integer.parseInt(xmlEvent.getAttribute("min"));
//		  	int second = Integer.parseInt(xmlEvent.getAttribute("sec"));
//		  	float xstart = Float.parseFloat(xmlEvent.getAttribute("x"));
//         	float ystart = Float.parseFloat(xmlEvent.getAttribute("y"));
//
//         	if (action_type.equals("End of period")){
//         		Event prevEvent = eventList.get(eventList.size()-1);
//         		if (!prevEvent.getAction_type().equals(action_type)){ //ligger to end of period elementer i xml. Trenger bare 1
//         			eventList.add(new Event(event_id, action_type, 1, 0, 0, 50, 50, 50, 50, number, sequence, game_id, period, minute, second, 0, 0));
//         			sequence += 1;
//         			continue;
//         		}
//         		continue;
//			}
//
//
//
//         	try {
//         		player_id = Integer.parseInt(xmlEvent.getAttribute("player_id"));
//         	}
//         	catch (NumberFormatException E){
//         		continue;
//         	}
//         	if (action_type.equals("Goalkeeper control")){
//         		eventList.add(new Event(event_id,action_type,1,team_id,player_id,xstart,ystart,xstart,ystart,number,sequence,game_id,period,minute,second,mp,gd));
//         		sequence +=1;
//         		continue;
//         	}
//
//         	float[] endCoordinates = getEndCoordinates(xmlEvent);
//         	float xend = endCoordinates[0];
//         	float yend = endCoordinates[1];
//         	int outcome = Integer.parseInt(xmlEvent.getAttribute("outcome"));
//         	boolean aerial = action_type.equals("Aerial duel");
//
//         	if (eventList.size() > 0){ //hvis lista ikke er tom m� vi sjekke om ball received eller ball carry har forekommet. M� da sjekke forrige event.
//				Event prevEvent = eventList.get(eventList.size()-1);
//				String prevActionType = prevEvent.getAction_type();
//
//				if (prevEvent.getOutcome() == 1){ //sjekker ball carry fra forrige event til denne eventen
//
//
//					if ((prevActionType.equals("Pass")) || prevActionType.equals("Long pass") || prevActionType.equals("Ball recovery") || prevActionType.equals("Throw in taken") || prevActionType.equals("Cross") || prevActionType.equals("Free kick pass") || prevActionType.equals("Corner taken")){
//						if (!aerial){
//							if (prevEvent.getTeam_id() == team_id){ //hvis false: neste event mest sannsynlig foul committed
//								if (event_id==437012617){
//									System.out.println(getCarryLength(prevEvent, xstart, ystart, team_id) > 7.5);
//								}
//								if (getCarryLength(prevEvent, xstart, ystart, team_id) > 7.5){
//									eventList.add(new Event(tempID+1, "Ball carry", 1, team_id, player_id, prevEvent.getXend(), prevEvent.getYend(), xstart, ystart, number, sequence, game_id, period, prevEvent.getMinute(), prevEvent.getSecond(), mp, gd));
//									number+=1;
//									tempID+=1;
//								}
//							}
//							else { // to forskjellige lag
//								if (action_type.equals("Foul committed")){
//									if (getCarryLength(prevEvent, xstart, ystart, team_id) > 7.5){
//										Element nextEvent = (Element) xmlEventList.item(i+1);
//										try {
//											int nextEventPlayerID = Integer.parseInt(nextEvent.getAttribute("player_id"));
//											eventList.add(new Event(tempID+1, "Ball carry", 1, prevEvent.getTeam_id(), nextEventPlayerID, prevEvent.getXend(), prevEvent.getYend(), 100 - xstart, 100 - ystart, number, sequence, game_id, period, prevEvent.getMinute(), prevEvent.getSecond(), mp, gd));
//											number+=1;
//											tempID+=1;
//							         	}
//							         	catch (NumberFormatException E){
//							         		continue;
//							         	}
//
//									}
//								}
//								else {
//									if (getCarryLength(prevEvent, xstart, ystart, team_id) > 7.5){
//										eventList.add(new Event(tempID+1, "Ball carry", 1, prevEvent.getTeam_id(), prevEvent.getPlayer_id(), prevEvent.getXend(), prevEvent.getYend(), 100 - xstart, 100 - ystart, number, sequence, game_id, period, prevEvent.getMinute(), prevEvent.getSecond(), mp, gd));
//										number+=1;
//										tempID+=1;
//									}
//								}
//							}
//						}
//					}
//         		}
//			}
//
//         	if (!action_type.equals("Goal")){
//         		eventList.add(new Event(event_id, action_type, outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
//         	}
//         	else {
//         		NodeList qualifierList = xmlEvent.getChildNodes();
//         		boolean ownGoal = false;
//         		for (int j = 0; j < qualifierList.getLength(); j++){
//         			if (qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
//         				Element q = (Element) qualifierList.item(j);
//         				int qualifierID = Integer.parseInt(q.getAttribute("qualifier_id"));
//         				if (qualifierID == 28){
//         					ownGoal = true;
//         					eventList.add(new Event(event_id, "Ball touch", 0, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
//         	         		number = number + 1;
//         	         		eventList.add(new Event(tempID+1, action_type, outcome, game.getOtherTeam(team_id), player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
//         	         		sequence += 1;
//         	         		tempID+=1;
//         	         		break;
//         				}
//         			}
//         		}
//         		if (!ownGoal){
//         			eventList.add(new Event(event_id, "Shot", outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
//	         		number = number + 1;
//	         		eventList.add(new Event(tempID+1, action_type, outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, mp, gd));
//	         		sequence += 1;
//	         		tempID+=1;
//         		}
//         		if (team_id == game.getHome_team_id()){
//         			if (ownGoal){
//         				gd = gd - 1;
//         			}
//         			else{
//         				gd = gd + 1;
//         			}
//         		}
//         		else {
//         			if (ownGoal){
//         				gd = gd + 1;
//         			}
//         			else{
//         				gd = gd - 1;
//         			}
//         		}
//         	}
//         	if (action_type.equals("Out of play")){
//         		sequence += 1;
//         	}
//		}
//		return eventList;
//	}

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
			//actiontype = "Dispossessed";
			actiontype = "skip";
			return actiontype;
		}
		else if (typeid == 7){
			actiontype = "Tackle";
			return actiontype;
		}
		else if (typeid == 8 || typeid == 74){
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
				if(Float.parseFloat(xmlEvent.getAttribute("x"))>100){
					actiontype = "Goalkeeper";
				}
				else actiontype = "Out of play";

			}
			else {
				actiontype = "skip";
			}
//			actiontype ="skip";
			return actiontype;
		}

		else if (typeid == 13 || typeid == 14 || typeid == 15){
			NodeList qualifierList = xmlEvent.getChildNodes();
			for(int i=0; i<qualifierList.getLength();i++){
				if(qualifierList.item(i).getNodeType() == Node.ELEMENT_NODE){
					Element q = (Element) qualifierList.item(i);
		    		int qid = Integer.parseInt(q.getAttribute("qualifier_id"));
		    		if (qid == 15){
		    			actiontype = "Headed shot";
		    			return actiontype;
		    		}
				}
			}
			actiontype = "Shot";
			return actiontype;
		}
		else if (typeid == 10){
			NodeList qualifierList = xmlEvent.getChildNodes();
			for(int i=0; i<qualifierList.getLength();i++){
				if(qualifierList.item(i).getNodeType() == Node.ELEMENT_NODE){
					Element q = (Element) qualifierList.item(i);
		    		int qid = Integer.parseInt(q.getAttribute("qualifier_id"));
		    		if (qid == 94){
		    			actiontype = "Blocked shot";
		    			return actiontype;
		    		}
				}
			}
			actiontype = "Shot saved";
			return actiontype;
		}

		else if(typeid == 52){
			actiontype = "Goalkeeper";
			return actiontype;
		}
		else if (typeid == 10){
			NodeList qualifierList = xmlEvent.getChildNodes();
			for(int i=0; i<qualifierList.getLength();i++){
				if(qualifierList.item(i).getNodeType() == Node.ELEMENT_NODE){
					Element q = (Element) qualifierList.item(i);
		    		int qid = Integer.parseInt(q.getAttribute("qualifier_id"));
		    		if (qid == 176 || qid== 177 || qid == 92 || qid == 93){
		    			actiontype = "Goalkeeper";
		    			return actiontype;
		    		}
				}
			}
			actiontype = "skip";
			return actiontype;
		}
		else if (typeid == 11){
			if (Integer.parseInt(xmlEvent.getAttribute("outcome"))==1){
				actiontype = "Goalkeeper";
				return actiontype;
			}
			else{
				actiontype = "skip";
				return actiontype;
			}
		}
		else if (typeid == 16){
			actiontype = "Goal";
			return actiontype;
		}
		else if (typeid == 30){
			actiontype = "End of period";
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
		NodeList qualifierList = xmlEvent.getChildNodes();
		for(int i=0; i<qualifierList.getLength();i++){
			if(qualifierList.item(i).getNodeType() == Node.ELEMENT_NODE){
				Element q = (Element) qualifierList.item(i);
	    		int qid = Integer.parseInt(q.getAttribute("qualifier_id"));
	    		if (qid == 140){
	    			xEnd = Float.parseFloat(q.getAttribute("value"));
	    			xUpdated = true;
	    		}
	    		else if (qid == 141){
	    			yEnd = Float.parseFloat(q.getAttribute("value"));
	    			yUpdated = true;
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

	private static double getCarryLength(Event prevEvent, float currentXstart, float currentYstart, int currentTeamID){
		double carryLength = 0;
		if (prevEvent.getTeam_id() == currentTeamID) {
			double xdiff = 1.05*Math.abs(currentXstart - prevEvent.getXend());
			double ydiff = 0.68*Math.abs(currentYstart - prevEvent.getYend());
			carryLength = Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2));
		}
		else {
			if (!(prevEvent.getXend()<0 || prevEvent.getXend()>100 || prevEvent.getYend()<0 || prevEvent.getYend()>100)){
				double xdiff = 1.05*Math.abs(currentXstart - (100 - prevEvent.getXend()));
				double ydiff = 0.68*Math.abs(currentYstart - (100 - prevEvent.getYend()));
				carryLength = Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2));
			}
		}
		return carryLength;
	}

	public static ArrayList<Event> buildEventList(Document doc, Game game){
		ArrayList<Event> eventList = new ArrayList<Event>();
		int game_id = game.getGame_id();
		NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen

		int gd = 0; //goal difference
		int tempID=game_id*1000;
		int sequence=1; //sequence nummer internt i en game
		int number = 0;
		for (int i=0; i<xmlEventList.getLength();i++){ //l�kke som g�r gjennom hver event-node og lager event-objekter
			Element xmlEvent = (Element) xmlEventList.item(i);
			long event_id = Integer.parseInt(xmlEvent.getAttribute("id")); //setter opta-event id;
			String action_type = getActionType(xmlEvent);
			if (action_type.equals("skip")){
				continue;
			}
			int team_id = Integer.parseInt(xmlEvent.getAttribute("team_id"));

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
		  	int player_id = 0;

		  	int minute = Integer.parseInt(xmlEvent.getAttribute("min"));
		  	int second = Integer.parseInt(xmlEvent.getAttribute("sec"));
		  	float xstart = Float.parseFloat(xmlEvent.getAttribute("x"));
         	float ystart = Float.parseFloat(xmlEvent.getAttribute("y"));

         	if (action_type.equals("End of period")){
         		Event prevEvent = eventList.get(eventList.size()-1);
         		if (!prevEvent.getAction_type().equals(action_type)){ //ligger to end of period elementer i xml. Trenger bare 1
         			eventList.add(new Event(event_id, action_type, 1, 0, 0, 50, 50, 50, 50, number, sequence, game_id, period, minute, second, 0));
         			sequence += 1;
         			continue;
         		}
         		continue;
			}


         	try {
         		player_id = Integer.parseInt(xmlEvent.getAttribute("player_id"));
         	}
         	catch (NumberFormatException E){
         		continue;
         	}
         	if (action_type.equals("Goalkeeper")){
         		eventList.add(new Event(event_id,action_type,1,team_id,player_id,xstart,ystart,xstart,ystart,number,sequence,game_id,period,minute,second,gd));
         		sequence +=1;
         		continue;
         	}


         	float[] endCoordinates = getEndCoordinates(xmlEvent);
         	float xend = endCoordinates[0];
         	float yend = endCoordinates[1];
         	int outcome = Integer.parseInt(xmlEvent.getAttribute("outcome"));


         	if (action_type.equals("Aerial duel") && outcome==1){ //laget som vinner hodeduellen kommer alltid f�rst i eventlisten
         		Event prevEvent = eventList.get(eventList.size()-1);
         		if (prevEvent.getAction_type()=="Aerial duel"){
         			eventList.remove(eventList.get(eventList.size()-1));
         			eventList.add(new Event(event_id, action_type, outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, gd));
         			eventList.add(prevEvent);
         			continue;
         		}
         	}

         	if (!action_type.equals("Goal")){
         		eventList.add(new Event(event_id, action_type, outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, gd));
         	}
         	else {
         		NodeList qualifierList = xmlEvent.getChildNodes();
         		boolean ownGoal = false;
         		for (int j = 0; j < qualifierList.getLength(); j++){
         			if (qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
         				Element q = (Element) qualifierList.item(j);
         				int qualifierID = Integer.parseInt(q.getAttribute("qualifier_id"));
         				if (qualifierID == 28){
         					ownGoal = true;
         					eventList.add(new Event(event_id, "Ball touch", 0, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, gd));
         	         		number = number + 1;
         	         		eventList.add(new Event(tempID+1, action_type, outcome, game.getOtherTeam(team_id), player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, gd));
         	         		sequence += 1;
         	         		tempID+=1;
         	         		break;
         				}
         			}
         		}
         		if (!ownGoal){
         			boolean header = false;
        			for(int j=0; j<qualifierList.getLength();j++){
        				if(qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
        					Element q = (Element) qualifierList.item(j);
        		    		int qid = Integer.parseInt(q.getAttribute("qualifier_id"));
        		    		if (qid == 15){
        		    			eventList.add(new Event(event_id, "Headed shot", outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, gd));
        		         		number = number + 1;
        		         		header = true;
        		    		}
        				}
        			}
         			if (!header){
         				eventList.add(new Event(event_id, "Shot", outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, gd));
         				number = number + 1;
         			}
	         		eventList.add(new Event(tempID+1, action_type, outcome, team_id, player_id, xstart, ystart, xend, yend, number, sequence, game_id, period, minute, second, gd));
	         		sequence += 1;
	         		tempID+=1;
         		}
         		if (team_id == game.getHome_team_id()){
         			if (ownGoal){
         				gd = gd - 1;
         			}
         			else{
         				gd = gd + 1;
         			}
         		}
         		else {
         			if (ownGoal){
         				gd = gd + 1;
         			}
         			else{
         				gd = gd - 1;
         			}
         		}
         	}
//         	if (action_type.equals("Out of play")){
//         		sequence += 1;
//         	}
		}
		ArrayList<Event> completeEventList = new ArrayList<Event>();
		completeEventList.add(eventList.get(0));
		System.out.println(eventList.size());
		for (int i = 1; i < eventList.size(); i++){ //g�r gjennom eventlisten for � legge til "kunstige" events (ball carry, ball received osv.)
			Event prevEvent = completeEventList.get(completeEventList.size()-1);
			//System.out.println(prevEvent);
			Event thisEvent = eventList.get(i);


			//System.out.println(thisEvent);
			if (prevEvent.getOutcome() == 1){

				if (prevEvent.getAction_type().equals("Pass") || prevEvent.getAction_type().equals("Long pass") || prevEvent.getAction_type().equals("Throw in taken")
					|| prevEvent.getAction_type().equals("Cross") || prevEvent.getAction_type().equals("Free kick pass") || prevEvent.getAction_type().equals("Corner taken")){
					if (prevEvent.getTeam_id()==thisEvent.getTeam_id()){
						completeEventList.add(new Event(9999, "Ball received", 1, prevEvent.getTeam_id(), thisEvent.getPlayer_id(), prevEvent.getXend(),
						prevEvent.getYend(), prevEvent.getXend(), prevEvent.getYend(), completeEventList.get(completeEventList.size()-1).getNumber()+1, thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), thisEvent.getMinute(),
						thisEvent.getSecond(), thisEvent.getGoaldifference())); // legger til "Ball received" event etter pasninger som kom frem
						prevEvent = completeEventList.get(completeEventList.size()-1);
					}
					else{ //lag er ulike
						if (thisEvent.getAction_type().equals("Foul committed")){
							Event nextEvent = eventList.get(i+1);
							if (thisEvent.getEvent_id()==696609825){
								System.out.println("Event");
							}
							try {
								int nextEventPlayerID = nextEvent.getPlayer_id();
								completeEventList.add(new Event(9999, "Ball received", 1, prevEvent.getTeam_id(), nextEventPlayerID, prevEvent.getXend(), prevEvent.getYend(),
										100 - thisEvent.getXstart(), 100 - thisEvent.getYstart(), completeEventList.get(completeEventList.size()-1).getNumber()+1,
										thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
								prevEvent = completeEventList.get(completeEventList.size()-1);
							//	System.out.println("Ball carry hvor foul committed");
				         	}
				         	catch (NumberFormatException E){
				         		continue;
				         	}
						}
					}
				}

				if (prevEvent.getAction_type().equals("Pass") || prevEvent.getAction_type().equals("Ball received") || prevEvent.getAction_type().equals("Long pass") || prevEvent.getAction_type().equals("Ball recovery") ||
						prevEvent.getAction_type().equals("Throw in taken") || prevEvent.getAction_type().equals("Cross") || prevEvent.getAction_type().equals("Free kick pass") ||
						prevEvent.getAction_type().equals("Corner taken")){

					if (!thisEvent.getAction_type().equals("Aerial duel")){
						if (prevEvent.getTeam_id() == thisEvent.getTeam_id()){ //hvis false: neste event mest sannsynlig foul committed

							if (getCarryLength(prevEvent, thisEvent.getXstart(), thisEvent.getYstart(), thisEvent.getTeam_id()) > 7.5){
								//System.out.println("Ball carry true hvor prevteam == currTeam");
								completeEventList.add(new Event(9999, "Ball carry", 1, thisEvent.getTeam_id(), thisEvent.getPlayer_id(), prevEvent.getXend(),
								prevEvent.getYend(), thisEvent.getXstart(), thisEvent.getYstart(), completeEventList.get(completeEventList.size()-1).getNumber()+1,
								thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
								prevEvent = completeEventList.get(completeEventList.size()-1);
							}
						}
						else { // to forskjellige lag
							if (thisEvent.getAction_type().equals("Foul committed")){
								Event nextEvent = eventList.get(i+1);
								if (getCarryLength(prevEvent, nextEvent.getXstart(), nextEvent.getYstart(), nextEvent.getTeam_id()) > 7.5){

									try {
										int nextEventPlayerID = nextEvent.getPlayer_id();
										completeEventList.add(new Event(9999, "Ball carry", 1, prevEvent.getTeam_id(), nextEventPlayerID, prevEvent.getXend(),
										prevEvent.getYend(), nextEvent.getXstart(), nextEvent.getYstart(), completeEventList.get(completeEventList.size()-1).getNumber()+1,
										thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
										prevEvent = completeEventList.get(completeEventList.size()-1);
									//	System.out.println("Ball carry hvor foul committed");
						         	}
						         	catch (NumberFormatException E){
						         		continue;
						         	}
								}
							}
							else {
								if (getCarryLength(prevEvent, thisEvent.getXstart(), thisEvent.getYstart(), thisEvent.getTeam_id()) > 7.5){
									completeEventList.add(new Event(9999, "Ball carry", 1, prevEvent.getTeam_id(), prevEvent.getPlayer_id(), prevEvent.getXend(), prevEvent.getYend(),
									100 - thisEvent.getXstart(), 100 - thisEvent.getYstart(), completeEventList.get(completeEventList.size()-1).getNumber()+1, thisEvent.getSequence(),
									thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
									prevEvent = completeEventList.get(completeEventList.size()-1);
									//System.out.println("Ball carry hvor team er ulike");
								}
							}
						}
					}
				}
			}
			thisEvent.setNumber(completeEventList.get(completeEventList.size()-1).getNumber()+1);
			completeEventList.add(thisEvent);
		}



		return completeEventList;
	}


}


