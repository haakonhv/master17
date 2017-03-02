package markov2;

public class StateTransition {

	private int stateTransitionID;
	private State startState;
	private State endState;
	private String action;
	private int occurrence;

	public StateTransition(int stateTransitionID, State startState, State endState, String action) {
		super();
		this.startState = startState;
		this.endState = endState;
		this.action = action;
		this.occurrence = 1;
	}

	public void setStateTransitionID(int stateTransitionID) {
		this.stateTransitionID = stateTransitionID;
	}

	public StateTransition(State startState, State endState, String action) {
		super();
		this.startState = startState;
		this.endState = endState;
		this.action = action;
		this.occurrence = 1;
	}

	public int getStateTransitionID() {
		return stateTransitionID;
	}

	public State getStartState() {
		return startState;
	}

	public State getEndState() {
		return endState;
	}

	public String getAction() {
		return action;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void incrementOccurence(){
		this.occurrence+=1;
	}

}
