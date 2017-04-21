package master17;

import java.util.ArrayList;
import java.util.List;

public class StartingEleven {
	private int teamID;
	private int gameID;
	private List<String> players;
	public StartingEleven(int teamID, int gameID, List<String> players) {
		super();
		this.teamID = teamID;
		this.gameID = gameID;
		this.players = players;
	}
	public int getTeamID() {
		return teamID;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	public int getGameID() {
		return gameID;
	}
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}
	public List<String> getPlayers() {
		return players;
	}
	public void setPlayers(List<String> players) {
		this.players = players;
	}
	@Override
	public String toString() {
		return "StartingEleven [teamID=" + teamID + ", gameID=" + gameID + ", players=" + players + "]";
	}
}
