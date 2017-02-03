package master17;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StateTransitionBuilder {

	public static ArrayList<StateTransition> getStateTransitions(Game game, ArrayList<StateTransition> stateTransList) throws SQLException, ClassNotFoundException{
		ResultSet rs = DatabaseHandler.getOrderedEvents(game.getGame_id());
		rs.next();
		int prevStateID = rs.getInt("StateID");
		while(rs.next()){
			int currentStateID = rs.getInt("StateID");
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
			prevStateID = currentStateID;
		}
		return stateTransList;
	}
}
