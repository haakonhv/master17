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
	private int cross;
	private int pullBack;
	private int throughBall;
	private int secondThrough;
	private double distance;
	private double angle;
	
	

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
		cross = 0;
		pullBack = 0;
		throughBall = 0;
		secondThrough = 0;
	}
	@Override
	public String toString() {
		return "shotVector [goal=" + goal + ", directFK=" + directFK + ", corner=" + corner + ", intentional_assist="
				+ intentional_assist + ", penalty=" + penalty + ", volley=" + volley + ", header=" + header
				+ ", fastBreak=" + fastBreak + ", followsDribble=" + followsDribble + ", cross=" + cross + ", pullBack="
				+ pullBack + ", throughBall=" + throughBall + ", secondThrough=" + secondThrough + ", distance="
				+ distance + ", angle=" + angle + "]";
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
	public int getCross() {
		return cross;
	}
	public void setCross(int cross) {
		this.cross = cross;
	}
	public int getPullBack() {
		return pullBack;
	}
	public void setPullBack(int pullBack) {
		this.pullBack = pullBack;
	}
	public int getThroughBall() {
		return throughBall;
	}
	public void setThroughBall(int throughBall) {
		this.throughBall = throughBall;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public int getSecondThrough() {
		return secondThrough;
	}
	public void setSecondThrough(int secondThrough) {
		this.secondThrough = secondThrough;
	}
	
	

}
