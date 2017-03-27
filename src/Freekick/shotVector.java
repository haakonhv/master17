package Freekick;

public class shotVector {
	private int goal;
	private int directFK;
	private int corner;
	private int intentional_assist;
	private int penalty;
	private int volley;
	private int header;
	private int fastBreak;
	private int followsDribble;
	private float distance;
	private float angle;

	public shotVector(int goal) {
		super();
		this.goal = goal;
		directFK = 0;
		corner = 0;
		intentional_assist = 0;
		penalty = 0;
		volley = 0;
		header = 0;
		fastBreak = 0;
		followsDribble = 0;
		
	}
	public int getGoal() {
		return goal;
	}
	public void setGoal(int goal) {
		this.goal = goal;
	}
	public int getDirektFK() {
		return directFK;
	}
	public void setDirektFK(int direktFK) {
		this.directFK = direktFK;
	}
	public int getCorner() {
		return corner;
	}
	public void setCorner(int corner) {
		this.corner = corner;
	}
	public int getIntentional_assist() {
		return intentional_assist;
	}
	public void setIntentional_assist(int intentional_assist) {
		this.intentional_assist = intentional_assist;
	}
	public int getPenalty() {
		return penalty;
	}
	public void setPenalty(int penalty) {
		this.penalty = penalty;
	}
	public int getVolley() {
		return volley;
	}
	public void setVolley(int volley) {
		this.volley = volley;
	}
	public int getDirectFK() {
		return directFK;
	}
	public void setDirectFK(int directFK) {
		this.directFK = directFK;
	}
	public int getHeader() {
		return header;
	}
	public void setHeader(int header) {
		this.header = header;
	}
	public int getFastBreak() {
		return fastBreak;
	}
	public void setFastBreak(int fastBreak) {
		this.fastBreak = fastBreak;
	}
	public int getFollowsDribble() {
		return followsDribble;
	}
	public void setFollowsDribble(int followsDribble) {
		this.followsDribble = followsDribble;
	}
	
	

}
