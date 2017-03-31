package Freekick;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import master17.Game;
import master17.xmlReader;

public class findShotVectors {
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException {
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < 1; i++){
			long startTime = System.nanoTime();
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Game game = xmlReader.getGame(doc);
			ArrayList<shotVector> shotList = findShots(doc, game);
			for (shotVector s:shotList){
				System.out.println(s);
			}
			long endTime = System.nanoTime();
		}
	}
	
	
	
	
	
	public static ArrayList<shotVector> findShots(Document doc, Game game){
		NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
		ArrayList<shotVector> shots = new ArrayList<shotVector>();

		for (int i=0; i<xmlEventList.getLength();i++){ 
			Element xmlEvent = (Element) xmlEventList.item(i);
			int eventType = Integer.parseInt(xmlEvent.getAttribute("type_id"));

			// event er skudd
			if (eventType == 13 || eventType == 14 || eventType == 15 || eventType == 16 || eventType == 60){//60= chance missed
				shotVector shot = new shotVector(0);
				if (eventType == 16) shot.setGoal(1);
				double x = 1.05*Float.parseFloat(xmlEvent.getAttribute("x"));
				double y = 0.68*Float.parseFloat(xmlEvent.getAttribute("y"));
				double distance = Math.sqrt(Math.pow(x-105.0, 2)+Math.pow(y-35.0, 2));
				shot.setDistance(distance);
				double angle = getAngle(x, y);
				shot.setAngle(angle);
				boolean intentional_assist = false;
				boolean secondAssisted = false;
				int assist_id = 0;
				NodeList qualifierList = xmlEvent.getChildNodes(); //liste over alle qualifiers til eventet
				for(int j=0; j<qualifierList.getLength();j++){
					
					//check if "event_id=44" har qualifier 154 (intentional assist) and happens right before shot

					int second_assist_id = 0;
					if(qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
						Element q = (Element) qualifierList.item(j);
						int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));    	
						switch (qualifier_id) {
						case 55: assist_id = Integer.parseInt(q.getAttribute("value"));
						break;
						case 154: intentional_assist = true;
						break;
						case 26: shot.setDirektFK(1);
						break;
						case 25: shot.setCorner(1); 
						break;
						case 9: shot.setPenalty(1);
						break;
						case 108: shot.setVolley(1);
						break;
						case 15: shot.setHeader(1);
						break;
						case 23: shot.setFastBreak(1);
						break;
						case 254: shot.setFollowsDribble(1);
						break;
						case 217: secondAssisted = true;
						break;
						}
					}
					
				}
				if (intentional_assist){

					Element potentialAssist = (Element) xmlEventList.item(i-1);
					if (Integer.parseInt(potentialAssist.getAttribute("event_id"))==assist_id){
						NodeList assistQualifiers = potentialAssist.getChildNodes();
						for(int j=0; j<assistQualifiers.getLength();j++){
							if (assistQualifiers.item(j).getNodeType()==Node.ELEMENT_NODE){
								Element q = (Element) assistQualifiers.item(j);
								int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));
								switch (qualifier_id) {
								case 2: shot.setCross(1);
								break;
								case 195: shot.setPullBack(1);
								break;
								case 4: shot.setThroughBall(1);
								}
							}
						}
					}
				}
				if (secondAssisted){
					boolean wasAssist = false;
					boolean wasThroughBall = false;
					Element secondAssist = (Element) xmlEventList.item(i-2);
					NodeList assistQualifiers = secondAssist.getChildNodes();
					for(int j=0; j<assistQualifiers.getLength();j++){
						if (assistQualifiers.item(j).getNodeType()==Node.ELEMENT_NODE){
							Element q = (Element) assistQualifiers.item(j);
							int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));
							switch (qualifier_id){
							case 218: wasAssist = true;
							break;
							case 4: wasThroughBall = true;
							}
							
						}
					}
					if (wasAssist && wasThroughBall){
						shot.setSecondThrough(1);
					}
				}
				shots.add(shot);
			}		
		}
		return shots;
	}
	private static double getAngle(double x, double y){
		double goalLength = 37.4-30.26;

		double distPost1 = Math.sqrt(Math.pow(105-x, 2)+Math.pow(30.26-y, 2));
		double distPost2 = Math.sqrt(Math.pow(105-x, 2)+Math.pow(37.4-y, 2));
		double angle = Math.acos((Math.pow(goalLength,2)-Math.pow(distPost1, 2)-Math.pow(distPost2, 2))/(-2.0*distPost1*distPost2));
		return angle;

	}
	
	
}