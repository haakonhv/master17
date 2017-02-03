package master17;

public class State {

	private int stateID;
	private int zone;
	private boolean home;
	private String action;
	private int period;
	private int manpowerDiff;
	private int matchStatus;
	private int occurrence;
	private int reward;
	private double qValue;

	

	public State(int stateID,int zone, boolean home, String action, int period, int manpowerDiff, int matchStatus, int reward) {
		super();
		this.stateID = stateID;
		this.zone = zone;
		this.home = home;
		this.action = action;
		this.period = period;
		this.manpowerDiff = manpowerDiff;
		this.matchStatus = matchStatus;
		this.reward = reward;
		this.occurrence = 1;
	}
	
	
	public State(int stateID, int reward, double qValue, int occurrence) {
		super();
		this.stateID = stateID;
		this.reward = reward;
		this.qValue = qValue;
		this.occurrence = occurrence;
	}


	public double getqValue() {
		return qValue;
	}

	public void setqValue(double qValue) {
		this.qValue = qValue;
	}

	public int getStateID(){
		return stateID;
	}

	public int getZone() {
		return zone;
	}
	public boolean isHome() {
		return home;
	}
	public String getAction() {
		return action;
	}
	public int getPeriod() {
		return period;
	}
	public int getManpowerDiff() {
		return manpowerDiff;
	}
	public int getMatchStatus() {
		return matchStatus;
	}
	public int getOccurrence() {
		return occurrence;
	}
	public int getReward() {
		return reward;
	}

	public void incrementOccurrence(){
		this.occurrence +=1;
	}


	@Override
	public String toString() {
		return "State [stateID=" + stateID + ", occurrence=" + occurrence + ", reward=" + reward + ", qValue=" + qValue
				+ "]";
	}





}
