package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Qlearning {
	public static void qLearningAlgorithm() throws ClassNotFoundException, SQLException{
		double lastValue = 0;
		double currentValue = 0;
		boolean converged = false;
		int maxIterations = 100;
		double convergeCriterion = 0.001;
		
		ResultSet stateSet = DatabaseHandler.getDatabaseStates();
		ResultSet stateTransSet = DatabaseHandler.getDatabaseStateTrans();
		ArrayList<State> stateList = new ArrayList<State>();
		
		
		
		while(stateSet.next()){
			stateList.add(new State(stateSet.getInt("StateID"), stateSet.getInt("Reward"), stateSet.getFloat("QValue"), stateSet.getInt("Occurrence")));
		}
		
		for (int i=0; i<maxIterations; i++){
			for (int j=0; j<stateList.size();j++){
				State s = stateList.get(j);
				if (!converged){
					float nextStateQValues = 0;
					while(stateTransSet.next()){
						if(s.getStateID()==stateTransSet.getInt("StartID")){
							nextStateQValues += ((double) stateTransSet.getInt("Occurrence"))*getQvalue(stateList, stateTransSet.getInt("EndID"));
							//System.out.println("Transition fra state " + s.getStateID() + " til state "+ stateTransSet.getInt("EndID"));
						}
					}
					stateTransSet.beforeFirst();
					double stateReward = (double) s.getReward();
					double stateOccurrence = (double) s.getOccurrence();
					double newQValue =stateReward + nextStateQValues/stateOccurrence; 
					s.setqValue(newQValue);
					System.out.println("StateID: " + s.getStateID()+ " ny Q: " +newQValue + " i iterasjon: " + i);
					currentValue = currentValue + Math.abs(newQValue);
				}
			}
			if (!converged){
				if((currentValue - lastValue)/currentValue < convergeCriterion){
					converged = true;
				}
			}
			lastValue = currentValue;
			currentValue = 0;	
		}
		for (State s: stateList){
			System.out.println(s);
		}
		
	}
	public static double getQvalue(ArrayList<State> stateList, int stateID){
		for (State s: stateList){
			if (s.getStateID() == stateID){
				return s.getqValue();
			}
		}
		return 0;
	}
	
}