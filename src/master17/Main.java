package master17;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException {
		//File file = new File("data_files/f24-90-2016-839685-eventdetails.xml");
		//OptaDocument opta = new OptaDocument("data_files/f24-90-2016-839685-eventdetails.xml");
		//ArrayList<Event> eventlist = opta.getEventList();
		//DatabaseHandler dbhandler = new DatabaseHandler();
		//dbhandler.insertEvents(eventlist);
		//Game game = new Game(839685,305,197,1,2016);
//		ArrayList<State> stateList = StateBuilder.getStatesFromEvents(game);
//		dbhandler.insertStates(stateList);
		//ArrayList<StateTransition> stateTransList = StateTransitionBuilder.getStateTransitions(game);
		//dbhandler.insertStateTransitions(stateTransList);
		//sendGamesFromDataFiles();
		Qlearning.qLearningAlgorithm();
	}
	public static void sendGamesFromDataFiles() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, SQLException{
		File folder = new File("data_files");
		File[] listOfFiles = folder.listFiles();
		ArrayList<State> stateList = new ArrayList<State>();
		DatabaseHandler dbhandler = new DatabaseHandler();
		ArrayList<StateTransition> stateTransList = new ArrayList<StateTransition>();
		for(int i = 0; i < listOfFiles.length; i++){ 
			long startTime = System.nanoTime();
			System.out.println(listOfFiles[i].toString());
			OptaDocument opta = new OptaDocument(listOfFiles[i].toString());
			ArrayList<Event> eventlist = opta.getEventList();
			dbhandler.insertEvents(eventlist);
			Game game = opta.getGame();	
			stateList = StateBuilder.getStatesFromEvents(game, stateList);
			stateTransList = StateTransitionBuilder.getStateTransitions(game, stateTransList);
			long endTime = System.nanoTime();
			System.out.println("Eventlist, statelist og statetrans oppdatert med fil " + (i+1) + "av " + listOfFiles.length + " Tid= " +(endTime-startTime)/Math.pow(10, 9)+" sekunder") ;
			
		}
		dbhandler.insertStates(stateList);
		System.out.println("StateList inserted");
		dbhandler.insertStateTransitions(stateTransList);
		System.out.println("StateTransitions inserted");
		DatabaseHandler.closeConnection();
	}

}
