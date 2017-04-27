package master17;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

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
//		sendGamesFromDataFiles();
		//Qlearning.qLearningAlgorithm();
		//insertGames();
//		FindPlayerValues.findValues();
//		sendEventsFromDataFiles();
//		StateBuilder.getStatesFromEvents();
//		StateTransitionBuilder.setStateTransitions();
//		ReinforcementLearning.learningAlgorithm();
//		markov2.Builder.buildFromEvents();
//		GameTimeReader.setPlayerGameTime();
//		markov2.Builder.setStateAction();
//		markov2.Reinforcement.learningAlgorithm();
//		markov2.Builder.buildStateAction();
//		markov2.Reinforcement.learning();
//		markov2.PlayerValueBuilder.buildPlayerValues();
//		sendStartingElevenFromDataFiles();
//		sendMatchResults();
		Validation.gameAverageImpact(Validation.playerImpactPerGroup(Validation.createGroups()));
	}
	
	public static void sendMatchResults() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		Hashtable<Integer, Integer> results = new Hashtable<Integer, Integer>();
		for(int i = 0; i < listOfFiles.length; i++){
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Game game = xmlReader.getGame(doc);
			int gameID = game.getGame_id();
			int result = xmlReader.getResultsFromEvents(doc, game);
			results.put(gameID, result);
			System.out.println(gameID + " " + result);
//			Document doc = xmlReader.getDocument(listOfFiles[i].toString()); // bruk hvis man leser resultfiler
//			results = xmlReader.getResults(doc);
//			
		}
		DatabaseHandler.updateMatchResults(results);
	}
	
	
	public static void sendStartingElevenFromDataFiles() throws ParserConfigurationException, SAXException, IOException, SQLException, ClassNotFoundException {
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		ArrayList<StartingEleven> seList = new ArrayList<StartingEleven>();
		
		for(int i = 0; i < listOfFiles.length; i++){
			long startTime = System.nanoTime();
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Game game = xmlReader.getGame(doc);
			long endTime = System.nanoTime();
			System.out.println("Game " + (i+1) + " av " + listOfFiles.length + " Tid= " +(endTime-startTime)/Math.pow(10, 9)+" sekunder");
			ArrayList<StartingEleven> temp = xmlReader.getStartingEleven(doc, game);
			for (StartingEleven se: temp){
				seList.add(se);
			}
		}
		DatabaseHandler.insertStartingEleven(seList);
		
	}
	public static void sendGamesFromDataFiles() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		ArrayList<Game> games = new ArrayList<Game>();

		for(int i = 1; i < listOfFiles.length; i++){
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
		for(int i = 1; i < listOfFiles.length; i++){
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
