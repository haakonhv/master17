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
		int maxIterations = 10;
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
		Hashtable<Integer, Hashtable<String, StateAction>> tempStateAction = new Hashtable<Integer, Hashtable<String, StateAction>>();
		while (stateActionSet.next()){
			int stateID = stateActionSet.getInt("StateID");
			String action = stateActionSet.getString("Action");
			int occurrence = stateActionSet.getInt("Occurrence");
			if(stateAction.containsKey(stateID)){
				StateAction sa = new StateAction(stateID, action, occurrence);
				StateAction tempsa = new StateAction(stateID, action, occurrence);
				Hashtable<String, StateAction> actionTable = stateAction.get(stateID);
				Hashtable<String, StateAction> tempActionTable = stateAction.get(stateID);
				actionTable.put(action, sa);
				stateAction.put(stateID, actionTable);
				tempActionTable.put(action, tempsa);
				tempStateAction.put(stateID, tempActionTable);
			}
			else{
				Hashtable<String, StateAction> actionTable = new Hashtable<String, StateAction>();
				Hashtable<String, StateAction> tempActionTable = new Hashtable<String, StateAction>();
				StateAction sa = new StateAction(stateID, action, occurrence);
				StateAction tempsa = new StateAction(stateID, action, occurrence);
				actionTable.put(action, sa);
				stateAction.put(stateID, actionTable);
				tempActionTable.put(action, tempsa);
				tempStateAction.put(stateID, tempActionTable);
			}
		}


		for (int i=0; i<maxIterations; i++){
			long startTime = System.nanoTime();
			while (stateTransSet.next()){
				if (!converged){
					int startID = stateTransSet.getInt("StartID");
					int endID = stateTransSet.getInt("EndID");
					String action = stateTransSet.getString("Action");
					double stateOcc = stateList.get(startID).getOccurrence();
					double transOcc = stateTransSet.getInt("Occurrence");
					double stateActionOcc = stateAction.get(startID).get(action).getOccurrence();
					double endStateValue = stateList.get(endID).getValue();
					double endReward = stateList.get(endID).getReward();
					double prevQ= tempStateAction.get(startID).get(action).getValue();
					double tempQ = prevQ +(transOcc/stateActionOcc)*(endStateValue+endReward);
					StateAction tempSA = tempStateAction.get(startID).get(action);
					tempSA.setValue(tempQ);
					Hashtable<String, StateAction> actionTable = tempStateAction.get(startID);
					actionTable.put(action, tempSA);
					tempStateAction.put(startID, actionTable);
					if (startID == 49){
						System.out.println("Action: " +action + " tempQ: " + tempQ + " Reward: " + endReward+ " startStateV: "+ stateList.get(startID).getValue() +" endstateID: " + endID+" endStateV: "+ endStateValue + " stateocc: " + stateOcc + " transocc: " + transOcc);
					}
					if (endReward!=0){
//						System.out.println(tempStateAction.get(startID).get(action).getValue());
//						System.out.println(tempSA.getValue());
					}
					
				}
			}
			if (!converged){
				Set<Integer> stateIDs = tempStateAction.keySet();
				double stateValue = 0.0;
				for (int stateID: stateIDs){
					Set<String> actions = tempStateAction.get(stateID).keySet();
					int Qcount=0;
					for (String action: actions){
						StateAction tempSA = tempStateAction.get(stateID).get(action);
						int occurrence = tempSA.getOccurrence();
						double newValue = tempSA.getValue();
						if(tempSA.getValue()>1){
							System.out.println("!!");
							System.out.println(tempSA.getStateID());
						}
						Hashtable<String, StateAction> realActiontable = stateAction.get(stateID);
						StateAction realStateAction = new StateAction(stateID, action, occurrence);
						realStateAction.setValue(newValue);
						realActiontable.put(action, realStateAction);
						stateAction.put(stateID, realActiontable);
						tempSA.setValue(0.0);
						tempStateAction.get(stateID).put(action, tempSA);
						stateValue += (double) realStateAction.getOccurrence()*realStateAction.getValue();
						Qcount+=occurrence;
						
					}
//					if (stateValue/(double) stateList.get(stateID).getOccurrence()>1){
//						System.out.println(stateID);
//						System.out.println(stateValue);
//						System.out.println(Qcount);
//						System.out.println(stateList.get(stateID).getOccurrence());
//						System.out.println(stateValue/stateList.get(stateID).getOccurrence());
//					}
					stateValue = stateValue/(double) (stateList.get(stateID).getOccurrence());
					
					stateList.get(stateID).setValue(stateValue);
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
		Set<Integer> stateids2=stateAction.keySet();
//		for(Integer stateid : stateids2){
//			Set<String> actions = stateAction.get(stateid).keySet();
//			for (String action:actions){
//				if (stateAction.get(stateid).get(action).getValue()!=0){
//					System.out.println("9999");
//					System.out.println(stateAction.get(stateid).get(action).getValue());
//				}
//			}
//			System.out.println(stateList.get(stateid).getValue());
//		}

		DatabaseHandler.updateStateActionQ(stateAction);
		DatabaseHandler.updateStateValues(stateArray);
	}
	public static void learning() throws ClassNotFoundException, SQLException{
		ResultSet rs = DatabaseHandler.getStateActionNext();
		Hashtable<String, ArrayList<StateActionNext>> saNextList = new Hashtable<String, ArrayList<StateActionNext>>();
		Hashtable<String, StateAction> saList = new Hashtable<String, StateAction>();
		
		int maxI = 750;
		
		
		//genererer tabeller
		while(rs.next()){
			int thisStateID = rs.getInt("StartID");
			int nextStateID = rs.getInt("NextID");
			String thisAction = rs.getString("StartAction");
			String nextAction = rs.getString("nextAction");
			int saNextOcc = rs.getInt("Occurrence");
			if (nextAction.equals("HomeGoal")) {
				if (saList.containsKey(nextStateID+nextAction)){
					saList.get(nextStateID+nextAction).incrementOccurrence();
				}
				else {
					StateAction sa = new StateAction(nextStateID, nextAction,1);
					sa.setValue(1);
					saList.put(nextStateID+nextAction, sa);
				}
			}
			else if (nextAction.equals("AwayGoal")){
				if (saList.containsKey(nextStateID+nextAction)){
					saList.get(nextStateID+nextAction).incrementOccurrence();
				}
				else {
					StateAction sa = new StateAction(nextStateID, nextAction,1);
					sa.setValue(-1);
					saList.put(nextStateID+nextAction, sa);
				}
			}
			String key = thisStateID+thisAction;
			if (saNextList.containsKey(key)){
				ArrayList<StateActionNext> nextList = saNextList.get(key);
				StateActionNext san = new StateActionNext(thisStateID, nextStateID, thisAction, nextAction, saNextOcc);
				nextList.add(san);
			}
			else{
				ArrayList<StateActionNext> nextList = new ArrayList<StateActionNext>();
				StateActionNext san = new StateActionNext(thisStateID, nextStateID, thisAction, nextAction, saNextOcc);
				nextList.add(san);
				saNextList.put(key, nextList);
			}
			if (saList.containsKey(key)){
				StateAction sa = saList.get(key);
				sa.setOccurrence(sa.getOccurrence()+saNextOcc);
			}
			else {
				StateAction sa = new StateAction(thisStateID,thisAction,saNextOcc);
				if (thisStateID == 254) System.out.println(sa.getValue());
				
				saList.put(key, sa);
			}
		}
		Set<String> nextKeys = saNextList.keySet();
		for (String key : nextKeys){
			ArrayList<StateActionNext> saNextArray = saNextList.get(key);
			for (int i = 0 ; i < saNextArray.size(); i++)			{
				if (!saList.containsKey(saNextArray.get(i).getNextStateID()+saNextArray.get(i).getNextAction())){
					int id = saNextArray.get(i).getNextStateID();
					String action = saNextArray.get(i).getNextAction();
					int occurrence = saNextArray.get(i).getOccurrence();
					saList.put(id+action, new StateAction(id, action, occurrence));
				}
			}
		}
		
		//reinforcement
		for (int i = 0; i < maxI ;i++){
			Set<String> keySet = saList.keySet();
			for (String key: keySet){
				StateAction sa = saList.get(key);
				if (sa.getAction().equals("HomeGoal")||sa.equals("AwayGoal") || !saNextList.containsKey(key)){
					continue;
				}
				int saOcc = sa.getOccurrence();
				

				double qValue = 0.0;
				ArrayList<StateActionNext> nextList = saNextList.get(key);
//				System.out.println(key);
				for (int j = 0; j < nextList.size(); j++){
					StateActionNext san = nextList.get(j);
					String nextKey = san.getNextStateID() + san.getNextAction();
					int sanOcc = san.getOccurrence();
					double nextQvalue = saList.get(nextKey).getValue();
					qValue += (double) sanOcc/saOcc*nextQvalue;

				}
				sa.setValue(qValue);
				if (sa.getValue() == saList.get(key).getValue()){
//					System.out.println("bra");
				}
				else {
					System.out.println("Ikke bra");
				}
//				System.out.println(sa.getValue());
//				System.out.println(saList.get(key).getValue());
				
				
				
			}
			
			System.out.println("iterasjon " + i + " ferdig");
		}
		Set<String> keyset = saList.keySet();
		for (String key:keyset){
			if (saList.get(key).getValue()>0) System.out.println(saList.get(key));
		}


	}
	
}
