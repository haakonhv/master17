package master17;

import java.util.ArrayList;
import java.util.Hashtable;

public class PlayerValues {

	private int playerID;
	private int gameID;
	private int teamID;
	private ArrayList<Double> tackle;
	private ArrayList<Double> clearance;
	private ArrayList<Double> aerialDuel;
	private ArrayList<Double> dispossessed;
	private ArrayList<Double> foulCommitted;
	private ArrayList<Double> fouled;
	private ArrayList<Double> interception;
	private ArrayList<Double> ballRecovery;
	private ArrayList<Double> ballTouch;
	private ArrayList<Double> ballCarry;
	private ArrayList<Double> pass;
	private ArrayList<Double> longPass;
	private ArrayList<Double> cross;
	private ArrayList<Double> freekickPass;
	private ArrayList<Double> cornerTaken;
	private ArrayList<Double> throwInTaken;
	private ArrayList<Double> takeOn;
	private ArrayList<Double> shot;
	private ArrayList<Double> headedShot;
	private ArrayList<Double> ballReceived;
	private ArrayList<Double> total;
	private ArrayList<Double> blockedShot;
	private ArrayList<Double> savedShot;
	private Hashtable<String, Integer> actionCount;

	public PlayerValues(int playerID, int gameID, int teamID) {
		super();
		this.playerID = playerID;
		this.teamID = teamID;
		this.gameID = gameID;
		this.tackle =  new ArrayList<Double>();
		this.clearance = new ArrayList<Double>();
		this.aerialDuel = new ArrayList<Double>();
		this.dispossessed = new ArrayList<Double>();
		this.foulCommitted = new ArrayList<Double>();
		this.fouled = new ArrayList<Double>();
		this.interception = new ArrayList<Double>();
		this.ballRecovery = new ArrayList<Double>();
		this.ballTouch = new ArrayList<Double>();
		this.ballCarry = new ArrayList<Double>();
		this.pass = new ArrayList<Double>();
		this.longPass = new ArrayList<Double>();
		this.cross = new ArrayList<Double>();
		this.freekickPass = new ArrayList<Double>();
		this.cornerTaken = new ArrayList<Double>();
		this.throwInTaken = new ArrayList<Double>();
		this.takeOn = new ArrayList<Double>();
		this.shot = new ArrayList<Double>();
		this.headedShot = new ArrayList<Double>();
		this.ballReceived = new ArrayList<Double>();
		this.blockedShot = new ArrayList<Double>();
		this.savedShot = new ArrayList<Double>();
		this.total=new ArrayList<Double>();
		this.actionCount = new Hashtable<String, Integer>();
		generateCountTable();
		generateArrayLists();
	}
	private void generateArrayLists(){
		for (int i = 0; i < 4; i++){
			tackle.add(0.0);
			clearance.add(0.0);
			aerialDuel.add(0.0);
			dispossessed.add(0.0);
			foulCommitted.add(0.0);
			fouled.add(0.0);
			interception.add(0.0);
			ballRecovery.add(0.0);
			ballTouch.add(0.0);
			ballCarry.add(0.0);
			pass.add(0.0);
			longPass.add(0.0);
			cross.add(0.0);
			freekickPass.add(0.0);
			cornerTaken.add(0.0);
			throwInTaken.add(0.0);
			takeOn.add(0.0);
			shot.add(0.0);
			headedShot.add(0.0);
			ballReceived.add(0.0);
			total.add(0.0);
			total.add(0.0);
			blockedShot.add(0.0);
			savedShot.add(0.0);
		}
	}
	
	private void generateCountTable(){
		actionCount.put("tackle",0);
		actionCount.put("clearance",0);
		actionCount.put("aerialDuel",0);
		actionCount.put("dispossessed",0);
		actionCount.put("foulCommitted",0);
		actionCount.put("fouled",0);
		actionCount.put("interception",0);
		actionCount.put("ballRecovery",0);
		actionCount.put("ballTouch",0);
		actionCount.put("ballCarry",0);
		actionCount.put("pass",0);
		actionCount.put("longPass",0);
		actionCount.put("cross",0);
		actionCount.put("freekickPass",0);
		actionCount.put("cornerTaken",0);
		actionCount.put("throwInTaken",0);
		actionCount.put("takeOn",0);
		actionCount.put("shot",0);
		actionCount.put("headedShot",0);
		actionCount.put("ballReceived",0);
		actionCount.put("total",0);
		actionCount.put("blockedShot",0);
		actionCount.put("actionCount",0);
		actionCount.put("savedShot", 0);
	}

	public int getGameID() {
		return gameID;
	}

	public int getTeamID() {
		return teamID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void updateTackle(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = tackle.get(i);
			this.tackle.set(i, oldVal+values.get(i));
		}
		actionCount.put("tackle", actionCount.get("tackle")+1);
	}

	public void updateClearance(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = clearance.get(i);
			this.clearance.set(i, oldVal+values.get(i));
		}
		actionCount.put("clearance", actionCount.get("clearance")+1);
	}

	public void updateAerial(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = aerialDuel.get(i);
			this.aerialDuel.set(i, oldVal+values.get(i));
		}
		actionCount.put("aerialDuel", actionCount.get("aerialDuel")+1);
	}

	public void updateDispossessed(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = dispossessed.get(i);
			this.dispossessed.set(i, oldVal+values.get(i));
		}
		actionCount.put("dispossessed", actionCount.get("dispossessed")+1);
	}

	public void updateFoulCommitted(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = foulCommitted.get(i);
			this.foulCommitted.set(i, oldVal+values.get(i));
		}
		actionCount.put("foulCommitted", actionCount.get("foulCommitted")+1);
	}

	public void updateFouled(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = fouled.get(i);
			this.fouled.set(i, oldVal+values.get(i));
		}
		actionCount.put("fouled", actionCount.get("fouled")+1);
	}

	public void updateInterception(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = interception.get(i);
			this.interception.set(i, oldVal+values.get(i));
		}
		actionCount.put("interception", actionCount.get("interception")+1);
	}

	public void updateBallRecovery(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = ballRecovery.get(i);
			this.ballRecovery.set(i, oldVal+values.get(i));
		}
		actionCount.put("ballRecovery", actionCount.get("ballRecovery")+1);
	}

	public void updateBallTouch(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = ballTouch.get(i);
			this.ballTouch.set(i, oldVal+values.get(i));
		}
		actionCount.put("ballTouch", actionCount.get("ballTouch")+1);
	}

	public void updateBallCarry(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = ballCarry.get(i);
			this.ballCarry.set(i, oldVal+values.get(i));
		}
		actionCount.put("ballCarry", actionCount.get("ballCarry")+1);
	}

	public void updatePass(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = pass.get(i);
			this.pass.set(i, oldVal+values.get(i));
		}
		actionCount.put("pass", actionCount.get("pass")+1);
	}

	public void updateLongPass(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = longPass.get(i);
			this.longPass.set(i, oldVal+values.get(i));
		}
		actionCount.put("longPass", actionCount.get("longPass")+1);
	}

	public void updateCross(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = cross.get(i);
			this.cross.set(i, oldVal+values.get(i));
		}
		actionCount.put("cross", actionCount.get("cross")+1);
	}

	public void updateFreekickPass(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = freekickPass.get(i);
			this.freekickPass.set(i, oldVal+values.get(i));
		}
		actionCount.put("freekickPass", actionCount.get("freekickPass")+1);
	}

	public void updateCorner(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = cornerTaken.get(i);
			this.cornerTaken.set(i, oldVal+values.get(i));
		}
		actionCount.put("cornerTaken", actionCount.get("cornerTaken")+1);
	}

	public void updateThrowIn(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = throwInTaken.get(i);
			this.throwInTaken.set(i, oldVal+values.get(i));
		}
		actionCount.put("throwInTaken", actionCount.get("throwInTaken")+1);
	}

	public void updateTakeOn(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = takeOn.get(i);
			this.takeOn.set(i, oldVal+values.get(i));
		}
		actionCount.put("takeOn", actionCount.get("takeOn")+1);
	}

	public void updateShot(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = shot.get(i);
			this.shot.set(i, oldVal+values.get(i));
		}
		actionCount.put("shot", actionCount.get("shot")+1);
	}
	public void updateHeadedShot(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = headedShot.get(i);
			this.headedShot.set(i, oldVal+values.get(i));
		}
		actionCount.put("headedShot", actionCount.get("headedShot")+1);
	}
	public void updateBallReceived(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = ballReceived.get(i);
			this.ballReceived.set(i, oldVal+values.get(i));
		}
		actionCount.put("ballReceived", actionCount.get("ballReceived")+1);
	}
	public void updateBlockedShot(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = blockedShot.get(i);
			this.blockedShot.set(i, oldVal+values.get(i));
		}
		actionCount.put("blockedShot", actionCount.get("blockedShot")+1);
	}	
	public void updateSavedShot(ArrayList<Double> values){
		for (int i=0; i<values.size(); i++){
			double oldVal = savedShot.get(i);
			this.savedShot.set(i, oldVal+values.get(i));
		}
		actionCount.put("savedShot", actionCount.get("savedShot")+1);
	}


	public ArrayList<Double> getBallReceived() {
		return ballReceived;
	}




	public ArrayList<Double> getBlockedShot() {
		return blockedShot;
	}



	public ArrayList<Double> getSavedShot() {
		return savedShot;
	}

	public void updateValue(String action, ArrayList<Double> values){
		if(action.equals("Pass")){
			this.updatePass(values);
		}
		else if(action.equals("Ball received")){
			this.updateBallReceived(values);
		}
		else if(action.equals("Long pass")){
			this.updateLongPass(values);
		}
		else if(action.equals("Ball carry")){
			this.updateBallCarry(values);
		}
		else if(action.equals("Ball recovery")){
			this.updateBallRecovery(values);
		}
		else if(action.equals("Aerial duel")){
			this.updateAerial(values);
		}
		else if(action.equals("Clearance")){
			this.updateClearance(values);
		}
		else if(action.equals("Throw in taken")){
			this.updateThrowIn(values);
		}
		else if(action.equals("Ball touch")){
			this.updateBallTouch(values);
		}
		else if(action.equals("Interception")){
			this.updateInterception(values);
		}
		else if(action.equals("Cross")){
			this.updateCross(values);
		}
		else if(action.equals("Tackle")){
			this.updateTackle(values);
		}
		else if(action.equals("Shot")){
			this.updateShot(values);
		}
		else if(action.equals("Headed shot")){
			this.updateHeadedShot(values);
		}
		else if(action.equals("Take on")){
			this.updateTakeOn(values);
		}
		else if(action.equals("Free kick pass")){
			this.updateFreekickPass(values);
		}
		else if(action.equals("Foul committed")){
			this.updateFoulCommitted(values);
		}
		else if(action.equals("Fouled")){
			this.updateFouled(values);
		}
		else if(action.equals("Dispossessed")){
			this.updateDispossessed(values);
		}
		else if(action.equals("Corner taken")){
			this.updateCorner(values);
		}
		else if(action.equals("Blocked shot")){
			this.updateBlockedShot(values);
		}
		else if(action.equals("Shot saved")){
			this.updateSavedShot(values);
		}

	}

	public ArrayList<Double> getTackle() {
		return tackle;
	}

	public ArrayList<Double> getClearance() {
		return clearance;
	}

	public ArrayList<Double> getAerialDuel() {
		return aerialDuel;
	}

	public ArrayList<Double> getDispossessed() {
		return dispossessed;
	}

	public ArrayList<Double> getFoulCommitted() {
		return foulCommitted;
	}

	public ArrayList<Double> getFouled() {
		return fouled;
	}

	public ArrayList<Double> getInterception() {
		return interception;
	}

	public ArrayList<Double> getBallRecovery() {
		return ballRecovery;
	}

	public ArrayList<Double> getBallTouch() {
		return ballTouch;
	}

	public ArrayList<Double> getBallCarry() {
		return ballCarry;
	}

	public ArrayList<Double> getPass() {
		return pass;
	}

	public ArrayList<Double> getLongPass() {
		return longPass;
	}

	public ArrayList<Double> getCross() {
		return cross;
	}

	public ArrayList<Double> getFreekickPass() {
		return freekickPass;
	}

	public ArrayList<Double> getCornerTaken() {
		return cornerTaken;
	}

	public ArrayList<Double> getThrowInTaken() {
		return throwInTaken;
	}

	public ArrayList<Double> getTakeOn() {
		return takeOn;
	}

	public ArrayList<Double> getShot() {
		return shot;
	}
	public ArrayList<Double> getHeadedShot() {
		return headedShot;
	}

	public ArrayList<Double> getTotal() {
		return total;
	}

	public void setTotal() {
		for (int i = 0; i < 8; i++){
			double totalVal = this.getAerialDuel().get(i)+this.getBallCarry().get(i)+this.getBallRecovery().get(i)+this.getBallTouch().get(i)+this.getClearance().get(i)+this.getCornerTaken().get(i)+this.getCross().get(i)+
					this.getDispossessed().get(i)+this.getFoulCommitted().get(i)+this.getFouled().get(i)+this.getFreekickPass().get(i)+this.getInterception().get(i)+this.getLongPass().get(i)+this.getPass().get(i)+this.getShot().get(i)+
					this.getTackle().get(i)+this.getTakeOn().get(i)+this.getThrowInTaken().get(i)+this.getBallReceived().get(i)+this.getBlockedShot().get(i)+this.getSavedShot().get(i)+this.getHeadedShot().get(i);
			this.total.set(i, totalVal);
		}		
	}
	public void setAverageActionValues(){
		for (int i = 0; i<4; i++){
			if(actionCount.get("tackle")!=0) tackle.add(tackle.get(i)/actionCount.get("tackle"));
			else tackle.add(0.0);
			
			if (actionCount.get("clearance")!= 0) clearance.add(clearance.get(i)/actionCount.get("clearance"));
			else clearance.add(0.0);
			
			if (actionCount.get("aerialDuel")!=0) aerialDuel.add(aerialDuel.get(i)/actionCount.get("aerialDuel"));
			else aerialDuel.add(0.0);
			
			if (actionCount.get("dispossessed")!=0) dispossessed.add(dispossessed.get(i)/actionCount.get("dispossessed"));
			else dispossessed.add(0.0);
			
			if (actionCount.get("foulCommitted")!=0) foulCommitted.add(foulCommitted.get(i)/actionCount.get("foulCommitted"));
			else foulCommitted.add(0.0);
			
			if (actionCount.get("fouled")!=0) fouled.add(fouled.get(i)/actionCount.get("fouled"));
			else fouled.add(0.0);
			
			if (actionCount.get("interception")!=0) interception.add(interception.get(i)/actionCount.get("interception"));
			else interception.add(0.0);
			
			if (actionCount.get("ballRecovery")!=0)	ballRecovery.add(ballRecovery.get(i)/actionCount.get("ballRecovery"));
			else ballRecovery.add(0.0);
			
			if (actionCount.get("ballTouch")!=0) ballTouch.add(ballTouch.get(i)/actionCount.get("ballTouch"));
			else ballTouch.add(0.0);
			
			if (actionCount.get("ballCarry")!=0) ballCarry.add(ballCarry.get(i)/actionCount.get("ballCarry"));
			else ballCarry.add(0.0);
			
			if (actionCount.get("pass")!=0) pass.add(pass.get(i)/actionCount.get("pass"));
			else pass.add(0.0);
			
			if (actionCount.get("longPass")!=0) longPass.add(longPass.get(i)/actionCount.get("longPass"));
			else longPass.add(0.0);
			
			if (actionCount.get("cross")!=0) cross.add(cross.get(i)/actionCount.get("cross"));
			else cross.add(0.0);
			
			if (actionCount.get("freekickPass")!=0) freekickPass.add(freekickPass.get(i)/actionCount.get("freekickPass"));
			else freekickPass.add(0.0);
			
			if (actionCount.get("cornerTaken")!=0) cornerTaken.add(cornerTaken.get(i)/actionCount.get("cornerTaken"));
			else cornerTaken.add(0.0);
			
			if (actionCount.get("throwInTaken")!=0) throwInTaken.add(throwInTaken.get(i)/actionCount.get("throwInTaken"));
			else throwInTaken.add(0.0);
			
			if (actionCount.get("takeOn")!=0) takeOn.add(takeOn.get(i)/actionCount.get("takeOn"));
			else takeOn.add(0.0);
			
			if (actionCount.get("shot")!=0) shot.add(shot.get(i)/actionCount.get("shot"));
			else shot.add(0.0);
			
			if (actionCount.get("headedShot")!=0) headedShot.add(headedShot.get(i)/actionCount.get("headedShot"));
			else headedShot.add(0.0);
			
			if (actionCount.get("ballReceived")!=0) ballReceived.add(ballReceived.get(i)/actionCount.get("ballReceived"));
			else ballReceived.add(0.0);
			
			if (actionCount.get("blockedShot")!=0) blockedShot.add(blockedShot.get(i)/actionCount.get("blockedShot"));
			else blockedShot.add(0.0);
			
			if (actionCount.get("savedShot")!=0) savedShot.add(savedShot.get(i)/actionCount.get("savedShot"));
			else savedShot.add(0.0);
		}
		this.setTotal();
	}
	@Override
	public String toString() {
		return "PlayerValues [playerID=" + playerID + ", gameID=" + gameID + ", teamID=" + teamID + ", tackle=" + tackle
				+ ", clearance=" + clearance + ", aerialDuel=" + aerialDuel + ", dispossessed=" + dispossessed
				+ ", foulCommitted=" + foulCommitted + ", fouled=" + fouled + ", interception=" + interception
				+ ", ballRecovery=" + ballRecovery + ", ballTouch=" + ballTouch + ", ballCarry=" + ballCarry + ", pass="
				+ pass + ", longPass=" + longPass + ", cross=" + cross + ", freekickPass=" + freekickPass
				+ ", cornerTaken=" + cornerTaken + ", throwInTaken=" + throwInTaken + ", takeOn=" + takeOn + ", shot="
				+ shot + ", headedShot=" + headedShot + ", ballReceived=" + ballReceived + ", total=" + total
				+ ", blockedShot=" + blockedShot + ", savedShot=" + savedShot + ", actionCount=" + actionCount + "]";
	}


}
