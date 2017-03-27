package Freekick;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import master17.Game;

public class findShotVectors {
	public void findShots(Document doc, Game game){
		NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
		ArrayList<shotVector> shots = new ArrayList<shotVector>();

		for (int i=0; i<xmlEventList.getLength();i++){ 
			Element xmlEvent = (Element) xmlEventList.item(i);
			int eventType = Integer.parseInt(xmlEvent.getAttribute("type_id"));

			// event er skudd
			if (eventType == 13 || eventType == 14 || eventType == 15 || eventType == 16 || eventType == 60){//60= chance missed
				shotVector shot = new shotVector(0);
				if (eventType == 16) shot.setGoal(1);
				float x = Float.parseFloat(xmlEvent.getAttribute("x"));
				float y = Float.parseFloat(xmlEvent.getAttribute("y"));

				NodeList qualifierList = xmlEvent.getChildNodes(); //liste over alle qualifiers til eventet
				for(int j=0; j<qualifierList.getLength();j++){
					Element q = (Element) qualifierList.item(j);
					int assist_id = 0;
					boolean intentional_assist = false;
					//check if "event_id=44" har qualifier 154 (intentional assist) and happens right before shot

					int second_assist_id = 0;
					if(qualifierList.item(j).getNodeType() == Node.ELEMENT_NODE){
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
						case 254: shot.followsDribble(1);
						break;



						}
					}
				}
			}


		}


	}
}