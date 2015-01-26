package helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXB;

import jaxb.classes.Church;
import jaxb.classes.Churches;
import jaxb.classes.Quiz;
import jaxb.classes.QuizMeet;
import jaxb.classes.Schedule;
import jaxb.classes.Schedules;
import jaxb.classes.Slot;
import jaxb.classes.Team;

public class Possibilities {
	private static int teamCount = 37;

	File file = new File("data/ChurchListing.xml");
	File meetFile = new File("data/MeetListing.xml");
	File scheduleFile = new File("data/ScheduleListing.xml");

	List<Team> teams;
	Map<Team, HashSet<Team>> matched;
	List<Match> matches;
	List<Match> ms = new ArrayList<Match>();
	Map<Team, Integer> teamCounts;

	public static void main(String[] args) {
		Possibilities p = new Possibilities();
//		List<Match> matches = p.findPossibilities();
	}

	public List<Match> findPossibilities(List<Slot> alreadyHappened) {
		Churches churches = JAXB.unmarshal(file, Churches.class);
		// Meets meets = JAXB.unmarshal(meetFile, Meets.class);

		boolean done = false;
		while (!done) {
			ms.clear();
			teams = new ArrayList<Team>();
			matched = new HashMap<Team, HashSet<Team>>();
			teamCounts = new HashMap<Team, Integer>();
			int iterationsToCompletion = 0;

			for (Church church : churches.getChurch()) {
				for (Team team : church.getTeam()) {
					teams.add(team);
					teamCounts.put(team, 0);
					matched.put(team, new HashSet<Team>());
				}
			}

			matches = getExistingMatches(teams, alreadyHappened);
			// for (Match match : matches) {
			// System.out.println(match);
			// }

			teamCount = teams.size();
			for (int i = 0; i < (teamCount * teamCount * teamCount); i++) {
				Random r = new Random();
				Integer tid1 = r.nextInt(teamCount);
				Integer tid2 = r.nextInt(teamCount);
				Integer tid3 = r.nextInt(teamCount);

				// System.err.println(team1+", "+team2+", "+team3);
				if (tid1.equals(tid2) || tid1.equals(tid3) || tid2.equals(tid3))
					continue;

				Team team1 = teams.get(tid1);
				Team team2 = teams.get(tid2);
				Team team3 = teams.get(tid3);

				HashSet<Team> team1set = matched.get(team1);
				HashSet<Team> team2set = matched.get(team2);
				HashSet<Team> team3set = matched.get(team3);
				if (team1set.contains(team2) || team1set.contains(team3))
					continue;
				if (team2set.contains(team1) || team2set.contains(team3))
					continue;
				if (team3set.contains(team1) || team3set.contains(team2))
					continue;

				// new match possibility found
				team1set.add(team2);
				team1set.add(team3);
				team2set.add(team1);
				team2set.add(team3);
				team3set.add(team1);
				team3set.add(team2);

				teamCounts.put(team1, teamCounts.get(team1).intValue() + 1);
				teamCounts.put(team2, teamCounts.get(team2).intValue() + 1);
				teamCounts.put(team3, teamCounts.get(team3).intValue() + 1);

				Match match = new Match(team1, team2, team3);
				matches.add(match);
				ms.add(match);
				iterationsToCompletion = i;
				// System.out.println(counter(i) + ": " + match);

			}

			validate(matches);

			done = (37 == scheduleRemaining(14, matches, teams));
		}

		int min = Integer.MAX_VALUE;
		int max = 0;
		int total = 0;
		for (Team team : teams) {
			total += teamCounts.get(team);
			if (teamCounts.get(team) < min)
				min = teamCounts.get(team);
			if (teamCounts.get(team) > max)
				max = teamCounts.get(team);
		}
		System.out.println("Total: " + matches.size() + " matches");
		System.out.println("Minimum/Maximum matches for any team: " + min + " / " + max);
		// System.out.println("Averages matches/team: "
		// + (((double) total) / teamCount) + "  ("
		// + iterationsToCompletion + " iterations)");

		return matches;
	}

	private List<Match> getExistingMatches(List<Team> teams, List<Slot> alreadyHappened) {
		int r = 0;
		int s = 0;
		List<Match> matches = new ArrayList<Match>();
		Schedules schedules = JAXB.unmarshal(scheduleFile, Schedules.class);
		for (Schedule schedule : schedules.getSchedule()) {
			for (QuizMeet quizMeet : schedule.getQuizMeet()) {
				quizMeet.getSlot().addAll(alreadyHappened);
				for (Slot slot : quizMeet.getSlot()) {
					r++;
					s = 0;
					for (Quiz quiz : slot.getQuiz()) {
						s++;
						if (quiz.getTeam1() == null)
							continue;
						Team team1 = findTeam(quiz.getTeam1(), teams);
						Team team2 = findTeam(quiz.getTeam2(), teams);
						Team team3 = findTeam(quiz.getTeam3(), teams);
						teamCounts.put(team1, teamCounts.get(team1).intValue() + 1);
						teamCounts.put(team2, teamCounts.get(team2).intValue() + 1);
						teamCounts.put(team3, teamCounts.get(team3).intValue() + 1);

						HashSet<Team> team1set = matched.get(team1);
						HashSet<Team> team2set = matched.get(team2);
						HashSet<Team> team3set = matched.get(team3);
						team1set.add(team2);
						team1set.add(team3);
						team2set.add(team1);
						team2set.add(team3);
						team3set.add(team1);
						team3set.add(team2);

						Match match = new Match(r, s, team1, team2, team3);
						matches.add(match);
					}
				}
			}
		}
		return matches;
	}

	public int scheduleRemaining(int startOffset, List<Match> matches, List<Team> teams) {
		// process 1 section
		Map<Team, Integer> count = new HashMap<Team, Integer>();
		for (Team team : teams) {
			count.put(team, 0);
		}

		Match[][] grid = new Match[7][6];
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 6; j++)
				grid[i][j] = null;
		grid[0][0] = new Match(null, null, null);

		Set<Team> scheduled = new HashSet<Team>();

		int x = 0;
		for (Team team : teams) {
			// System.out.println("Searching for matches for " +
			// team.getName());
			List<Match> candidates = new ArrayList<Match>();
			for (Match match : matches) {
				Team team1 = match.getTeam(0);
				Team team2 = match.getTeam(1);
				Team team3 = match.getTeam(2);

				if (match.getRow() != -1)
					continue;
				if (!(team.equals(team1) || team.equals(team2) || team.equals(team3)))
					continue;
				// if (scheduled.contains(team1) ||
				// scheduled.contains(team2)
				// || scheduled.contains(team3))
				// continue;
				if (count.get(team1) > 2 || count.get(team2) > 2 || count.get(team3) > 2)
					continue;

				candidates.add(match);
			}
			int num = count.get(team);
			for (int i = 0; i < 3 - num; i++) {
				Match bestFit = null;
				int bestScore = 0;
				for (Match match : candidates) {
					if (match.getRow() != -1)
						continue;

					int score = 1;
					for (int j = 0; j < 3; j++) {
						if (!match.getTeam(j).equals(team))
							score *= count.get(match.getTeam(j)) + 1;
					}

					if (bestFit == null) {
						bestFit = match;
						bestScore = score;
						continue;
					} else if (score < bestScore) {
						bestFit = match;
						bestScore = score;
					}
				}

				if (bestFit == null) {
					// System.out.println("No options available...");
					break;
				}

				Team team1 = bestFit.getTeam(0);
				Team team2 = bestFit.getTeam(1);
				Team team3 = bestFit.getTeam(2);

				count.put(team1, count.get(team1) + 1);
				count.put(team2, count.get(team2) + 1);
				count.put(team3, count.get(team3) + 1);

				bestFit.setRow(++x);
				bestFit.setSlot(99);

				System.out.println("New Match: " + bestFit);

			}
		}

		// for (Team team : count.keySet()) {
		// System.out.println(team.getName() + ": " + count.get(team)
		// + " matches");
		// }
		return x;

	}

	private Team findTeam(String t, List<Team> teams) {
		for (Team team : teams) {
			if (team.getName().equals(t))
				return team;
		}
		return null;
	}

	private void validate(List<Match> matches) {
		Schedules schedules = JAXB.unmarshal(scheduleFile, Schedules.class);
		int quizCounter = 0;
		int matchCounter = 0;

		for (Schedule schedule : schedules.getSchedule()) {
			for (QuizMeet quizMeet : schedule.getQuizMeet()) {
				for (Slot slot : quizMeet.getSlot()) {
					for (Quiz quiz : slot.getQuiz()) {
						if (quiz.getTeam1() == null)
							continue;
						quizCounter++;
						for (Match match : matches) {
							String team1 = match.getTeam(0).getName();
							String team2 = match.getTeam(1).getName();
							String team3 = match.getTeam(2).getName();
							if (team1.equals(quiz.getTeam1()) || team1.equals(quiz.getTeam2()) || team1.equals(quiz.getTeam3())) {
								if (team2.equals(quiz.getTeam1()) || team2.equals(quiz.getTeam2()) || team2.equals(quiz.getTeam3())) {
									if (team3.equals(quiz.getTeam1()) || team3.equals(quiz.getTeam2()) || team3.equals(quiz.getTeam3())) {
										match.setExecuted(true);
										matchCounter++;
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		// System.out.println("Quizzes already run: " + quizCounter);
		// System.out.println("Matched quizzes: " + matchCounter);
	}

	public List<Match> getMs() {
		return ms;
	}

	public void setMs(List<Match> ms) {
		this.ms = ms;
	}

	public static int getTeamCount() {
		return teamCount;
	}

	public static void setTeamCount(int teamCount) {
		Possibilities.teamCount = teamCount;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getMeetFile() {
		return meetFile;
	}

	public void setMeetFile(File meetFile) {
		this.meetFile = meetFile;
	}

	public File getScheduleFile() {
		return scheduleFile;
	}

	public void setScheduleFile(File scheduleFile) {
		this.scheduleFile = scheduleFile;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public Map<Team, HashSet<Team>> getMatched() {
		return matched;
	}

	public void setMatched(Map<Team, HashSet<Team>> matched) {
		this.matched = matched;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	public Map<Team, Integer> getTeamCounts() {
		return teamCounts;
	}

	public void setTeamCounts(Map<Team, Integer> teamCounts) {
		this.teamCounts = teamCounts;
	}

	private String counter(int i) {
		String result = "0000000" + i;
		return result.substring(result.length() - 7);
	}

}
