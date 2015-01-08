package helpers;

import java.util.ArrayList;
import java.util.List;

import jaxb.classes.Team;

public class Match {
	List<Team> teams = new ArrayList<Team>(3);
	int row = -1;
	int slot = -1;
	boolean executed = false;

	public Match(int r, int s, Team t1, Team t2, Team t3) {
		row = r;
		slot = s;
		teams.add(t1);
		teams.add(t2);
		teams.add(t3);
	}

	public Match(Team t1, Team t2, Team t3) {
		this(-1, -1, t1, t2, t3);
	}

	public String toString() {
		return "" + getRow() + ":" + getSlot() + "  " + teams.get(0).getName()
				+ ", " + teams.get(1).getName() + ", " + teams.get(2).getName()
				+ (isExecuted() ? " [COMPLETED]" : "");
	}

	public boolean isExecuted() {
		return executed;
	}

	public Team getTeam(int i) {
		return teams.get(i);
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	

}
