package master17;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class OptaDocument {
	private String fileName;
	private Document doc;
	private Game game;
	public String getFileName() {
		return fileName;
	}

	public Document getDoc() {
		return doc;
	}

	public Game getGame() {
		return game;
	}

	private ArrayList<Event> EventList;

	public OptaDocument(String fileName) throws ParserConfigurationException, SAXException, IOException {
		super();
		this.fileName = fileName;
		this.doc = xmlReader.getDocument(fileName);
		this.game = xmlReader.getGame(doc);
		this.EventList = xmlReader.getEventList(doc, game);

	}

	public ArrayList<Event> getEventList() {
		return EventList;
	}



}
