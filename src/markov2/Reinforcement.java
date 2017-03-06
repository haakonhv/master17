package markov2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import master17.DatabaseHandler;

public class Reinforcement {
	public static void learningAlgorithm() throws ClassNotFoundException, SQLException{
		double lastValue = 0;
		double currentValue = 0;
		boolean converged = false;
		int maxIterations = 3000;
		double convergeCriterion = 0.00001;

		ResultSet stateSet = DatabaseHandler.getDatabaseStates();
		ResultSet stateTransSet = DatabaseHandler.getDatabaseStateTrans();
		ResultSet stateActionSet = DatabaseHandler.getDatabaseStateAction();
		//ArrayList<State> stateList = new ArrayList<State>();
		Hashtable<Integer, State> stateList= new Hashtable<Integer, State>();
		Hashtable<Integer, Double> tempQvalues= new Hashtable<Integer, Double>();



		while(stateSet.next()){
			stateList.put(stateSet.getInt("StateID"), new State(stateSet.getInt("StateID"), stateSet.getInt("Reward"), 0.0, stateSet.getInt("Occurrence")));
			if (stateSet.getInt("Reward")!=0){
				System.out.println("m�l");
			}
			tempQvalues.put(stateSet.getInt("StateID"), 0.0);
		}
		Set<Integer> keys = stateList.keySet();

		Hashtable<Integer, Hashtable<String, StateAction>> stateAction = new Hashtable<Integer, Hashtable<String, StateAction>>();
		while (stateActionSet.next()){
			int stateID = stateActionSet.getInt("StateID");
			String action = stateActionSet.getString("Action");
			int occurrence = stateActionSet.getInt("Occurrence");
			if(stateAction.containsKey(stateID)){
				StateAction sa = new StateAction(stateID, action, occurrence);
				Hashtable<String, StateAction> actionTable = stateAction.get(stateID);
				actionTable.put(action, sa);
				stateAction.put(stateID, actionTable);
			}
			else{
				Hashtable<String, StateAction> actionTable = new Hashtable<String, StateAction>();
				StateAction sa = new StateAction(stateID, action, occurrence);
				actionTable.put(action, sa);
				stateAction.put(stateID, actionTable);
			}
		}
		Hashtable<Integer, Hashtable<String, StateAction>> tempStateAction = stateAction;

		for (int i=0; i<maxIterations; i++){
			long startTime = System.nanoTime();
			while (stateTransSet.next()){
				if (!converged){
					int startID = stateTransSet.getInt("StartID");
					int endID = stateTransSet.getInt("EndID");
					String action = stateTransSet.getString("Action");
					double stateOcc = stateList.get(startID).getOccurrence();
					double transOcc = stateTransSet.getInt("Occurrence");
					double stateActionOcc = 0.0;
					try{
						stateActionOcc = stateAction.get(startID).get(action).getOccurrence();
					}
					catch (NullPointerException e){
						System.out.println(startID + action);
						continue;
					}
					double endStateValue = stateList.get(endID).getValue();
					double endReward = stateList.get(endID).getReward();
					double prevQ= tempStateAction.get(startID).get(action).getValue();
					double tempQ = prevQ +(transOcc/stateActionOcc)*(endStateValue+endReward);
					StateAction tempSA = tempStateAction.get(startID).get(action);
					tempSA.setValue(tempQ);
					Hashtable<String, StateAction> actionTable = tempStateAction.get(startID);
					actionTable.put(action, tempSA);
					tempStateAction.put(startID, actionTable);
				}
			}
			if (!converged){
				Set<Integer> stateIDs = tempStateAction.keySet();
				double stateValue = 0.0;
				for (Integer stateID : stateIDs){
					Set<String> actions = tempStateAction.get(stateID).keySet();
					for (String action : actions){
						StateAction sa = tempStateAction.get(stateID).get(action);
						stateAction.get(stateID).put(action, sa);
						tempStateAction.get(stateID).get(action).setValue(0.0);
						stateValue += sa.getOccurrence()*sa.getValue();
						currentValue += Math.abs(sa.getValue());
					}
					stateValue = stateValue / stateList.get(stateID).getOccurrence();
					stateList.get(stateID).setValue(stateValue);
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

		DatabaseHandler.updateStateActionQ(stateAction);
		DatabaseHandler.updateStateValues(stateArray);
	}
}
