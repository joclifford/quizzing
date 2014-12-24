package helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import jaxb.classes.Team;

public class TeamHelper {
	private String id;
	private JTextField teamName;
	private JTextField coach;
	@SuppressWarnings("rawtypes")
	private JComboBox rating;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTeamName() {
		return teamName.getText();
	}

	public void setTeamName(JTextField teamName) {
		this.teamName = teamName;
	}

	public String getCoach() {
		return coach.getText();
	}

	public void setCoach(JTextField coach) {
		this.coach = coach;
	}

	public String getRating() {
		return rating.getSelectedItem().toString();
	}

	public void setRating(@SuppressWarnings("rawtypes") JComboBox rating) {
		this.rating = rating;
	}

	public TeamHelper(String id, JTextField teamName, JTextField coach, @SuppressWarnings("rawtypes") JComboBox rating) {
		super();
		this.teamName = teamName;
		this.coach = coach;
		this.rating = rating;
		this.id = id;


	}

	public Team getTeamObject() {
		Team team = new Team();
		team.setId(getId());
		team.setName(getTeamName());
		team.setCoach(getCoach());
		team.setRating(getRating());

		return team;
	}


}
