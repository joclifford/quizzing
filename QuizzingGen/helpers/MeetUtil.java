package helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXB;

import jaxb.classes.Church;
import jaxb.classes.Churches;
import jaxb.classes.Quiz;
import jaxb.classes.QuizMeet;
import jaxb.classes.Schedule;
import jaxb.classes.Slot;
import jaxb.classes.Team;

public class MeetUtil {
	public static boolean hasQuizzed(Set<String> matches, String team1, String team2) {
		// System.out.println("checking...." + team1.getId() + ":" +
		// team2.getId());
		for (String s : matches) {
			if (s.equals(team1 + ":" + team2)) {
				return true;
			} else if (s.equals(team2 + ":" + team1)) {
				return true;
			}
		}
		return false;
	}

	public static boolean doneQuizzing(HashMap<String, Integer> count, Team team, int max) {
		if (!count.containsKey(team.getId())) {
			return false;
		} else {
			return count.get(team.getId()) >= max;
		}
	}

	public boolean invalidQuiz(final int quizMax, Set<String> matches, HashMap<String, Integer> count, Team team1, Team team2, Team team3) {
		return doneQuizzing(count, team1, quizMax) || doneQuizzing(count, team2, quizMax) || doneQuizzing(count, team3, quizMax) || hasQuizzed(matches, team1, team2) || hasQuizzed(matches, team1, team3) || hasQuizzed(matches, team2, team3);
	}

	public static boolean hasQuizzed(Set<String> matches, Team team1, Team team2) {
		return hasQuizzed(matches, team1, team2, false);
	}

	public static boolean hasQuizzed(Set<String> matches, Team team1, Team team2, boolean canDup) {
		return hasQuizzed(matches, new HashSet<String>(), team1, team2, canDup);
	}

	public static boolean hasQuizzed(Set<String> matches, Set<String> matchesInThisQuiz, Team team1, Team team2, boolean canDup) {
		if (matches.contains(team1.getName() + ":" + team2.getName()) || matches.contains(team2.getName() + ":" + team1.getName())) {
			if (canDup && !matches.contains(team1.getName() + "::" + team2.getName()) && !matches.contains(team2.getName() + "::" + team1.getName())) {
				if (!matchesInThisQuiz.contains(team1.getName() + ":" + team2.getName()) || !matchesInThisQuiz.contains(team2.getName() + ":" + team1.getName()))
					return false;
			}
			return true;
		}
		return false;
	}

	public static boolean everyTeamQuizzed(HashMap<String, Integer> count, int max) {
		for (int i : count.values()) {
			if (i < max) {
				return false;
			}
		}

		return true;
	}

	public static Quiz getQuiz(HashMap<String, Integer> count, Set<String> matches, ArrayList<Team> teams, Slot slot) {
		return getQuiz(count, matches, new HashSet<String>(), teams, slot);
	}

	@SuppressWarnings("unchecked")
	public static Quiz getQuiz(HashMap<String, Integer> count, Set<String> matches, Set<String> matchesInThisQuiz, ArrayList<Team> teams, Slot slot) {
		List<Team> ts = (List<Team>) teams.clone();
		Collections.shuffle(ts);

		Iterator<Team> itr = ts.iterator();
		while (itr.hasNext()) {
			Team team = itr.next();
			if (doneQuizzing(count, team, 3)) {
				itr.remove();
			}
		}

		if (ts.size() < 3) {
			return null;
		} else {
			for (Team team : ts) {
				Quiz q = new Quiz();

				// Check to see if they can quiz another team.
				boolean canDup = canDupQuiz(matches);

				Team team2 = new Team();
				for (Team t : ts) {
					if (!hasQuizzed(matches, matchesInThisQuiz, team, t, canDup) && !TeamUtil.sameTwoTeams(team, t)) {
						team2 = t;
					}
				}
				
				if (canDup && hasQuizzed(matches, team, team2)){
					canDup = false;
				}

				Team team3 = new Team();
				for (Team t : ts) {
					if (!hasQuizzed(matches, matchesInThisQuiz, team, t, canDup) && !hasQuizzed(matches, matchesInThisQuiz, team2, t, canDup) && !sameThreeTeams(team, team2, t)) {
						team3 = t;
					}
				}

				if (team2 == null || team3 == null || team2.getId() == null || team3.getId() == null) {
					continue;
				}
				q.setTeam1(team.getId());
				q.setTeam2(team2.getId());
				q.setTeam3(team3.getId());
				return q;
			}
		}
		return null;
	}

	private static boolean canDupQuiz(Set<String> matches) {
		List<Team> teams = allTeams();
		boolean canDup = false;
		for (Team team : teams) {
			int teamsLeft = 0;
			for (Team t : teams) {
				if (!hasQuizzed(matches, team, t) && !TeamUtil.sameTwoTeams(team, t)) {
					teamsLeft++;
				}
			}

			if (teamsLeft <= 6)
				canDup = true;
		}

		return canDup;
	}

	private static boolean sameThreeTeams(Team team1, Team team2, Team team3) {
		try {
			if (team1.getId() != null && team2.getId() != null && team3.getId() != null) {
				if (team1.getId().equals(team2.getId())) {
					return true;
				} else if (team1.getId().equals(team3.getId())) {
					return true;
				} else if (team2.getId().equals(team3.getId())) {
					return true;
				}
			} else if (team1.getId() == null) {
				if (team2.getId() != null || team3.getId() != null) {
					return team2.getId().equals(team3.getId());
				} else {
					return false;
				}
			} else if (team2.getId() == null) {
				if (team3.getId() != null) {
					return team1.getId().equals(team3.getId());
				} else {
					return false;
				}
			} else if (team3.getId() == null) {
				return team1.getId().equals(team2.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public static boolean inSlot(Slot slot, String team1, String team2, String team3) {
		if (team1 == null || team2 == null || team3 == null)
			return false;

		for (Quiz s : slot.getQuiz()) {
			String t1 = s.getTeam1();
			String t2 = s.getTeam2();
			String t3 = s.getTeam3();

			if (team1.equals(t1) || team2.equals(t1) || team3.equals(t1)) {
				return true;
			}
			if (team1.equals(t2) || team2.equals(t2) || team3.equals(t2)) {
				return true;
			}

			if (team1.equals(t3) || team2.equals(t3) || team3.equals(t3)) {
				return true;
			}

		}
		return false;
	}

	public static boolean inSlot(Slot slot, String team) {
		for (Quiz s : slot.getQuiz()) {
			String t1 = s.getTeam1();
			String t2 = s.getTeam2();
			String t3 = s.getTeam3();

			if (team.equals(t1) || team.equals(t2) || team.equals(t3)) {
				return true;
			}
		}
		return false;
	}

	public static boolean inSlot(Slot slot, Quiz quiz) {
		if (slot == null || slot.getStart() == null || quiz == null) {
			return false;
		}
		return inSlot(slot, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());
	}

	public static boolean validateMeet(QuizMeet meet) {
		if (meet.getSlot().size() == 0) {
			return true;
		}
		boolean team1 = false;
		boolean team2 = false;
		boolean team3 = false;
		for (Slot slot : meet.getSlot()) {
			for (Quiz quiz : slot.getQuiz()) {
				team1 = quiz.getTeam1() == null || quiz.getTeam1() == "";
				team2 = quiz.getTeam2() == null || quiz.getTeam2() == "";
				team3 = quiz.getTeam3() == null || quiz.getTeam3() == "";
				if (!team1) {
					if (team2 || team3) {
						System.out.println("BAD MEET" + quiz.getTeam1() + quiz.getTeam2() + quiz.getTeam3());
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String getSuitableTeam(HashMap<String, Integer> count, Team team1, Team team2) {
		int highest = 0;
		for (String s : count.keySet()) {
			if (count.get(s) > highest) {
				highest = count.get(s);
			}
		}

		List<String> keys = new ArrayList<String>(count.keySet());
		Collections.shuffle(keys);
		for (String s : keys) {
			if (count.get(s) < highest || count.get(s) == 0 || count.get(s) < 3) {
				if (!s.equals(team1.getId()) && !s.equals(team2.getId())) {
					return s;
				}
			}
		}

		return null;
	}

	public static void checkForRepeatMatches(Schedule schedule) {
		HashSet<String> matches = new HashSet<String>();
		for (QuizMeet qm : schedule.getQuizMeet()) {
			for (Slot slot : qm.getSlot()) {
				for (Quiz q : slot.getQuiz()) {
					if (q.getTeam1() != null) {
						if (hasQuizzed(matches, q.getTeam1(), q.getTeam2()) || hasQuizzed(matches, q.getTeam1(), q.getTeam3()) || hasQuizzed(matches, q.getTeam2(), q.getTeam3())) {
							System.out.println("TEST" + q.getTeam1() + ":" + q.getTeam2() + ":" + q.getTeam3());
						}
						matches.add(q.getTeam1() + ":" + q.getTeam2());
						matches.add(q.getTeam1() + ":" + q.getTeam3());
						matches.add(q.getTeam2() + ":" + q.getTeam3());

					}
				}
			}
		}
	}

	public static void identifyTrips(List<Slot> quizzes, List<Team> teams, HashMap<Team, List<String>> trips) {
		trips.clear();
		for (Team team : teams) {
			for (int i = 2; i < quizzes.size(); i++) {
				if (inSlot(quizzes.get(i), team.getName())) {
					if (inSlot(quizzes.get(i - 1), team.getName())) {
						if (inSlot(quizzes.get(i - 2), team.getName())) {
							List<String> slots = new ArrayList<String>();
							for (int l = 0; l < quizzes.get(i).getQuiz().size(); l++) {
								if (quizzes.get(i).getQuiz().get(l).getTeam1() == null)
									continue;
								if (quizzes.get(i).getQuiz().get(l).getTeam1().equals(team.getName()) || quizzes.get(i).getQuiz().get(l).getTeam2().equals(team.getName()) || quizzes.get(i).getQuiz().get(l).getTeam3().equals(team.getName())) {
									slots.add(i + ":" + l);
								}
							}
							for (int l = 0; l < quizzes.get(i - 1).getQuiz().size(); l++) {
								if (quizzes.get(i - 1).getQuiz().get(l).getTeam1() == null)
									continue;
								if (quizzes.get(i - 1).getQuiz().get(l).getTeam1().equals(team.getName()) || quizzes.get(i - 1).getQuiz().get(l).getTeam2().equals(team.getName()) || quizzes.get(i - 1).getQuiz().get(l).getTeam3().equals(team.getName())) {
									slots.add((i - 1) + ":" + l);
								}
							}
							for (int l = 0; l < quizzes.get(i - 2).getQuiz().size(); l++) {
								if (quizzes.get(i - 2).getQuiz().get(l).getTeam1() == null)
									continue;
								if (quizzes.get(i - 2).getQuiz().get(l).getTeam1().equals(team.getName()) || quizzes.get(i - 2).getQuiz().get(l).getTeam2().equals(team.getName()) || quizzes.get(i - 2).getQuiz().get(l).getTeam3().equals(team.getName())) {
									slots.add((i - 2) + ":" + l);
								}
							}
							trips.put(team, slots);
						}
					}
				}
			}
		}
	}

	public static void sortTriples(List<Slot> quizzing, HashMap<Team, List<String>> trips) {
		tripLoop: for (Team team : trips.keySet()) {
			for (int i = 0; i < quizzing.size(); i++) {
				if (inSlot(quizzing.get(i), team.getName())) {
					// This is a slot containing a team with a triple quiz
					int quizNum = 0;
					Quiz quizToMove = null;
					for (int k = 0; k < quizzing.get(i).getQuiz().size(); k++) {
						if (quizzing.get(i).getQuiz().get(k).getTeam1() != null) {
							if (quizzing.get(i).getQuiz().get(k).getTeam1().equals(team.getName()) || quizzing.get(i).getQuiz().get(k).getTeam2().equals(team.getName()) || quizzing.get(i).getQuiz().get(k).getTeam3().equals(team.getName())) {
								quizNum = k;
								quizToMove = quizzing.get(i).getQuiz().get(k);
							}
						}
					}

					// now looking for a slot that this team is not in.
					for (int j = 0; j < quizzing.size(); j++) {
						for (String s : trips.get(team)) {
							if (s.contains(j + ":"))
								continue;
						}
						if (!inSlot(quizzing.get(j), quizToMove)) {
							if (quizzing.get(j).getQuiz().size() < 6) {
								quizzing.get(j).getQuiz().add(quizToMove);
								quizzing.get(i).getQuiz().remove(quizNum);
								System.out.println("MOVED A QUIZ TO A VACANT SPOT");
								continue tripLoop;
							}

							for (int q = 0; q < quizzing.get(j).getQuiz().size(); q++) {
								if (quizzing.get(j).getQuiz().get(q).getTeam1() == null)
									continue;
								if (!inSlot(quizzing.get(i), quizzing.get(j).getQuiz().get(q))) {
									// move trip quiz into new slot
									quizzing.get(j).getQuiz().add(quizToMove);
									quizzing.get(i).getQuiz().remove(quizNum);

									// move other quiz into trip slot
									quizzing.get(i).getQuiz().add(quizzing.get(j).getQuiz().get(q));
									quizzing.get(j).getQuiz().remove(q);

									System.out.println("SWAPPED QUIZZES");
									continue tripLoop;
								}
							}

						}
					}
				}
			}
		}
	}

	public static void updateCount(HashMap<String, Integer> count, Team team1, Team team2, Team team3) {
		if (!count.containsKey(team1.getId())) {
			count.put(team1.getId(), 1);
		} else {
			int num = count.get(team1.getId());
			count.put(team1.getId(), num + 1);
		}

		if (!count.containsKey(team2.getId())) {
			count.put(team2.getId(), 1);
		} else {
			int num = count.get(team2.getId());
			count.put(team2.getId(), num + 1);
		}

		if (!count.containsKey(team3.getId())) {
			count.put(team3.getId(), 1);
		} else {
			int num = count.get(team3.getId());
			count.put(team3.getId(), num + 1);
		}
	}

	public static void updateCount(HashMap<String, Integer> count, String team1, String team2, String team3) {
		if (team1 == "" || team2 == "" || team3 == "" || team1 == null || team2 == null || team3 == null)
			return;

		if (!count.containsKey(team1)) {
			count.put(team1, 1);
		} else {
			int num = count.get(team1);
			count.put(team1, num + 1);
		}

		if (!count.containsKey(team2)) {
			count.put(team2, 1);
		} else {
			int num = count.get(team2);
			count.put(team2, num + 1);
		}

		if (!count.containsKey(team3)) {
			count.put(team3, 1);
		} else {
			int num = count.get(team3);
			count.put(team3, num + 1);
		}
	}

	public static void printCount(HashMap<String, Integer> count) {
		for (String s : count.keySet()) {
			if (s != null) {
				System.out.print(s + " : " + count.get(s) + "\n");
			}
		}
	}

	public static int amountRemaining(HashMap<String, Integer> count, List<Team> teamList) {
		int amountRemaining = 0;

		for (Integer i : count.values()) {
			if (i < 3) {
				amountRemaining++;
			}
		}
		return amountRemaining;

	}

	public static boolean allTeamsQuizzed(Set<String> matches, List<Team> teams) {
		HashMap<String, List<String>> teamMatches = new HashMap<String, List<String>>();

		for (Team team : teams) {
			teamMatches.put(team.getName(), new ArrayList<String>());
			for (Team t : teams) {
				if (!t.getId().equals(team.getId())) {
					teamMatches.get(team.getName()).add(t.getName());
				}
			}
		}

		for (String match : matches) {
			String m[] = match.split(":");
			if (!m[0].equals("null") && !m[1].equals("null")) {
				teamMatches.get(m[0]).remove(m[1]);
				teamMatches.get(m[1]).remove(m[0]);
			}

		}
		for (List<String> list : teamMatches.values()) {
			if (list.size() > 0) {
				return false;
			}
		}

		return true;
	}

	public boolean stillQuizzes(HashMap<String, Integer> count, List<Team> teams, Set<String> matches, Team team1, Team team2, Team team3) {
		HashMap<String, Integer> testCount = new HashMap<String, Integer>();

		Map<String, List<String>> matchups = getMatchups(teams, matches, testCount);

		for (Team team : teams) {
			if (matchups.get(team.getName()).size() == 0) {
				return false;
			}
		}
		return true;
	}

	public static Map<String, List<String>> getMatchups(List<Team> teams, Set<String> matches, HashMap<String, Integer> count) {
		Map<String, List<String>> teamMatchups = new HashMap<String, List<String>>();

		for (Team team : teams) {
			List<String> matchups = new ArrayList<String>();
			for (Team t : teams) {
				if (!TeamUtil.sameTwoTeams(team, t)) {
					if (!hasQuizzed(matches, team.getName(), t.getName()) && !doneQuizzing(count, t, 3)) {
						matchups.add(t.getName());
					}
				}
			}
			teamMatchups.put(team.getName(), matchups);
		}
		return teamMatchups;
	}

	public static void sortSlots(List<Slot> slots, int roomSize) {
		// Assuming that the first slot in the morning and the last slot in the
		// afternoon is blank we don't need to organize that column. Therefore,
		// start at 1.

		List<Integer> rooms = new ArrayList<Integer>();
		for (int i = 1; i < roomSize; i++) {
			rooms.add(i);
		}

		for (int i = 1; i <= slots.size(); i++) {
			Iterator<Integer> itr = rooms.iterator();
			while (itr.hasNext()) {
				int j = itr.next();
				try {
					if (MeetUtil.inSlot(slots.get(i + 1), slots.get(i).getQuiz().get(j))) {
						if (!MeetUtil.inSlot(slots.get(slots.size() - 1), slots.get(i).getQuiz().get(j)) && !MeetUtil.inSlot(slots.get(slots.size() - 2), slots.get(i).getQuiz().get(j))) {
							Quiz quiz = slots.get(i).getQuiz().get(j);
							System.out.println("Moving quiz :  " + quiz.getTeam1() + ":" + quiz.getTeam2() + ":" + quiz.getTeam3());
							slots.get(i).getQuiz().remove(j);
							slots.get(slots.size() - 1).getQuiz().add(quiz);
							itr.remove();
						}
					}
				} catch (Exception e) {

				}
			}
		}
	}

	public static boolean checkCount(HashMap<String, Integer> count) {
		boolean allGood = true;
		for (Integer i : count.values()) {
			if (i < 3) {
				allGood = false;
			}
		}
		return allGood;
	}

	public static boolean refillTeamsIfNeeded(Set<String> matches, HashMap<String, Integer> count, ArrayList<Team> teams, ArrayList<Team> teamsToQuiz, Slot slot) {
		if (MeetUtil.getQuiz(count, matches, teamsToQuiz, slot) == null) {
			teamsToQuiz.clear();
			teamsToQuiz.addAll(teams);
			return true;
		}
		return false;
	}

	public static List<Team> allTeams() {
		File file = new File("data/ChurchListing.xml");
		Churches churches = JAXB.unmarshal(file, Churches.class);

		List<Team> teams = new ArrayList<Team>();
		for (Church c : churches.getChurch()) {
			for (Team t : c.getTeam()) {
				teams.add(t);
			}
		}

		return teams;
	}

}
