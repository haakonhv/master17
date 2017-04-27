package markov2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import master17.DatabaseHandler;

public class Reinforcement {

	public static void learning() throws ClassNotFoundException, SQLException{
		//ResultSet rs = DatabaseHandler.getStateActionNext();
		ResultSet transRS = DatabaseHandler.getDatabaseStateTrans();
		ResultSet stateActionRS = DatabaseHandler.getDatabaseStateAction();
		ResultSet stateRS = DatabaseHandler.getDatabaseStates();
		
		Hashtable<String, ArrayList<StateTransition>> transList = new Hashtable<String, ArrayList<StateTransition>>(); //startID+action peker på en liste med stateTransitions. StateTransition har occurrence
		Hashtable<String, StateAction> saList = new Hashtable<String, StateAction>(); //"State+action peker på et stateAction-objekt. Dette objektet har occurrence og value
		Hashtable<Integer, State> stateList = new Hashtable<Integer, State>(); //stateID peker på et state-objekt. Dette objektet har occurrence og value
		
		while (transRS.next()){ //konverterer resultset til hashtable med objekter
			String key = transRS.getString("StartID")+transRS.getString("Action");
			StateTransition trans = new StateTransition(new State(transRS.getInt("StartID")), new State (transRS.getInt("EndID")), transRS.getString("Action"));
			trans.setOccurrence(transRS.getInt("Occurrence"));
			if (transList.containsKey(key)){
				ArrayList<StateTransition> tlist = transList.get(key);
				tlist.add(trans);
				transList.put(key, tlist);
			}
			else{
				ArrayList<StateTransition> tlist = new ArrayList<StateTransition>();
				tlist.add(trans);
				transList.put(key, tlist);
			}
		}
		
		while (stateActionRS.next()){		//konverterer resultset til hashtable med objekter
			String key = stateActionRS.getString("StateID")+stateActionRS.getString("Action");
			StateAction sa = new StateAction(stateActionRS.getInt("StateID"), stateActionRS.getString("Action"),stateActionRS.getInt("Occurrence"));
			saList.put(key, sa);
		}
		
		while (stateRS.next()){ //konverterer resultset til hashtable med objekter
			State state = new State(stateRS.getInt("StateID"), stateRS.getInt("Reward"), 0.0, stateRS.getInt("Occurrence"));
			state.setTeam(stateRS.getString("Team"));
			stateList.put(stateRS.getInt("StateID"), state);
		}

		int maxI = 3000;
		double currentValue = 0;
		double lastValue = 0;
		boolean converged = false;
		double c = 0.0001;
		
		Set<String> saKeys = saList.keySet();
		Set<Integer> stateIds = stateList.keySet();

		for (int i = 0; i < 2000; i++){
			if (!converged){
				for (String key: saKeys){
					StateAction sa = saList.get(key);
					if(!transList.containsKey(key)) continue;
					ArrayList<StateTransition> tlist = transList.get(key);
					int saOcc = sa.getOccurrence();
					double newQ = 0.0;
					for (StateTransition st:tlist){
						int stOcc = st.getOccurrence();
						State nextState = stateList.get(st.getEndState().getStateID());
						double nextReward = (Double) (nextState.getReward()+nextState.getValue());
						newQ += ((double) stOcc/(double) saOcc)*nextReward;
						currentValue += Math.abs(newQ);
//						if (key.equals("280Shot")) {
//							System.out.println(newQ + ", " +nextState.getStateID()+", "+i);
//							if(newQ!=0) System.out.println(nextState.getStateID() +", " + nextState.getValue() + ", " + nextState.getReward());
//						}
					}
					sa.setValue(newQ);	
					
				}
				if (!converged){
					
					for (int stateID: stateIds){
						State s = stateList.get(stateID);
						s.setValue(0);
					}
					for (String key: saKeys){
						int stateID = Integer.parseInt(key.replaceAll("[^\\d.]", ""));
//					System.out.println(key);
//					System.out.println(stateID);
						State s = stateList.get(stateID);
						double stateOcc = (double) s.getOccurrence();
						double saOcc = (double) saList.get(key).getOccurrence();
//						if (stateID==280){
//							System.out.println(saOcc +", " + stateOcc + ", " +key + saList.get(key).getValue() +", " + s.getValue());
//							System.out.println(s.getValue());
//							System.out.println(s.getValue()+saList.get(key).getValue()*saOcc/stateOcc);
//							
//						}
						s.setValue(s.getValue()+saList.get(key).getValue()*saOcc/stateOcc);
						
					}
				}
				if ((currentValue-lastValue)/currentValue < c) converged = true;
				lastValue = currentValue;
				currentValue = 0;
				if(converged){
					for (int stateID: stateIds){
						System.out.println("Team: " + stateList.get(stateID).getTeam() + " Zone: " + stateList.get(stateID).getZone() + " Value: " + stateList.get(stateID).getValue());
						if (stateList.get(stateID).getValue()>0.10) System.err.println("Value>0.10");
					}
					System.err.println("CONVERGED " + i);
//					DatabaseHandler.updateStateActionQ(saList);
//					DatabaseHandler.updateStateValuesMod2(stateList);
				}
				
			}
		}
		


	}

	public static void learning2() throws ClassNotFoundException, SQLException{
		ResultSet rs = DatabaseHandler.getStateActionNext();
		Hashtable<String, ArrayList<StateActionNext>> saNextList = new Hashtable<String, ArrayList<StateActionNext>>();
		Hashtable<String, StateAction> saList = new Hashtable<String, StateAction>();

		int maxI = 3000;
		double currentValue = 0;
		double lastValue = 0;
		boolean converged = false;
		double c = 0.0001;

		//genererer tabeller
		while(rs.next()){
			int thisStateID = rs.getInt("StartID");
			int nextStateID = rs.getInt("NextID");
			String thisAction = rs.getString("StartAction");
			String nextAction = rs.getString("nextAction");
			int saNextOcc = rs.getInt("Occurrence");
			if (nextAction.equals("HomeGoal")) {
				if (saList.containsKey(nextStateID+nextAction)){
					StateAction sa = saList.get(nextStateID+nextAction);
					sa.setOccurrence(sa.getOccurrence()+saNextOcc);
				}
				else {
					StateAction sa = new StateAction(nextStateID, nextAction,1);
					sa.setValue(1);
					sa.setOccurrence(saNextOcc);
					saList.put(nextStateID+nextAction, sa);
				}
			}
			else if (nextAction.equals("AwayGoal")){
				if (saList.containsKey(nextStateID+nextAction)){
					StateAction sa = saList.get(nextStateID+nextAction);
					sa.setOccurrence(sa.getOccurrence()+saNextOcc);
				}
				else {
					StateAction sa = new StateAction(nextStateID, nextAction,1);
					sa.setValue(-1);
					sa.setOccurrence(saNextOcc);
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
				saList.put(key, sa);
			}
		}
		Set<String> nextKeys = saNextList.keySet();
		for (String key : nextKeys){
			ArrayList<StateActionNext> saNextArray = saNextList.get(key);
			for (int i = 0 ; i < saNextArray.size(); i++){
				if (!saList.containsKey(saNextArray.get(i).getNextStateID()+saNextArray.get(i).getNextAction())){
					int id = saNextArray.get(i).getNextStateID();
					String action = saNextArray.get(i).getNextAction();
					int occurrence = saNextArray.get(i).getOccurrence();
					saList.put(id+action, new StateAction(id, action, occurrence));
				}
			}
		}

		Set<String> keySet1 = saList.keySet();
		Hashtable<Integer, State> states = new Hashtable<Integer, State>();
		for (String key : keySet1){
			int stateID = Integer.parseInt(key.replaceAll("[^\\d.]", ""));
			double saValue = saList.get(key).getValue();
			int occurrence = saList.get(key).getOccurrence();
			if (states.containsKey(stateID)){
				State state = states.get(stateID);
				int oldOcc = state.getOccurrence();
				state.setOccurrence(oldOcc+occurrence);
				states.put(stateID, state);
			}
			else{
				State state = new State(stateID, saValue, occurrence);
				states.put(stateID, state);
			}
		}

		//reinforcement
		for (int i = 0; i < maxI ;i++){
			long startTime = System.nanoTime();
			if(!converged){
				Set<String> keySet = saList.keySet();
				for (String key: keySet){
					StateAction sa = saList.get(key);
					if (sa.getAction().equals("HomeGoal")||sa.equals("AwayGoal") || !saNextList.containsKey(key)){
						continue;
					}
					int saOcc = sa.getOccurrence();
					double qValue = 0.0;
					ArrayList<StateActionNext> nextList = saNextList.get(key);
					for (int j = 0; j < nextList.size(); j++){
						StateActionNext san = nextList.get(j);
						String nextKey = san.getNextStateID() + san.getNextAction();
						int sanOcc = san.getOccurrence();
						double nextStateValue = states.get(Integer.parseInt(nextKey.replaceAll("[^\\d.]", ""))).getValue(); //finner value til next state
						qValue += (double) sanOcc/saOcc*nextStateValue;
						currentValue += Math.abs(qValue);

					}
					sa.setValue(qValue);
				}
				//oppdaterer State-values
				Set<Integer> stateIDs = states.keySet();
				for (int stateID: stateIDs){
					State state = states.get(stateID);
					if(!(state.getValue()==1) || !(state.getValue()==-1)){
						state.setValue(0);
					}
				}
				for (String key: keySet){
					StateAction sa = saList.get(key);
					int stateID = Integer.parseInt(key.replaceAll("[^\\d.]", ""));
					State state = states.get(stateID);
					if (stateID==253) System.out.println(sa.getValue() + ", " +state.getValue());
					state.setValue(state.getValue()+(sa.getValue()*sa.getOccurrence())/state.getOccurrence());
					if(i%100==0){
						System.out.println(state.getValue());
					}
				}


				long endTime = System.nanoTime();
				if(i%100==0){
					System.out.println("iterasjon " + (i+1) + " ferdig. Tid: "+(endTime-startTime)/Math.pow(10, 9)+" sekunder "+ (currentValue - lastValue)/currentValue);
				}
			}
			if(!converged){
				if(((currentValue - lastValue)/currentValue) < c){
					converged = true;
					System.err.println("COVERGED");
				}
			}
			lastValue = currentValue;
			currentValue = 0;
		}



//		DatabaseHandler.updateStateActionQ(saList);
//		DatabaseHandler.updateStateValuesMod2(states);


	}

}
