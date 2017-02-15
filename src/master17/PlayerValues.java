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
	private double ballReceived;
	private double total;
	private double blockedShot;
	private double savedShot;

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
		this.ballReceived = 0;
		this.blockedShot = 0;
		this.savedShot = 0;
		this.total=0;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void updateTackle(double value){
		this.tackle += value;
	}

	public void updateClearance(double value){
		this.clearance += value;
	}

	public void updateAerial(double value){
		this.aerialDuel += value;
	}

	public void updateDispossessed(double value){
		this.dispossessed += value;
	}

	public void updateFoulCommitted(double value){
		this.foulCommitted += value;
	}

	public void updateFouled(double value){
		this.fouled += value;
	}

	public void updateInterception(double value){
		this.interception += value;
	}

	public void updateBallRecovery(double value){
		this.ballRecovery += value;
	}

	public void updateBallTouch(double value){
		this.ballTouch += value;
	}

	public void updateBallCarry(double value){
		this.ballCarry += value;
	}

	public void updatePass(double value){
		this.pass += value;
	}

	public void updateLongPass(double value){
		this.longPass += value;
	}

	public void updateCross(double value){
		this.cross += value;
	}

	public void updateFreekickPass(double value){
		this.freekickPass += value;
	}

	public void updateCorner(double value){
		this.cornerTaken += value;
	}

	public void updateThrowIn(double value){
		this.throwInTaken += value;
	}

	public void updateTakeOn(double value){
		this.takeOn += value;
	}

	public void updateShot(double value){
		this.shot += value;
	}
	public void updateBallReceived(double value){
		this.ballReceived += value;
	}


	public double getBallReceived() {
		return ballReceived;
	}

	public void updateBlockedShot(double value){
		this.blockedShot += value;
	}


	public double getBlockedShot() {
		return blockedShot;
	}

	public void updateSavedShot(double value){
		this.savedShot += value;
	}


	public double getSavedShot() {
		return savedShot;
	}

	public void updateValue(String action, double value){
		if(action.equals("Pass")){
			this.updatePass(value);
		}
		else if(action.equals("Ball received")){
			this.updateBallReceived(value);
		}
		else if(action.equals("Long pass")){
			this.updateLongPass(value);
		}
		else if(action.equals("Ball carry")){
			this.updateBallCarry(value);
		}
		else if(action.equals("Ball recovery")){
			this.updateBallRecovery(value);
		}
		else if(action.equals("Aerial duel")){
			this.updateAerial(value);
		}
		else if(action.equals("Clearance")){
			this.updateClearance(value);
		}
		else if(action.equals("Throw in taken")){
			this.updateThrowIn(value);
		}
		else if(action.equals("Ball touch")){
			this.updateBallTouch(value);
		}
		else if(action.equals("Interception")){
			this.updateInterception(value);
		}
		else if(action.equals("Cross")){
			this.updateCross(value);
		}
		else if(action.equals("Tackle")){
			this.updateTackle(value);
		}
		else if(action.equals("Shot")){
			this.updateShot(value);
		}
		else if(action.equals("Take on")){
			this.updateTakeOn(value);
		}
		else if(action.equals("Free kick pass")){
			this.updateFreekickPass(value);
		}
		else if(action.equals("Foul committed")){
			this.updateFoulCommitted(value);
		}
		else if(action.equals("Fouled")){
			this.updateFouled(value);
		}
		else if(action.equals("Dispossessed")){
			this.updateDispossessed(value);
		}
		else if(action.equals("Corner taken")){
			this.updateCorner(value);
		}
		else if(action.equals("Blocked shot")){
			this.updateBlockedShot(value);
		}
		else if(action.equals("Shot saved")){
			this.updateSavedShot(value);
		}

	}

	public double getTackle() {
		return tackle;
	}

	public double getClearance() {
		return clearance;
	}

	public double getAerialDuel() {
		return aerialDuel;
	}

	public double getDispossessed() {
		return dispossessed;
	}

	public double getFoulCommitted() {
		return foulCommitted;
	}

	public double getFouled() {
		return fouled;
	}

	public double getInterception() {
		return interception;
	}

	public double getBallRecovery() {
		return ballRecovery;
	}

	public double getBallTouch() {
		return ballTouch;
	}

	public double getBallCarry() {
		return ballCarry;
	}

	public double getPass() {
		return pass;
	}

	public double getLongPass() {
		return longPass;
	}

	public double getCross() {
		return cross;
	}

	public double getFreekickPass() {
		return freekickPass;
	}

	public double getCornerTaken() {
		return cornerTaken;
	}

	public double getThrowInTaken() {
		return throwInTaken;
	}

	public double getTakeOn() {
		return takeOn;
	}

	public double getShot() {
		return shot;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal() {
		this.total = this.getAerialDuel()+this.getBallCarry()+this.getBallRecovery()+this.getBallTouch()+this.getClearance()+this.getCornerTaken()+this.getCross()+
				this.getDispossessed()+this.getFoulCommitted()+this.getFouled()+this.getFreekickPass()+this.getInterception()+this.getLongPass()+this.getPass()+this.getShot()+
				this.getTackle()+this.getTakeOn()+this.getThrowInTaken()+this.getBallReceived()+this.getBlockedShot()+this.getSavedShot();
	}


}
