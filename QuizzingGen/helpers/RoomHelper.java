package helpers;

import javax.swing.JTextField;

import jaxb.classes.Room;

public class RoomHelper {
	private String id;
	private JTextField name;
	private JTextField quizMasters;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name.getText();
	}

	public void setName(JTextField name) {
		this.name = name;
	}

	public String getQuizMasters() {
		return quizMasters.getText();
	}

	public void setQuizMasters(JTextField quizMasters) {
		this.quizMasters = quizMasters;
	}

	public Room getRoomObject() {
		Room room = new Room();
		room.setId(getId());
		room.setName(getName());
		room.setQuizmasters(getQuizMasters());
		return room;
	}

	public RoomHelper(String id, JTextField name, JTextField quizMasters) {
		super();
		this.id = id;
		this.name = name;
		this.quizMasters = quizMasters;
	}

}
