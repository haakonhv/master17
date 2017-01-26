package master17;


public class Event {
	private int event_id;
	private String action_type;
	private int team_id;
	private int player_id;
	private float xstart;
	private float ystart;
	private int number;
	private int sequence;
	private int game_id;
	private int period;
	private int manpowerdifference;
	private int goaldifference;
	private int minute;
	private int second;

	public int getEvent_id() {
		return event_id;
	}

	public String getAction_type() {
		return action_type;
	}

	public int getTeam_id() {
		return team_id;
	}

	public int getPlayer_id() {
		return player_id;
	}

	public float getXstart() {
		return xstart;
	}

	public float getYstart() {
		return ystart;
	}

	public int getNumber() {
		return number;
	}

	public int getSequence() {
		return sequence;
	}

	public int getGame_id() {
		return game_id;
	}

	public int getPeriod() {
		return period;
	}

	public int getManpowerdifference() {
		return manpowerdifference;
	}

	public int getGoaldifference() {
		return goaldifference;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public String toString() {
		return "Event [event_id=" + event_id + ", action_type=" + action_type + ", team_id=" + team_id + ", player_id="
				+ player_id + ", xstart=" + xstart + ", ystart=" + ystart + ", number=" + number + ", sequence="
				+ sequence + ", game_id=" + game_id + ", period=" + period + ", manpowerdifference="
				+ manpowerdifference + ", goaldifference=" + goaldifference + ", minute=" + minute + ", second="
				+ second + "]";
	}

	public Event(int event_id, String action_type, int team_id, int player_id, float xstart, float ystart, int number,
			int sequence, int game_id, int period, int minute, int second, int manpowerdifference, int goaldifference) {
		super();
		this.event_id = event_id;
		this.action_type = action_type;
		this.team_id = team_id;
		this.player_id = player_id;
		this.xstart = xstart;
		this.ystart = ystart;
		this.number = number;
		this.sequence = sequence;
		this.game_id = game_id;
		this.period = period;
		this.minute = minute;
		this.second = second;
		this.manpowerdifference = manpowerdifference;
		this.goaldifference = goaldifference;
	}

}
