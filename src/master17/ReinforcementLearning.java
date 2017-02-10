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
		int maxIterations = 3000;
		double convergeCriterion = 0.00001;

		ResultSet stateSet = DatabaseHandler.getDatabaseStates();
		ResultSet stateTransSet = DatabaseHandler.getDatabaseStateTrans();
		//ArrayList<State> stateList = new ArrayList<State>();
		Hashtable<Integer, State> stateList= new Hashtable<Integer, State>();
		Hashtable<Integer, Double> tempQvalues= new Hashtable<Integer, Double>();


		while(stateSet.next()){
			stateList.put(stateSet.getInt("StateID"), new State(stateSet.getInt("StateID"), stateSet.getInt("Reward"), 0.0, stateSet.getInt("Occurrence")));
			if (stateSet.getInt("Reward")!=0){
				System.out.println("mål");
			}
			tempQvalues.put(stateSet.getInt("StateID"), 0.0);
		}
		Set<Integer> keys = stateList.keySet();
		

		for (int i=0; i<maxIterations; i++){
			long startTime = System.nanoTime();
			while (stateTransSet.next()){
				if (!converged){
					int startID = stateTransSet.getInt("StartID");
					int endID = stateTransSet.getInt("EndID");
					double stateOcc = stateList.get(startID).getOccurrence();
					double transOcc = stateTransSet.getInt("Occurrence");
					double endQvalue = stateList.get(endID).getqValue();
					double endReward = stateList.get(endID).getReward();
					double prevQ = tempQvalues.get(startID);
					double tempq = prevQ +(transOcc/stateOcc)*(endQvalue+endReward);
					
					tempQvalues.put(startID, tempq);
				}
			}
			if (!converged){
				for (Integer stateID : keys){
				
				State state = stateList.get(stateID);
				double newQvalue = tempQvalues.get(stateID);
				state.setqValue(newQvalue);
				stateList.put(stateID, state);
				tempQvalues.put(stateID, 0.0);
				currentValue += Math.abs(newQvalue);
				//System.out.println(stateID);break;
				}
			}
			
			if (!converged){
				if((currentValue - lastValue)/currentValue < convergeCriterion){
					System.out.println("converged");
					converged = true;

				}
			}
			
			lastValue = currentValue;
			currentValue = 0;
			long endTime = System.nanoTime();
			System.out.println("Iterasjon "+(i+1)+" ferdig. Tid: "+(endTime-startTime)/Math.pow(10, 9)+" sekunder");
			stateTransSet.beforeFirst();
			
		}

		ArrayList<State> stateArray = new ArrayList<State>();
		for (Integer stateID: keys){
			stateArray.add(stateList.get(stateID));
		}
		DatabaseHandler.updateQValues(stateArray);
		


	}

}
