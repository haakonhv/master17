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


	public int getStateID() {
		return stateID;
	}
	public State(int zone, boolean home, String action, int period, int manpowerDiff, int matchStatus, int reward) {
		super();
		this.zone = zone;
		this.home = home;
		this.action = action;
		this.period = period;
		this.manpowerDiff = manpowerDiff;
		this.matchStatus = matchStatus;
		this.reward = reward;
		this.occurrence = 1;
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

	public String toString() {
		return "State [stateID=" + stateID + ", zone=" + zone + ", home=" + home + ", action=" + action + ", period="
				+ period + ", manpowerDiff=" + manpowerDiff + ", matchStatus=" + matchStatus + ", occurrence="
				+ occurrence + ", reward=" + reward + "]";
	}



}
