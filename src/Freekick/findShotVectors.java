package Freekick;
import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.opencsv.CSVWriter;

import master17.Game;
import master17.xmlReader;

public class findShotVectors {
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException {
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> crossedShot = new ArrayList<String>();
		ArrayList<String> crossedHeader = new ArrayList<String>();
		ArrayList<String> regularShot = new ArrayList<String>();
		ArrayList<String> header = new ArrayList<String>();
		ArrayList<String> freeKick = new ArrayList<String>();
		ArrayList<String> penalty = new ArrayList<String>();
		ArrayList<String> all = new ArrayList<String>();
		
		
		for(int i = 0; i < 480; i++){
			long startTime = System.nanoTime();
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Game game = xmlReader.getGame(doc);
			ArrayList<shotVector> shotList = findShots(doc, game);
			for (shotVector s:shotList){
				if (s.getPenalty()==0 && s.getDirektFK()==0) all.add(s.toString());
				if(s.getPenalty()==1){
					penalty.add(s.toString());
				}
				else if (s.getDirectFK()==1){
					freeKick.add(s.toString());
				}
				
				else if (s.getHeader()==1){
					if (s.getCross()==1) crossedHeader.add(s.toString());
					else header.add(s.toString());
				}
				else if (s.getCross()==1) crossedShot.add(s.toString());
				else regularShot.add(s.toString());
				
					
			}
			long endTime = System.nanoTime();
		}
//		FileWriter penWriter = new FileWriter("C:/Users/Håkon/Documents/Eliteserien/penalty20142015.txt");
//		for (String s: penalty){
//			penWriter.write(s);
//		}
//		penWriter.close();
//		FileWriter fkWriter = new FileWriter("C:/Users/Håkon/Documents/Eliteserien/freeKick20142015.txt");
//		for (String s: freeKick){
//			fkWriter.write(s);
//		}
//		fkWriter.close();
//		FileWriter crossShotW = new FileWriter("C:/Users/Håkon/Documents/Eliteserien/crossedShot20142015.txt");
//		for (String s: crossedShot){
//			crossShotW.write(s);
//		}
//		crossShotW.close();
//		FileWriter headCrossW = new FileWriter("C:/Users/Håkon/Documents/Eliteserien/crossedHeader20142015.txt");
//		for (String s: crossedHeader){
//			headCrossW.write(s);
//		}
//		headCrossW.close();
//		FileWriter headerW = new FileWriter("C:/Users/Håkon/Documents/Eliteserien/header20142015.txt");
//		for (String s: header){
//			headerW.write(s);
//		}
//		headerW.close();
//		FileWriter regShotW = new FileWriter("C:/Users/Håkon/Documents/Eliteserien/regularShot20142015.txt");
//		for (String s: regularShot){
//			regShotW.write(s);
//		}
//		regShotW.close();
		FileWriter allW = new FileWriter("C:/Users/Håkon/Documents/Eliteserien/all20142015.txt");
		for (String s: all){
			allW.write(s);
		}
		allW.close();
	}
	
	
	
	
	
	public static void rateShots(Document doc, Game game){
		NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
		ArrayList<shotVector> shots = new ArrayList<shotVector>();
		ArrayList<ExpectedGoal> xg = new ArrayList<ExpectedGoal>();

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
				double inverseDist = 1/distance;
				shot.setDistance(distance);
				shot.setInverseDistance(inverseDist);
				double angle = getAngle(x, y);
				double invAngle = 1/angle;
				shot.setInverseAngle(invAngle);
				shot.setAngle(angle);
				double angleDist = angle*distance;
				shot.setDistanceAngle(angleDist);
				boolean intentional_assist = false;
				int assist_id = 0;
				boolean ownGoal = false;
				NodeList qualifierList = xmlEvent.getChildNodes(); //liste over alle qualifiers til eventet
				for(int j=0; j<qualifierList.getLength();j++){
					
					//check if "event_id=44" har qualifier 154 (intentional assist) and happens right before shot

					if(qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
						Element q = (Element) qualifierList.item(j);
						int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));    	
						switch (qualifier_id) {
						case 55: assist_id = Integer.parseInt(q.getAttribute("value"));
						break;
						case 154: intentional_assist = true;
						shot.setIntentional_assist(1);
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
						case 28: ownGoal = true;
						break;
						}
					}
					
				}
				if (ownGoal) continue;
				if (intentional_assist){
					for (int j=i-1; j>i-5; j--){
						Element potentialAssist = (Element) xmlEventList.item(j);

						if (Integer.parseInt(potentialAssist.getAttribute("event_id"))==assist_id){
							NodeList assistQualifiers = potentialAssist.getChildNodes();
							for(int k=0; k<assistQualifiers.getLength();k++){
								if (assistQualifiers.item(k).getNodeType()==Node.ELEMENT_NODE){
									Element q = (Element) assistQualifiers.item(k);
									int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));
									switch (qualifier_id) {
									case 2: shot.setCross(1);
									break;
									case 195: shot.setPullBack(1);
									break;
									case 4: shot.setThroughBall(1);
									break;
									case 155: shot.setCross(1);//chip
									break;
									
									}
								}
							}
							break;
						}
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
				double inverseDist = 1/distance;
				shot.setDistance(distance);
				shot.setInverseDistance(inverseDist);
				double angle = getAngle(x, y);
				double invAngle = 1/angle;
				shot.setInverseAngle(invAngle);
				shot.setAngle(angle);
				double angleDist = angle*distance;
				shot.setDistanceAngle(angleDist);
				boolean intentional_assist = false;
				int assist_id = 0;
				boolean ownGoal = false;
				NodeList qualifierList = xmlEvent.getChildNodes(); //liste over alle qualifiers til eventet
				for(int j=0; j<qualifierList.getLength();j++){
					
					//check if "event_id=44" har qualifier 154 (intentional assist) and happens right before shot

					if(qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
						Element q = (Element) qualifierList.item(j);
						int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));    	
						switch (qualifier_id) {
						case 55: assist_id = Integer.parseInt(q.getAttribute("value"));
						break;
						case 154: intentional_assist = true;
						shot.setIntentional_assist(1);
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
						case 28: ownGoal = true;
						break;
						}
					}
					
				}
				if (ownGoal) continue;
				if (intentional_assist){
					for (int j=i-1; j>i-5; j--){
						Element potentialAssist = (Element) xmlEventList.item(j);

						if (Integer.parseInt(potentialAssist.getAttribute("event_id"))==assist_id){
							NodeList assistQualifiers = potentialAssist.getChildNodes();
							for(int k=0; k<assistQualifiers.getLength();k++){
								if (assistQualifiers.item(k).getNodeType()==Node.ELEMENT_NODE){
									Element q = (Element) assistQualifiers.item(k);
									int qualifier_id = Integer.parseInt(q.getAttribute("qualifier_id"));
									switch (qualifier_id) {
									case 2: shot.setCross(1);
									break;
									case 195: shot.setPullBack(1);
									break;
									case 4: shot.setThroughBall(1);
									break;
									case 155: shot.setCross(1);//chip
									break;
									}
								}
							}
							break;
						}
					}
				}

				shots.add(shot);
			}		
		}
		return shots;
	}
	
	
}