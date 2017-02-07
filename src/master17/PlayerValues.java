package master17;

public class PlayerValues {

	private int playerID;
	private double tackle;
	private double clearance;
	private double aerialDuel;
	private double dispossessed;
	private double foulCommitted;
	private double fouled;
	private double interception;
	private double ballRecovery;
	private double ballTouch;
	private double ballCarry;
	private double pass;
	private double longPass;
	private double cross;
	private double freekickPass;
	private double cornerTaken;
	private double throwInTaken;
	private double takeOn;
	private double shot;

	public PlayerValues(int playerID) {
		super();
		this.playerID = playerID;
		this.tackle = 0;
		this.clearance = 0;
		this.aerialDuel = 0;
		this.dispossessed = 0;
		this.foulCommitted = 0;
		this.fouled = 0;
		this.interception = 0;
		this.ballRecovery = 0;
		this.ballTouch = 0;
		this.ballCarry = 0;
		this.pass = 0;
		this.longPass = 0;
		this.cross = 0;
		this.freekickPass = 0;
		this.cornerTaken = 0;
		this.throwInTaken = 0;
		this.takeOn = 0;
		this.shot = 0;
	}

	public void increaseTackle(double value){
		this.tackle += value;
	}

	public void decreaseTackle(double value){
		this.tackle -= value;
	}

	public void increaseClearance(double value){
		this.clearance += value;
	}

	public void decreaseClearance(double value){
		this.clearance -= value;
	}

}
