package master17;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		//File file = new File("data_files/f24-90-2016-839685-eventdetails.xml");
		OptaDocument opta = new OptaDocument("data_files/f24-90-2016-839685-eventdetails.xml");
		System.out.println("Ferdig");
		ArrayList<Event> eventlist = opta.getEventList();
		for (Event e:eventlist){
			System.out.println(e);
		}
	}
	
}
