package markov2;

public class State {

	private int stateID;
	private int zone;
	private String team;
	private int period;
	private int matchStatus;
	private int occurrence;
	private int reward;
	private double value;



	public State(int stateID,int zone, String team, int period, int matchStatus, int reward) {
		super();
		this.stateID = stateID;
		this.zone = zone;
		this.team = team;
		this.period = period;
		this.matchStatus = matchStatus;
		this.reward = reward;
		this.occurrence = 1;
	}


	public State(int stateID, int reward, double value, int occurrence) {
		super();
		this.stateID = stateID;
		this.reward = reward;
		this.value = value;
		this.occurrence = occurrence;
	}


	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	public void setOccurrenceZero(){
		this.occurrence = 0;
	}

	public int getStateID(){
		return stateID;
	}

	public int getZone() {
		return zone;
	}
//	public boolean isHome() {
//		return home;
//	}

	public int getPeriod() {
		return period;
	}

	public String getTeam() {
		return team;
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

}
