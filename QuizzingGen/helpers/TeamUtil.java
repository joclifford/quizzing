package helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jaxb.classes.Team;

public class TeamUtil {

	public Team getRandomTeam(List<Team> teams) {
		int num = ((int) ((Math.random()) * teams.size()));
		Team team = teams.get(num);
		while (team == null) {
			num = ((int) ((Math.random()) * teams.size()));
			team = teams.get(num);
		}
		return team;

	}
	
	public static boolean sameTwoTeams(Team team1, Team team2) {
		if (team1.getId() == null) {
			return false;
		} else if (team2.getId() == null) {
			return false;
		} else {
			return team1.getId().equals(team2.getId());
		}
	}
	
	private String getSuitableTeam(HashMap<String, Integer> count, Team team1, Team team2, int max, Set<String> matches) {
		int highest = 0;
		for (String s : count.keySet()) {
			if (count.get(s) > highest) {
				highest = count.get(s);
			}
		}

		List<String> keys = new ArrayList<String>(count.keySet());
		Collections.shuffle(keys);
		for (String s : keys) {
			if ((count.get(s) < highest && count.get(s) < max) || count.get(s) == 0) {
				if (!s.equals(team1.getId()) && !s.equals(team2.getId())) {
					if (!MeetUtil.hasQuizzed(matches, s, team1.getId()) && !MeetUtil.hasQuizzed(matches, s, team2.getId()))
						return s;
				}
			}
		}

		return null;
	}

}
