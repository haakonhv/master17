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
	public void increaseAerial(double value){
		this.aerialDuel += value;
	}
	public void decreaseAerial(double value){
		this.aerialDuel -= value;
	}
	public void increaseDispossessed(double value){
		this.dispossessed += value;
	}
	public void decreaseDispossessed(double value){
		this.dispossessed -= value;
	}
	public void increaseFoulCommitted(double value){
		this.foulCommitted += value;
	}
	public void decreaseFoulCommitted(double value){
		this.foulCommitted -= value;
	}
	public void increaseFouled(double value){
		this.fouled += value;
	}
	public void decreaseFouled(double value){
		this.fouled -= value;
	}
	public void increaseInterception(double value){
		this.interception += value;
	}
	public void decreaseInterception(double value){
		this.interception -= value;
	}
	public void increaseBallRecovery(double value){
		this.ballRecovery += value;
	}
	public void decreaseBallRecovery(double value){
		this.ballRecovery -= value;
	}
	public void increaseBallTouch(double value){
		this.ballTouch += value;
	}
	public void decreaseBallTouch(double value){
		this.ballTouch -= value;
	}
	public void increaseBallCarry(double value){
		this.ballCarry += value;
	}
	public void decreaseBallCarry(double value){
		this.ballCarry -= value;
	}
	public void increasePass(double value){
		this.pass += value;
	}
	public void decreasePass(double value){
		this.pass -= value;
	}
	public void increaseLongPass(double value){
		this.longPass += value;
	}
	public void decreaseLongPass(double value){
		this.longPass -= value;
	}
	public void increaseCross(double value){
		this.cross += value;
	}
	public void decreaseCross(double value){
		this.cross -= value;
	}
	public void increaseFreekickPass(double value){
		this.freekickPass += value;
	}
	public void decreaseFreekickPass(double value){
		this.freekickPass -= value;
	}
	public void increaseCorner(double value){
		this.cornerTaken += value;
	}
	public void decreaseCorner(double value){
		this.cornerTaken -= value;
	}
	public void increaseThrowIn(double value){
		this.throwInTaken += value;
	}
	public void decreaseThrowIn(double value){
		this.throwInTaken -= value;
	}
	public void increaseTakeOn(double value){
		this.takeOn += value;
	}
	public void decreaseTakeOn(double value){
		this.takeOn -= value;
	}
	public void increaseShot(double value){
		this.shot += value;
	}
	public void decreaseShot(double value){
		this.shot -= value;
	}

}
