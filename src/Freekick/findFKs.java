package Freekick;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import master17.Game;

public class findFKs {
	public static ArrayList<FreeKick> getFreeKickList(Document doc, Game game){
		NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
		ArrayList<FreeKick> fkList = new ArrayList<FreeKick>();
		for (int i=0; i<xmlEventList.getLength();i++){ 
			Element xmlEvent = (Element) xmlEventList.item(i);
			float xstart = Float.parseFloat(xmlEvent.getAttribute("x"));
         	float ystart = Float.parseFloat(xmlEvent.getAttribute("y"));
         	if ((75.0 < xstart && xstart < 85.0) && (79 < ystart || ystart < 21 )) { //sjekker om event er i korridor
         		int typeid = Integer.parseInt(xmlEvent.getAttribute("type_id"));
         		boolean freeKick = false;
         		boolean cross = false;
         		boolean rightFoot = false;
         		float xend = 0;
         		float yend = 0;
         		if (typeid == 1){ // sjekker om event er pasning
         			NodeList qualifierList = xmlEvent.getChildNodes(); //liste over alle qualifiers til eventet
        			for(int j=0; j<qualifierList.getLength();j++){
        				if(qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
        					Element q = (Element) qualifierList.item(j);
        					int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));
        					switch (qualifier_id){
        					case 5: freeKick = true;
        						break;
        					case 20: rightFoot = true;
        						break;
        					case 2: cross = true;
        						break;
        					case 140: xend = Float.parseFloat(q.getAttribute("value"));
        		    			break;
        					case 141: yend = Float.parseFloat(q.getAttribute("value"));
        		    			break;   
        					}
        				}
        			}
         		}
         		if (freeKick && cross) { //sjekker om event var friskpark slått inn i boks
         			int inswing = 0;
         			if (rightFoot){
         				if (ystart > 79){
         					inswing = 1;
         				}
         			}
         			else {//if leftFoot
         				if (ystart < 21){
         					inswing = 1;
         				}
         			}
         			long optaID = Integer.parseInt(xmlEvent.getAttribute("id"));;
         			int playerID = Integer.parseInt(xmlEvent.getAttribute("player_id"));
         			int teamID = Integer.parseInt(xmlEvent.getAttribute("team_id"));       			
         			int gameID = game.getGame_id();
         			String outcome = getShotAndGoal(xmlEventList,i);
         			switch (outcome) {
         			case "nothing":
         				fkList.add(new FreeKick(optaID, gameID, teamID, playerID, inswing, xstart, ystart, xend, yend, 0, 0));
         				break;
         			case "shot":
         				fkList.add(new FreeKick(optaID, gameID, teamID, playerID, inswing, xstart, ystart, xend, yend, 0, 1));
         				break;
         			
         			case "goal":
         				fkList.add(new FreeKick(optaID, gameID, teamID, playerID, inswing, xstart, ystart, xend, yend, 1, 1));
         				break;
         			}	
         		}
         		
         	}
		}
		for (int k=0;k<fkList.size(); k++){
			System.out.println(fkList.get(k));
		}
		return fkList;
	}

	private static String getShotAndGoal(NodeList xmlEventList, int i) {
		boolean shot = false;
		boolean goal = false;
		String returnString = "nothing";
		Element startEvent = (Element) xmlEventList.item(i);
		int teamID = Integer.parseInt(startEvent.getAttribute("team_id"));
		for (int j = i+1; j<xmlEventList.getLength(); j++){
			Element event = (Element) xmlEventList.item(j);
			int typeid = Integer.parseInt(event.getAttribute("type_id"));
			if (typeid == 5 || typeid == 6 || typeid == 2 || typeid == 4 || typeid == 30 || typeid == 27 ){
				System.out.println(j-i);
				return returnString;
			}
			else if ((Float.parseFloat(event.getAttribute("x")) < 83) && (Integer.parseInt(event.getAttribute("team_id")))==teamID){
				System.out.println(j-i);
				return returnString;
			}
			else if ((Float.parseFloat(event.getAttribute("x")) > 17) && (Integer.parseInt(event.getAttribute("team_id")))!=teamID){
				return returnString;
			}
			else if (typeid == 16){
				returnString = "goal";
				System.out.println(j-i);
				return returnString;
			}
			else if (typeid == 13 || typeid == 14 || typeid == 15){
				returnString = "shot";
			}
		}
		return returnString;
	}
}
