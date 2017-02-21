package master17;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException {
		//File file = new File("data_files/f24-90-2016-839685-eventdetails.xml");
		//Game game = new Game(839685,305,197,1,2016);
//		ArrayList<State> stateList = StateBuilder.getStatesFromEvents(game);
//		dbhandler.insertStates(stateList);
		//ArrayList<StateTransition> stateTransList = StateTransitionBuilder.getStateTransitions(game);
		//dbhandler.insertStateTransitions(stateTransList);
		//sendGamesFromDataFiles();
		//Qlearning.qLearningAlgorithm();
		//insertGames();
//		FindPlayerValues.findValues();
		sendEventsFromDataFiles();
//		StateBuilder.getStatesFromEvents();
//		StateTransitionBuilder.setStateTransitions();
//		ReinforcementLearning.learningAlgorithm();
//		GameTimeReader.setPlayerGameTime();

	}
	public static void sendGamesFromDataFiles() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		ArrayList<Game> games = new ArrayList<Game>();
	
		for(int i = 0; i < listOfFiles.length; i++){
			long startTime = System.nanoTime();
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Game game = xmlReader.getGame(doc);
			long endTime = System.nanoTime();
			System.out.println("Game " + (i+1) + " av " + listOfFiles.length + " Tid= " +(endTime-startTime)/Math.pow(10, 9)+" sekunder");
			games.add(game);
		}
		DatabaseHandler.insertGames(games);

		DatabaseHandler.closeConnection();
	}

	public static void sendEventsFromDataFiles() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		DatabaseHandler dbhandler = new DatabaseHandler();
		for(int i = 0; i < listOfFiles.length; i++){
			long startTime = System.nanoTime();
			System.out.println(listOfFiles[i].toString());
			OptaDocument opta = new OptaDocument(listOfFiles[i].toString());
			ArrayList<Event> eventlist = opta.getEventList();
			dbhandler.insertEvents(eventlist);
			long endTime = System.nanoTime();
			System.out.println("Eventlist " + (i+1) + " av " + listOfFiles.length + " Tid= " +(endTime-startTime)/Math.pow(10, 9)+" sekunder");
		}
	}
}
