package markov2;

public class StateTransition {

	private State startState;
	private State endState;
	private String action;
	private int occurrence;

	public StateTransition(State startState, State endState, String action) {
		super();
		this.startState = startState;
		this.endState = endState;
		this.action = action;
		this.occurrence = 1;
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
