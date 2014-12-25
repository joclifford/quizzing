package helpers;

import javax.swing.JSpinner;
import javax.swing.JTextField;

import jaxb.classes.Meet;

public class MeetHelper {
	private String id;
	private JTextField location;
	private JTextField date;
	private JSpinner start;
	private JSpinner end;

	public String getStart() {
		return start.getValue().toString();
	}

	public void setStart(JSpinner start) {
		this.start = start;
	}

	public String getEnd() {
		return end.getValue().toString();
	}

	public void setEnd(JSpinner end) {
		this.end = end;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocation() {
		return location.getText();
	}

	public void setLocation(JTextField location) {
		this.location = location;
	}

	public String getDate() {
		return date.getText();
	}

	public void setDate(JTextField date) {
		this.date = date;
	}

	public Meet getMeetObject() {
		Meet meet = new Meet();
		meet.setId(getId());
		meet.setLocation(getLocation());
		meet.setDate(getDate());
		meet.setStart(getStart());
		meet.setEnd(getEnd());
		return meet;

	}

	public MeetHelper(String id, JTextField location, JTextField date,
			JSpinner start, JSpinner end) {
		super();
		this.id = id;
		this.location = location;
		this.date = date;
		this.start = start;
		this.end = end;
	}

}
