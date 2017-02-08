package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StateTransitionBuilder {

	public static void setStateTransitions() throws SQLException, ClassNotFoundException{
		ResultSet rs = DatabaseHandler.getOrderedEvents();
		ArrayList<StateTransition> stateTransList = new ArrayList<StateTransition>();
		rs.next();
		int prevStateID = rs.getInt("StateID");
		String prevAction = rs.getString("Action");
		int counter = 0;
		while(rs.next()){
			counter++;
			int currentStateID = rs.getInt("StateID");
			if (!(prevAction.equals("Goal") || prevAction.equals("Out of play") || prevAction.equals("End of period") || prevAction.equals("Goalkeeper"))){
				if(stateTransList.size()==0){
					stateTransList.add(new StateTransition(prevStateID, currentStateID));
				}
				else {
					boolean transExists = false;
					for (int i = 0; i< stateTransList.size();i++){
						StateTransition st = stateTransList.get(i);
						if (st.getStartStateID() == prevStateID && st.getEndStateID() == currentStateID){
							st.incrementOccurence();
							transExists = true;
							break;
						}
					}
					if (!transExists){
						stateTransList.add(new StateTransition(prevStateID, currentStateID));
					}
				}
			}
			prevStateID = currentStateID;
			prevAction = rs.getString("Action");
			if(counter%1000==0){
				System.out.println("Laget "+counter+"transitions");
			}
		}
		DatabaseHandler.insertStateTransitions(stateTransList);
	}
}
