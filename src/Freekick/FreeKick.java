package Freekick;

public class FreeKick {

	@Override
	public String toString() {
		return "FreeKick [optaID=" + optaID + ", gameID=" + gameID + ", teamID=" + teamID + ", playerID=" + playerID
				+ ", inswing=" + inswing + ", xstart=" + xstart + ", ystart=" + ystart + ", xend=" + xend + ", yend="
				+ yend + ", goal=" + goal + ", shot=" + shot + "]";
	}
	public FreeKick(long optaID, int gameID, int teamID, int playerID, int inswing, float xstart, float ystart, float xend,
			float yend, int goal, int shot) {
		super();
		this.optaID = optaID;
		this.gameID = gameID;
		this.teamID = teamID;
		this.playerID = playerID;
		this.inswing = inswing;
		this.xstart = xstart;
		this.ystart = ystart;
		this.xend = xend;
		this.yend = yend;
		this.goal = goal;
		this.shot = shot;
	}
	public long getOptaID() {
		return optaID;
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
	public int getInswing() {
		return inswing;
	}
	public float getXstart() {
		return xstart;
	}
	public float getYstart() {
		return ystart;
	}
	public float getXend() {
		return xend;
	}
	public float getYend() {
		return yend;
	}
	public int getGoal() {
		return goal;
	}
	public int getShot() {
		return shot;
	}
	private long optaID;
	private int gameID;
	private int teamID;
	private int playerID;
	private int inswing;
	private float xstart;
	private float ystart;
	private float xend;
	private float yend;
	private int goal;
	private int shot;
	
	
}
