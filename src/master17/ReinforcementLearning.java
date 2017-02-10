package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

public class ReinforcementLearning {
	public static void learningAlgorithm() throws ClassNotFoundException, SQLException{
		double lastValue = 0;
		double currentValue = 0;
		boolean converged = false;
		int maxIterations = 1000;
		double convergeCriterion = 0.01;

		ResultSet stateSet = DatabaseHandler.getDatabaseStates();
		ResultSet stateTransSet = DatabaseHandler.getDatabaseStateTrans();
		//ArrayList<State> stateList = new ArrayList<State>();
		Hashtable<Integer, State> stateList= new Hashtable<Integer, State>();
		Hashtable<Integer, Double> tempQvalues= new Hashtable<Integer, Double>();


		while(stateSet.next()){
			stateList.put(stateSet.getInt("StateID"), new State(stateSet.getInt("StateID"), stateSet.getInt("Reward"), stateSet.getFloat("QValue"), stateSet.getInt("Occurrence")));
			tempQvalues.put(stateSet.getInt("StateID"), 0.0);
		}
		Set<Integer> keys = stateList.keySet();
		

		for (int i=0; i<maxIterations; i++){
			long startTime = System.nanoTime();
			while (stateTransSet.next()){
				if (!converged){
					int startID = stateTransSet.getInt("StartID");
					int endID = stateTransSet.getInt("EndID");
					int stateOcc = stateList.get(startID).getOccurrence();
					int transOcc = stateTransSet.getInt("Occurrence");
					double endQvalue = stateList.get(endID).getqValue();
					int endReward = stateList.get(endID).getReward();
					tempQvalues.put(startID, tempQvalues.get(startID)+(transOcc/stateOcc)*(endQvalue+endReward));
				}
			}
			for (Integer stateID : keys){
				State state = stateList.get(stateID);
				double newQvalue = tempQvalues.get(stateID);
				state.setqValue(newQvalue);
				stateList.put(stateID, state);
				tempQvalues.put(stateID, 0.0);
				currentValue += Math.abs(newQvalue);
				System.out.println(state.getqValue());
			}
			if (!converged){
				if((currentValue - lastValue)/currentValue < convergeCriterion){
					converged = true;
				}
			}

			lastValue = currentValue;
			currentValue = 0;
			long endTime = System.nanoTime();
			System.out.println("Iterasjon "+(i+1)+" ferdig. Tid: "+(endTime-startTime)/Math.pow(10, 9)+" sekunder");
			stateTransSet.beforeFirst();
			break;
			
		}
		


	}

}
