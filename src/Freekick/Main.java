package Freekick;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import master17.DatabaseHandler;
import master17.Game;
import master17.xmlReader;

public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException {
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		ArrayList<FreeKick> longFKlist = new ArrayList<FreeKick>();
		for(int i = 0; i < listOfFiles.length; i++){
			long startTime = System.nanoTime();
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Game game = xmlReader.getGame(doc);
			ArrayList<FreeKick> fklist = findFKs.getFreeKickList(doc, game);
			long endTime = System.nanoTime();
			longFKlist.addAll(fklist);
			System.out.println("Game " + (i+1) + " av " + listOfFiles.length + " Tid= " +(endTime-startTime)/Math.pow(10, 9)+" sekunder");
		}
		DatabaseHandler.insertFreeKicks(longFKlist);
	}
}
