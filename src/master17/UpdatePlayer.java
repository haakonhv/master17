package master17;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UpdatePlayer {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException {
		String filename = "data_files/srml-90-2016-squads.xml";
		Document doc = xmlReader.getDocument(filename);
		ArrayList<Player> playerlist = new ArrayList<Player>();
		NodeList playerNodeList = doc.getElementsByTagName("Player"); //liste med players
		
		for (int i=0; i<playerNodeList.getLength(); i++){
			Element p = (Element) playerNodeList.item(i);
			Player player = new Player();
			String uid=p.getAttribute("uID");
			uid = uid.replace("p", "");
			player.setId(Integer.parseInt(uid));
			
			NodeList position = p.getElementsByTagName("Position");
			player.setPosition(position.item(0).getTextContent());
			NodeList stats = p.getElementsByTagName("Stat"); //liste med statsene (subnodene) til hver player
			
			for (int j=0;j<stats.getLength();j++){
				if(stats.item(j).getNodeType()==Node.ELEMENT_NODE){
					Element s = (Element)stats.item(j);
					if (s.getAttribute("Type").equals("weight")){
						try {
							player.setWeight(Integer.parseInt(s.getTextContent()));
						}
						catch (Exception e){
						}
					}
					else if (s.getAttribute("Type").equals("real_position_side")){
						player.setSide(s.getTextContent());
					}
				}
			} 
			playerlist.add(player);
		}
		DatabaseHandler dbhandler = new DatabaseHandler();
		dbhandler.updatePlayers(playerlist);
		
	}

}
