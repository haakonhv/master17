package master17;

public class GameOutcome {
	int gameID;
	double homep;
	double drawp;
	double awayp;
	public int getGameID() {
		return gameID;
	}
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}
	public double getHomep() {
		return homep;
	}
	public void setHomep(double homep) {
		this.homep = homep;
	}
	public double getDrawp() {
		return drawp;
	}
	public void setDrawp(double drawp) {
		this.drawp = drawp;
	}
	public double getAwayp() {
		return awayp;
	}
	public void setAwayp(double awayp) {
		this.awayp = awayp;
	}
	public GameOutcome(int gameID, double homep, double drawp, double awayp) {
		super();
		this.gameID = gameID;
		this.homep = homep;
		this.drawp = drawp;
		this.awayp = awayp;
	}
	@Override
	public String toString() {
		return "GameOutcome [gameID=" + gameID + ", homep=" + homep + ", drawp=" + drawp + ", awayp=" + awayp + "]";
	}
	
	

}
