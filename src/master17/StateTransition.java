package master17;

public class StateTransition {

	private int startStateID;
	private int endStateID;
	private int occurrence;

	public StateTransition(int startStateID, int endStateID) {
		super();
		this.startStateID = startStateID;
		this.endStateID = endStateID;
		this.occurrence = 1;
	}

	public int getStartStateID() {
		return startStateID;
	}

	public int getEndStateID() {
		return endStateID;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void incrementOccurence(){
		this.occurrence+=1;
	}

}
