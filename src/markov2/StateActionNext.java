package markov2;

public class StateActionNext {
	int stateID;
	int nextStateID;
	String action;
	String nextAction;
	int occurrence;
	public StateActionNext(int stateID, int nextStateID, String action, String nextAction) {
		super();
		this.stateID = stateID;
		this.nextStateID = nextStateID;
		this.action = action;
		this.nextAction = nextAction;
		this.occurrence = 1;
	}
	public int getStateID() {
		return stateID;
	}
	public StateActionNext(int stateID, int nextStateID, String action, String nextAction, int occurrence) {
		super();
		this.stateID = stateID;
		this.nextStateID = nextStateID;
		this.action = action;
		this.nextAction = nextAction;
		this.occurrence = occurrence;
	}
	public int getNextStateID() {
		return nextStateID;
	}
	public String getAction() {
		return action;
	}
	public String getNextAction() {
		return nextAction;
	}
	public int getOccurrence() {
		return occurrence;
	}
	public void incrementOccurrence(){
		this.occurrence+=1;
	}
	

}
