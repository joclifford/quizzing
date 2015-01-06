package screens;

import helpers.MeetHelper;
import helpers.RoomHelper;
import helpers.TeamHelper;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import jaxb.classes.Church;
import jaxb.classes.Churches;
import jaxb.classes.Meet;
import jaxb.classes.Meets;
import jaxb.classes.Quiz;
import jaxb.classes.QuizMeet;
import jaxb.classes.Room;
import jaxb.classes.Schedule;
import jaxb.classes.Schedules;
import jaxb.classes.Slot;
import jaxb.classes.Team;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class MainScreen {
	Churches churchs = new Churches();
	Meets meets = new Meets();
	Schedules schedules = new Schedules();

	JTabbedPane churchTabbedPane = new JTabbedPane(JTabbedPane.TOP);
	JTabbedPane meetTabbedPane = new JTabbedPane(JTabbedPane.TOP);

	JLabel lblStatus;

	Map<String, JTextField> churchTxts = new HashMap<String, JTextField>();
	Map<String, TeamHelper> teamValues = new HashMap<String, TeamHelper>();
	Map<String, MeetHelper> meetValues = new HashMap<String, MeetHelper>();
	Map<String, RoomHelper> roomValues = new HashMap<String, RoomHelper>();

	HashMap<String, List<Team>> teamMatches = null;
	List<Team> teams = null;

	File file = new File("data/ChurchListing.xml");
	File meetFile = new File("data/MeetListing.xml");
	File scheduleFile = new File("data/ScheduleListing.xml");

	String[] startTimes = { "9:00", "9:30", "10:00", "10:30" };
	String[] endTimes = { "15:00", "15:30", "16:00", "16:30", "17:00", "17:30" };

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					MainScreen window = new MainScreen();
					window.frame.setVisible(true);
					window.frame.setSize(1000, 700);
					window.frame.setTitle("Quizzing Schedule");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainScreen() {
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setLayout(null);

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("File");
		MenuItem mi = new MenuItem("Save");
		menu.add(mi);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveChurchs(false);
				saveMeets();
			}
		});
		menuBar.add(menu);

		frame.setMenuBar(menuBar);

		// ///////////////////////////////////////////////////////////////////////////////////
		// /// Generate the churches from the xml file
		// ///////////////////////////////////////////////////////////////////////////////////

		churchs = JAXB.unmarshal(file, Churches.class);
		meets = JAXB.unmarshal(meetFile, Meets.class);
		schedules = JAXB.unmarshal(scheduleFile, Schedules.class);

		generateChurchTabs(churchs);
		generateMeetTabs(meets);

	}

	private void generateMeetTabs(Meets meets2) {
		meetTabbedPane.setBounds(10, 329, 923, 230);
		frame.getContentPane().add(meetTabbedPane);

		JLabel lblNewLabel = new JLabel("Meets");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 19));
		lblNewLabel.setBounds(10, 303, 145, 15);
		frame.getContentPane().add(lblNewLabel);

		for (Meet m : meets.getMeet()) {
			JPanel meetPanel = new JPanel();
			meetPanel.setLayout(null);
			meetTabbedPane.addTab(m.getLocation(), null, meetPanel, "");

			JLabel lblName = new JLabel("Name: ");
			lblName.setBounds(0, 6, 34, 14);
			meetPanel.add(lblName);

			JTextField locationTxtField = new JTextField(m.getLocation());
			locationTxtField.setBounds(54, 3, 170, 20);
			meetPanel.add(locationTxtField);
			locationTxtField.setColumns(10);

			JLabel lblDate = new JLabel("Date: ");
			lblDate.setBounds(0, 25, 34, 14);
			meetPanel.add(lblDate);

			JTextField dateTxtField = new JTextField(m.getDate());
			dateTxtField.setBounds(54, 25, 170, 20);
			meetPanel.add(dateTxtField);
			dateTxtField.setColumns(10);

			JLabel lblTime = new JLabel("Time: ");
			lblTime.setBounds(0, 45, 34, 14);
			meetPanel.add(lblTime);

			SpinnerListModel startModel = new SpinnerListModel(startTimes);
			JSpinner startSpinner = new JSpinner(startModel);
			startSpinner.setBounds(54, 45, 60, 20);
			try {
				startSpinner.setValue(m.getStart());
			} catch (Exception e) {

			}
			meetPanel.add(startSpinner);

			JLabel lblTo = new JLabel(" to ");
			lblTo.setBounds(115, 47, 20, 14);
			meetPanel.add(lblTo);

			SpinnerListModel endtModel = new SpinnerListModel(endTimes);
			JSpinner endSpinner = new JSpinner(endtModel);
			endSpinner.setBounds(140, 45, 60, 20);
			try {
				endSpinner.setValue(m.getEnd());
			} catch (Exception e) {

			}

			lblStatus = new JLabel("");
			lblStatus.setBounds(500, 11, 500, 23);
			frame.getContentPane().add(lblStatus);

			meetPanel.add(endSpinner);
			JButton thisPDF = new JButton("Create Meet");
			thisPDF.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					regenMeet(m.getId());
				}
			});
			thisPDF.setBounds(250, 10, 150, 25);
			meetPanel.add(thisPDF);

			JButton showStats = new JButton("Show Stats");
			showStats.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					displayStats(m.getId());
				}

			});
			showStats.setBounds(400, 10, 150, 25);
			meetPanel.add(showStats);

			meetValues.put(m.getId(), new MeetHelper(m.getId(), locationTxtField, dateTxtField, startSpinner, endSpinner));

			JTabbedPane meetPane = new JTabbedPane(JTabbedPane.TOP);
			meetPane.setBounds(10, 75, 900, 150);
			meetPanel.add(meetPane);
			for (Room r : m.getRoom()) {
				JPanel roomPanel = new JPanel();
				roomPanel.setLayout(null);
				meetPane.addTab(r.getName(), null, roomPanel, "");

				JLabel lblRoomName = new JLabel("Name: ");
				lblRoomName.setBounds(0, 6, 70, 14);
				roomPanel.add(lblRoomName);

				JTextField roomNameTxtField = new JTextField(r.getName());
				roomNameTxtField.setBounds(80, 3, 170, 20);
				roomPanel.add(roomNameTxtField);
				roomNameTxtField.setColumns(10);

				JLabel lblQuizMasters = new JLabel("QuizMasters: ");
				lblQuizMasters.setBounds(0, 25, 70, 14);
				roomPanel.add(lblQuizMasters);

				JTextField quizMastersTxtField = new JTextField(r.getQuizmasters());
				quizMastersTxtField.setBounds(80, 25, 170, 20);
				roomPanel.add(quizMastersTxtField);
				quizMastersTxtField.setColumns(10);

				JLabel lblHint = new JLabel("(Comma Separated)");
				lblHint.setBounds(250, 25, 150, 14);
				roomPanel.add(lblHint);

				roomValues.put(r.getId(), new RoomHelper(r.getId(), roomNameTxtField, quizMastersTxtField));
			}

			JPanel newRoomPanel = new JPanel();
			newRoomPanel.setLayout(null);
			meetPane.addTab("New Room", null, newRoomPanel, "");

			JLabel lblNewRoomName = new JLabel("Name: ");
			lblNewRoomName.setBounds(0, 6, 70, 14);
			newRoomPanel.add(lblNewRoomName);

			JTextField newRoomNameTxtField = new JTextField("");
			newRoomNameTxtField.setBounds(80, 3, 170, 20);
			newRoomPanel.add(newRoomNameTxtField);
			newRoomNameTxtField.setColumns(10);

			JLabel lblNewQuizMasters = new JLabel("QuizMasters: ");
			lblNewQuizMasters.setBounds(0, 25, 70, 14);
			newRoomPanel.add(lblNewQuizMasters);

			JTextField newQuizMastersTxtField = new JTextField("");
			newQuizMastersTxtField.setBounds(80, 25, 170, 20);
			newRoomPanel.add(newQuizMastersTxtField);
			newQuizMastersTxtField.setColumns(10);

			JLabel lblHint = new JLabel("(Comma Separated)");
			lblHint.setBounds(250, 25, 150, 14);
			newRoomPanel.add(lblHint);

			roomValues.put("NewRoom:" + m.getId(), new RoomHelper(UUID.randomUUID().toString(), newRoomNameTxtField, newQuizMastersTxtField));
		}

		JPanel meetPanel = new JPanel();
		meetPanel.setLayout(null);
		meetTabbedPane.addTab("New Meet", null, meetPanel, "");

		JLabel lblName = new JLabel("Name: ");
		lblName.setBounds(0, 6, 34, 14);
		meetPanel.add(lblName);

		JTextField locationTxtField = new JTextField("");
		locationTxtField.setBounds(54, 3, 170, 20);
		meetPanel.add(locationTxtField);
		locationTxtField.setColumns(10);

		JLabel lblDate = new JLabel("Date: ");
		lblDate.setBounds(0, 25, 34, 14);
		meetPanel.add(lblDate);

		JTextField dateTxtField = new JTextField("");
		dateTxtField.setBounds(54, 25, 170, 20);
		meetPanel.add(dateTxtField);
		dateTxtField.setColumns(10);

		JLabel lblTime = new JLabel("Time: ");
		lblTime.setBounds(0, 45, 34, 14);
		meetPanel.add(lblTime);

		SpinnerListModel startModel = new SpinnerListModel(startTimes);
		JSpinner startSpinner = new JSpinner(startModel);
		startSpinner.setBounds(54, 45, 60, 20);
		meetPanel.add(startSpinner);

		JLabel lblTo = new JLabel(" to ");
		lblTo.setBounds(115, 47, 20, 14);
		meetPanel.add(lblTo);

		SpinnerListModel endtModel = new SpinnerListModel(endTimes);
		JSpinner endSpinner = new JSpinner(endtModel);
		endSpinner.setBounds(140, 45, 60, 20);
		meetPanel.add(endSpinner);

		meetValues.put("New", new MeetHelper(UUID.randomUUID().toString(), locationTxtField, dateTxtField, startSpinner, endSpinner));

		JTabbedPane roomNewPane = new JTabbedPane(JTabbedPane.TOP);
		roomNewPane.setBounds(10, 70, 700, 150);
		meetPanel.add(roomNewPane);

		JPanel newRoomPanel = new JPanel();
		newRoomPanel.setLayout(null);
		roomNewPane.addTab("New Room", null, newRoomPanel, "");

		JLabel lblNewRoomName = new JLabel("Name: ");
		lblNewRoomName.setBounds(0, 6, 70, 14);
		newRoomPanel.add(lblNewRoomName);

		JTextField newRoomNameTxtField = new JTextField("");
		newRoomNameTxtField.setBounds(80, 3, 170, 20);
		newRoomPanel.add(newRoomNameTxtField);
		newRoomNameTxtField.setColumns(10);

		JLabel lblNewQuizMasters = new JLabel("QuizMasters: ");
		lblNewQuizMasters.setBounds(0, 25, 70, 14);
		newRoomPanel.add(lblNewQuizMasters);

		JTextField newQuizMastersTxtField = new JTextField("");
		newQuizMastersTxtField.setBounds(80, 25, 170, 20);
		newRoomPanel.add(newQuizMastersTxtField);
		newQuizMastersTxtField.setColumns(10);

		JLabel lblHint = new JLabel("(Comma Separated)");
		lblHint.setBounds(250, 25, 150, 14);
		newRoomPanel.add(lblHint);

		roomValues.put("New", new RoomHelper(UUID.randomUUID().toString(), newRoomNameTxtField, newQuizMastersTxtField));

		JButton btnNewButton = new JButton("Generate All");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblStatus.setText("Generating All....");
				generateAll();

			}
		});
		btnNewButton.setBounds(10, 599, 150, 23);
		frame.getContentPane().add(btnNewButton);

		JButton PDF = new JButton("PDF");
		PDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					schedules = JAXB.unmarshal(scheduleFile, Schedules.class);
					generatePDF(schedules.getSchedule().get(0));
				} catch (COSVisitorException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		PDF.setBounds(170, 599, 89, 23);
		frame.getContentPane().add(PDF);

	}

	protected void displayStats(String id) {
		schedules = JAXB.unmarshal(scheduleFile, Schedules.class);
		int backToBackCount = 0;
		int teamsWithTwo = 0;
		List<String> backToBacks = new ArrayList<String>();
		for (QuizMeet qm : schedules.getSchedule().get(0).getQuizMeet()) {
			if (qm.getId().equals(id)) {
				for (int i = 0; i < qm.getSlot().size(); i++) {
					if ((i - 1) > -1) {
						for (Quiz quiz : qm.getSlot().get(i).getQuiz()) {
							if (inSlot(qm.getSlot().get(i - 1), quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3())) {
								backToBackCount++;
								if (inSlot(qm.getSlot().get(i - 1), quiz.getTeam1())) {
									if (backToBacks.contains(quiz.getTeam1())) {
										teamsWithTwo++;
									} else {
										backToBacks.add(quiz.getTeam1());
									}
								}
								if (inSlot(qm.getSlot().get(i - 1), quiz.getTeam2())) {
									if (backToBacks.contains(quiz.getTeam2())) {
										teamsWithTwo++;
									} else {
										backToBacks.add(quiz.getTeam2());
									}
								}
								if (inSlot(qm.getSlot().get(i - 1), quiz.getTeam3())) {
									if (backToBacks.contains(quiz.getTeam3())) {
										teamsWithTwo++;
									} else {
										backToBacks.add(quiz.getTeam3());
									}
								}
							}
						}

					}
				}
			}
		}
		String msg = "There is '" + backToBackCount + "' back to back quizzes. And '" + teamsWithTwo + "' teams have more than one";
		lblStatus.setText(msg);

	}

	private void printSlotStats(List<Slot> slots) {
		int backToBackCount = 0;
		int teamsWithTwo = 0;
		List<String> backToBacks = new ArrayList<String>();

		for (int i = 0; i < slots.size(); i++) {
			if ((i - 1) > -1) {
				for (Quiz quiz : slots.get(i).getQuiz()) {
					if (inSlot(slots.get(i - 1), quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3())) {
						backToBackCount++;
						if (inSlot(slots.get(i - 1), quiz.getTeam1())) {
							if (backToBacks.contains(quiz.getTeam1())) {
								teamsWithTwo++;
							} else {
								backToBacks.add(quiz.getTeam1());
							}
						}
						if (inSlot(slots.get(i - 1), quiz.getTeam2())) {
							if (backToBacks.contains(quiz.getTeam2())) {
								teamsWithTwo++;
							} else {
								backToBacks.add(quiz.getTeam2());
							}
						}
						if (inSlot(slots.get(i - 1), quiz.getTeam3())) {
							if (backToBacks.contains(quiz.getTeam3())) {
								teamsWithTwo++;
							} else {
								backToBacks.add(quiz.getTeam3());
							}
						}
					}
				}

			}
		}
		System.out.println("There is '" + backToBackCount + "' back to back quizzes. And '" + teamsWithTwo + "' teams have more than one");
	}

	private void generateChurchTabs(Churches churchs) {
		churchTabbedPane.setBounds(10, 45, 923, 230);
		frame.getContentPane().add(churchTabbedPane);

		String ratingOptions[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		// String columnNames[] = { "Name", "Caption" };

		for (Church church : churchs.getChurch()) {
			JPanel churchPanel = new JPanel();
			churchPanel.setLayout(null);
			churchTabbedPane.addTab(church.getName(), null, churchPanel, "");
			churchPanel.setLayout(null);

			JLabel lblName = new JLabel("Name: ");
			lblName.setBounds(0, 6, 34, 14);
			churchPanel.add(lblName);

			JTextField textField = new JTextField(church.getName());
			textField.setBounds(54, 3, 170, 20);
			churchPanel.add(textField);
			textField.setColumns(10);

			churchTxts.put(church.getId(), textField);

			JTabbedPane teamPane = new JTabbedPane(JTabbedPane.TOP);
			teamPane.setBounds(10, 50, 900, 150);
			churchPanel.add(teamPane);

			for (Team team : church.getTeam()) {
				JPanel teamPanel = new JPanel();
				teamPanel.setLayout(null);
				teamPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
				teamPane.addTab(team.getName(), null, teamPanel, "");

				JLabel lblTeamName = new JLabel("Name: ");
				lblTeamName.setBounds(5, 5, 100, 20);
				teamPanel.add(lblTeamName);

				JTextField txtTeamName = new JTextField(team.getName());
				txtTeamName.setBounds(40, 5, 150, 20);
				txtTeamName.setColumns(10);
				teamPanel.add(txtTeamName);

				JLabel lblTeamCoach = new JLabel("Coach: ");
				lblTeamCoach.setBounds(5, 25, 100, 20);
				teamPanel.add(lblTeamCoach);

				JTextField txtTeamCoach = new JTextField(team.getCoach());
				txtTeamCoach.setBounds(40, 25, 150, 20);
				txtTeamCoach.setColumns(10);
				teamPanel.add(txtTeamCoach);

				JLabel lblTeamRating = new JLabel("Rating: ");
				lblTeamRating.setBounds(5, 45, 100, 20);
				teamPanel.add(lblTeamRating);

				JComboBox<?> txtTeamRating = new JComboBox<Object>(ratingOptions);
				txtTeamRating.setSelectedItem(team.getRating());
				txtTeamRating.setBounds(40, 45, 150, 20);
				teamPanel.add(txtTeamRating);

				teamValues.put(team.getId(), new TeamHelper(team.getId(), txtTeamName, txtTeamCoach, txtTeamRating));

				JButton removeTeam = new JButton("Remove");
				removeTeam.setBounds(40, 75, 150, 20);
				removeTeam.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						removeTeam(team.getId());
						saveChurchs(true);
					}
				});
				teamPanel.add(removeTeam);

				// THIS IS THE CODE FOR ADDING QUIZZERS GRID LIST TO THE UI
				// Object[][] object = new Object[100][100];
				// int i = 0;
				// if (team.getQuizzer().size() != 0) {
				// for (Quizzer quizzer : team.getQuizzer()) {
				// object[i][0] = quizzer.getName();
				// object[i][1] = quizzer.isCaption();
				// i++;
				// }
				// }
				//
				// JTable grid = new JTable(object, columnNames);
				//
				// grid.setBounds(0, 15, 200, 100);
				//
				// JScrollPane scroll = new JScrollPane();
				// scroll.setBounds(20, 75, 650, 150);
				// scroll.setViewportView(grid);
				// scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
				//
				// teamPanel.add(scroll);
			}

			JPanel teamPanel = new JPanel();
			teamPanel.setLayout(null);
			teamPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			teamPane.addTab("New Team", null, teamPanel, "");

			JLabel lblNewTeamName = new JLabel("Name: ");
			lblNewTeamName.setBounds(5, 5, 100, 20);
			teamPanel.add(lblNewTeamName);

			JTextField txtNewTeamName = new JTextField("");
			txtNewTeamName.setBounds(40, 5, 150, 20);
			txtNewTeamName.setColumns(10);
			teamPanel.add(txtNewTeamName);

			JLabel lblNewTeamCoach = new JLabel("Coach: ");
			lblNewTeamCoach.setBounds(5, 25, 100, 20);
			teamPanel.add(lblNewTeamCoach);

			JTextField txtNewTeamCoach = new JTextField("");
			txtNewTeamCoach.setBounds(40, 25, 150, 20);
			txtNewTeamCoach.setColumns(10);
			teamPanel.add(txtNewTeamCoach);

			JLabel lblNewTeamRating = new JLabel("Rating: ");
			lblNewTeamRating.setBounds(5, 45, 100, 20);
			teamPanel.add(lblNewTeamRating);

			JComboBox<?> txtNewTeamRating = new JComboBox<Object>(ratingOptions);
			txtNewTeamRating.setSelectedItem("");
			txtNewTeamRating.setBounds(40, 45, 150, 20);
			teamPanel.add(txtNewTeamRating);

			teamValues.put("NewTeam:" + church.getId(), new TeamHelper(UUID.randomUUID().toString(), txtNewTeamName, txtNewTeamCoach, txtNewTeamRating));
		}

		JPanel newChurchPanel = new JPanel();
		newChurchPanel.setLayout(null);
		churchTabbedPane.addTab("New Church", null, newChurchPanel, "Click Here to add new Church");
		newChurchPanel.setLayout(null);

		JLabel lblNewName = new JLabel("Name: ");
		lblNewName.setBounds(0, 6, 34, 14);
		newChurchPanel.add(lblNewName);

		JTextField txtNewChurch = new JTextField("");
		txtNewChurch.setBounds(54, 3, 170, 20);
		newChurchPanel.add(txtNewChurch);
		txtNewChurch.setColumns(10);

		churchTxts.put("New", txtNewChurch);

		JTabbedPane teamNewPane = new JTabbedPane(JTabbedPane.TOP);
		teamNewPane.setBounds(208, 52, 861, 150);
		teamNewPane.setBounds(10, 50, 700, 150);
		newChurchPanel.add(teamNewPane);

		JPanel newTeamPanel = new JPanel();
		newTeamPanel.setLayout(null);
		newTeamPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		teamNewPane.addTab("New Team", null, newTeamPanel, "");

		JLabel lblNewTeamName = new JLabel("Name: ");
		lblNewTeamName.setBounds(5, 5, 100, 20);
		newTeamPanel.add(lblNewTeamName);

		JTextField txtNewTeamName = new JTextField("");
		txtNewTeamName.setBounds(40, 5, 150, 20);
		txtNewTeamName.setColumns(10);
		newTeamPanel.add(txtNewTeamName);

		JLabel lblNewTeamCoach = new JLabel("Coach: ");
		lblNewTeamCoach.setBounds(5, 25, 100, 20);
		newTeamPanel.add(lblNewTeamCoach);

		JTextField txtNewTeamCoach = new JTextField("");
		txtNewTeamCoach.setBounds(40, 25, 150, 20);
		txtNewTeamCoach.setColumns(10);
		newTeamPanel.add(txtNewTeamCoach);

		JLabel lblNewTeamRating = new JLabel("Rating: ");
		lblNewTeamRating.setBounds(5, 45, 100, 20);
		newTeamPanel.add(lblNewTeamRating);

		JComboBox<?> txtNewTeamRating = new JComboBox<Object>(ratingOptions);
		txtNewTeamRating.setSelectedItem("1");
		txtNewTeamRating.setBounds(40, 45, 150, 20);
		newTeamPanel.add(txtNewTeamRating);

		teamValues.put("New", new TeamHelper(UUID.randomUUID().toString(), txtNewTeamName, txtNewTeamCoach, txtNewTeamRating));

		JLabel lblChurches = new JLabel("Churches");
		lblChurches.setFont(new Font("Tahoma", Font.BOLD, 19));
		lblChurches.setBounds(10, 11, 276, 23);
		frame.getContentPane().add(lblChurches);

	}

	public void removeTeam(String id) {
		for (Church church : churchs.getChurch()) {
			Iterator<Team> itr = church.getTeam().iterator();
			while (itr.hasNext()) {
				Team team = itr.next();
				if (team.getId().equals(id)) {
					itr.remove();
					return;
				}
			}
		}
	}

	public void saveChurchs(boolean regen) {
		try {
			for (Church c : churchs.getChurch()) {
				String name = churchTxts.get(c.getId()).getText();
				c.setName(name);

				for (Team t : c.getTeam()) {
					String teamName = teamValues.get(t.getId()).getTeamName();
					String teamCoach = teamValues.get(t.getId()).getCoach();
					String rating = teamValues.get(t.getId()).getRating();

					t.setName(teamName);
					t.setCoach(teamCoach);
					t.setRating(rating);
				}

				String newTeam = teamValues.get("NewTeam:" + c.getId()).getTeamName();
				if (newTeam != null && !newTeam.equals("")) {
					regen = true;
					c.getTeam().add(teamValues.get("NewTeam:" + c.getId()).getTeamObject());
				}

			}

			String newChurchName = churchTxts.get("New").getText();
			if (newChurchName != null && !newChurchName.equals("")) {
				regen = true;
				Church newChurch = new Church();
				newChurch.setName(newChurchName);
				newChurch.setId(UUID.randomUUID().toString());

				Team team = teamValues.get("New").getTeamObject();
				if (team.getName() != null && team.getName() != "") {
					newChurch.getTeam().add(team);
				}

				churchs.getChurch().add(newChurch);

			}

			if (regen) {
				churchTabbedPane.removeAll();
				generateChurchTabs(churchs);
			}
			JAXB.marshal(churchs, file);
			churchs = JAXB.unmarshal(file, Churches.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saveMeets() {
		boolean regen = false;
		for (Meet m : meets.getMeet()) {
			m.setLocation(meetValues.get(m.getId()).getLocation());
			m.setDate(meetValues.get(m.getId()).getDate());
			m.setStart(meetValues.get(m.getId()).getStart());
			m.setEnd(meetValues.get(m.getId()).getEnd());

			for (Room r : m.getRoom()) {
				String name = roomValues.get(r.getId()).getName();
				String quizMasters = roomValues.get(r.getId()).getQuizMasters();

				r.setName(name);
				r.setQuizmasters(quizMasters);
			}

			String newRoom = roomValues.get("NewRoom:" + m.getId()).getName();
			if (newRoom != null && !newRoom.equals("")) {
				regen = true;
				m.getRoom().add(roomValues.get("NewRoom:" + m.getId()).getRoomObject());
			}
		}

		String newMeetLocation = meetValues.get("New").getLocation();
		if (newMeetLocation != null && !newMeetLocation.equals("")) {
			regen = true;
			Meet newMeet = new Meet();
			newMeet.setLocation(newMeetLocation);
			newMeet.setDate(meetValues.get("New").getDate());
			newMeet.setStart(meetValues.get("New").getStart());
			newMeet.setEnd(meetValues.get("New").getEnd());

			Room room = roomValues.get("New").getRoomObject();
			if (room.getName() != null && room.getName() != "") {
				newMeet.getRoom().add(room);
			}
			meets.getMeet().add(newMeet);
		}

		if (regen) {
			meetTabbedPane.removeAll();
			generateMeetTabs(meets);
		}

		JAXB.marshal(meets, meetFile);

	}

	public void regenMeet(String id) {
		Set<String> matches = new HashSet<String>();

		for (Meet meet : meets.getMeet()) {
			if (meet.getId().equals(id)) {
				QuizMeet qm = generateQuizMeet(meet, matches); // generateMeet(meet,matches);
																// //
				qm.setId(meet.getId());
				Iterator<QuizMeet> itr = schedules.getSchedule().get(0).getQuizMeet().iterator();
				int count = 0;
				while (itr.hasNext()) {
					QuizMeet q = itr.next();
					if (q.getMeet().get(0).getId().equals(id)) {
						schedules.getSchedule().get(0).getQuizMeet().set(count, qm);
						addSchedule(schedules.getSchedule().get(0));
						return;
					}
					count++;
				}
				schedules.getSchedule().get(0).getQuizMeet().add(qm);
				addSchedule(schedules.getSchedule().get(0));
				return;
			}
			// if this is not the meet that is being regenerated and it occurs
			// beofre the requested one, update the matches with what occured.
			for (QuizMeet qm : schedules.getSchedule().get(0).getQuizMeet()) {
				for (Slot slot : qm.getSlot()) {
					for (Quiz q : slot.getQuiz()) {
						matches.add(q.getTeam1() + ":" + q.getTeam2());
						matches.add(q.getTeam1() + ":" + q.getTeam3());
						matches.add(q.getTeam2() + ":" + q.getTeam3());
					}
				}
			}

		}
	}

	public void generateAll() {
		try {
			Schedule schedule = new Schedule();
			Set<String> matches = new HashSet<String>();

			for (Meet meet : meets.getMeet()) {
				schedule.getQuizMeet().add(generateMeet(meet, matches));

				// CURRENTLY ADDED FOR DEBUGGING PURPOSES TO SEE HOW FAR IT GETS
				// EVERY TIME YOU ADD A NEW SCHEDULE.
				addSchedule(schedule);
			}

			addSchedule(schedule);
			generatePDF(schedule);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private QuizMeet generateMeet(Meet meet, Set<String> matches) {
		try {
			HashMap<String, Integer> count = new HashMap<String, Integer>();
			for (TeamHelper t : teamValues.values()) {
				if (!"".equals(t.getTeamName())) {
					count.put(t.getId(), 0);
				}
			}

			List<Team> teams = new ArrayList<Team>();
			for (TeamHelper helper : teamValues.values()) {
				if (!"".equals(helper.getTeamName())) {
					teams.add(helper.getTeamObject());
				}
			}

			QuizMeet qMeet = new QuizMeet();
			qMeet.getMeet().add(meet);
			GregorianCalendar startCalendar = new GregorianCalendar();
			startCalendar.set(0, 0, 0, Integer.parseInt(meet.getStart().substring(0, meet.getStart().indexOf(":"))), Integer.parseInt(meet.getStart().substring(meet.getStart().indexOf(":") + 1, meet.getStart().length())));
			GregorianCalendar endCalendar = new GregorianCalendar();
			endCalendar.set(0, 0, 0, Integer.parseInt(meet.getEnd().substring(0, meet.getEnd().indexOf(":"))), Integer.parseInt(meet.getEnd().substring(meet.getEnd().indexOf(":") + 1, meet.getEnd().length())));

			double diff = startCalendar.getTimeInMillis() - endCalendar.getTimeInMillis();
			double diffHours = diff / (60 * 60 * 1000) % 24;
			double totalHours = Math.abs(diffHours) - 1; // Accommodating
															// for lunch
			final double interval = 20;
			int slots = (int) Math.ceil((totalHours * 60.00) / interval);

			XMLGregorianCalendar start = XMLGregorianCalendarImpl.createTime(Integer.parseInt(meet.getStart().substring(0, meet.getStart().indexOf(":"))), Integer.parseInt(meet.getStart().substring(meet.getStart().indexOf(":") + 1, meet.getStart().length())), 0, 0);

			long intervalInMilli = (20 * 60) * 1000;
			long hourInMilli = (60 * 60) * 1000;
			Duration twentyMin = DatatypeFactory.newInstance().newDuration(intervalInMilli);
			Duration hour = DatatypeFactory.newInstance().newDuration(hourInMilli);

			List<Quiz> section = new ArrayList<Quiz>();
			section.clear();
			getNewSection(teams, matches, count, section);
			boolean morning = true;

			List<Slot> morningQuizzing = new ArrayList<Slot>();
			List<Slot> afternoonQuizzing = new ArrayList<Slot>();

			List<String> backToBacks = new ArrayList<String>();

			for (int i = 0; i < slots; i++) {
				Slot slot = new Slot();
				Slot lastSlot = new Slot();

				if (morning) {
					if (i > 0) {
						lastSlot = morningQuizzing.get(i - 1);
					}
				} else {
					if (afternoonQuizzing.size() > i - morningQuizzing.size()) {
						lastSlot = afternoonQuizzing.get(i - morningQuizzing.size());
					}
				}

				// /////////TIME
				// UPDATING///////////////////////////////////////////
				if (start.toString().substring(0, 5).equals("12:20")) {
					start.add(hour);
					// RESET MORNING QUIZZING
					for (Team t : teams) {
						count.replace(t.getId(), 0);
					}

					// addLeftoverQuizzes(section, morningQuizzing,
					// meet.getRoom().size());
					section.clear();
					getNewSection(teams, matches, count, section);
					morning = false;

				}
				slot.setStart(start.clone().toString().substring(0, 5));
				start.add(twentyMin);
				slot.setEnd(start.clone().toString().substring(0, 5));

				// ////////////////////////////////////////////////////////////////
				// //// Create quizzes for the
				// slot////////////////////////////////

				for (int j = 0; j < meet.getRoom().size(); j++) {
					Quiz quiz = new Quiz();
					Team team1 = new Team();
					Team team2 = new Team();
					Team team3 = new Team();

					// Leave first quiz open
					if (!(i == 0 && j == 0)) {
						Iterator<Quiz> itr = section.iterator();
						while (itr.hasNext()) {
							Quiz q = itr.next();
							if (!inSlot(slot, getTeam(q.getTeam1()).getName(), getTeam(q.getTeam2()).getName(), getTeam(q.getTeam3()).getName())) {
								if (inSlot(lastSlot, getTeam(q.getTeam1()).getName(), getTeam(q.getTeam2()).getName(), getTeam(q.getTeam3()).getName())) {
									if (backToBacks.contains(getTeam(q.getTeam1()).getName())) {
										continue;
									}
									if (backToBacks.contains(getTeam(q.getTeam2()).getName())) {
										continue;
									}
									if (backToBacks.contains(getTeam(q.getTeam3()).getName())) {
										continue;
									}

									if (inSlot(lastSlot, getTeam(q.getTeam1()).getName())) {
										backToBacks.add(getTeam(q.getTeam1()).getName());
									}
									if (inSlot(lastSlot, getTeam(q.getTeam2()).getName())) {
										backToBacks.add(getTeam(q.getTeam2()).getName());
									}
									if (inSlot(lastSlot, getTeam(q.getTeam3()).getName())) {
										backToBacks.add(getTeam(q.getTeam3()).getName());
									}
								}

								team1 = getTeam(q.getTeam1());
								team2 = getTeam(q.getTeam2());
								team3 = getTeam(q.getTeam3());
								itr.remove();
								break;
							}
						}

						quiz.setTeam1(team1.getName());
						quiz.setTeam2(team2.getName());
						quiz.setTeam3(team3.getName());
					}
					slot.getQuiz().add(quiz);
				}

				if (morning) {
					morningQuizzing.add(slot);
				} else {
					afternoonQuizzing.add(slot);
				}

			}
			qMeet.getSlot().addAll(morningQuizzing);
			// addLeftoverQuizzes(section, afternoonQuizzing,
			// meet.getRoom().size());
			qMeet.getSlot().addAll(afternoonQuizzing);
			return qMeet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// New and hopefully improved version
	public QuizMeet generateQuizMeet(Meet meet, Set<String> matches) {
		///matches.clear();
		StringBuilder statusMsg = new StringBuilder();
		QuizMeet qMeet = new QuizMeet();
		qMeet = new QuizMeet();
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		List<Slot> morningQuizzing = new ArrayList<Slot>();
		List<Slot> afternoonQuizzing = new ArrayList<Slot>();
		try {

			for (TeamHelper t : teamValues.values()) {
				if (!"".equals(t.getTeamName())) {
					count.put(t.getId(), 0);
				}
			}

			ArrayList<Team> teams = new ArrayList<Team>();
			for (TeamHelper helper : teamValues.values()) {
				if (!"".equals(helper.getTeamName())) {
					teams.add(helper.getTeamObject());
				}
			}
			Collections.shuffle(teams);
			qMeet.getMeet().add(meet);
			GregorianCalendar startCalendar = new GregorianCalendar();
			startCalendar.set(0, 0, 0, Integer.parseInt(meet.getStart().substring(0, meet.getStart().indexOf(":"))), Integer.parseInt(meet.getStart().substring(meet.getStart().indexOf(":") + 1, meet.getStart().length())));
			GregorianCalendar endCalendar = new GregorianCalendar();
			endCalendar.set(0, 0, 0, Integer.parseInt(meet.getEnd().substring(0, meet.getEnd().indexOf(":"))), Integer.parseInt(meet.getEnd().substring(meet.getEnd().indexOf(":") + 1, meet.getEnd().length())));

			double diff = startCalendar.getTimeInMillis() - endCalendar.getTimeInMillis();
			double diffHours = diff / (60 * 60 * 1000) % 24;
			double totalHours = Math.abs(diffHours) - 1; // Accommodating
															// for lunch
			final double interval = 20;
			int slots = (int) Math.ceil((totalHours * 60.00) / interval);

			XMLGregorianCalendar start = XMLGregorianCalendarImpl.createTime(Integer.parseInt(meet.getStart().substring(0, meet.getStart().indexOf(":"))), Integer.parseInt(meet.getStart().substring(meet.getStart().indexOf(":") + 1, meet.getStart().length())), 0, 0);

			long intervalInMilli = (20 * 60) * 1000;
			long hourInMilli = (60 * 60) * 1000;
			Duration twentyMin = DatatypeFactory.newInstance().newDuration(intervalInMilli);
			Duration hour = DatatypeFactory.newInstance().newDuration(hourInMilli);

			boolean morning = true;
			List<String> backToBacks = new ArrayList<String>();

			ArrayList<Team> teamsToQuiz = new ArrayList<Team>(teams);
			for (int i = 0; i < slots; i++) {
				Slot slot = new Slot();
				refillTeamsIfNeeded(matches, count, teams, teamsToQuiz, slot);

				// /////////TIME
				// UPDATING///////////////////////////////////////////
				if (start.toString().substring(0, 5).equals("12:20")) {
					start.add(hour);
					boolean allGood = checkCount(count);
					statusMsg.append("Morning: " + (allGood ? "Good" : "Bad"));

					String status = checkCount(count) ? "Good" : "Bad";
					// RESET QUIZZING
					for (Team t : teams) {
						count.replace(t.getId(), 0);
					}
					morning = false;
					printSlotStats(morningQuizzing);
					System.out.println("---MORNING DONE--Status:" + status + "---");
				}
				slot.setStart(start.clone().toString().substring(0, 5));
				start.add(twentyMin);
				slot.setEnd(start.clone().toString().substring(0, 5));

				// ////////////////////////////////////////////////////////////////
				// //// Create quizzes for the
				// slot////////////////////////////////

				slot: for (int j = 0; j < meet.getRoom().size(); j++) {
					if ((i == 0 && j == 0)) {
						slot.getQuiz().add(new Quiz());
						continue;
					}

					Quiz quiz = getQuiz(count, matches, teamsToQuiz, slot);
					boolean listRefilled = false;
					while (quiz == null || quiz.getTeam1() == null || quiz.getTeam2() == null || quiz.getTeam3() == null || inSlot(slot, getTeam(quiz.getTeam1()).getName(), getTeam(quiz.getTeam2()).getName(), getTeam(quiz.getTeam3()).getName())) {
						if (amountRemaining(count, teams) == 0) {
							break;
						}
						quiz = getQuiz(count, matches, teamsToQuiz, slot);
						if (!listRefilled) {

							listRefilled = true;
						} else {
							refillTeamsIfNeeded(matches, count, teams, teamsToQuiz, slot);
							// allTeamsQuizzed(matches, teams);
							j = -1;
							resetSlot(count, matches, slot);
							continue slot;
						}
					}

					if (quiz != null) {
						updateCount(count, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());
						Iterator<Team> itr = teamsToQuiz.iterator();
						while (itr.hasNext()) {
							Team team = itr.next();
							if (team.getId().equals(quiz.getTeam1()) || team.getId().equals(quiz.getTeam2()) || team.getId().equals(quiz.getTeam3())) {
								itr.remove();
							}
						}
						swapIdWithNames(quiz);

						matches.add(quiz.getTeam1() + ":" + quiz.getTeam2());
						matches.add(quiz.getTeam1() + ":" + quiz.getTeam3());
						matches.add(quiz.getTeam2() + ":" + quiz.getTeam3());

						slot.getQuiz().add(quiz);
					}
				}

				if (morning) {
					morningQuizzing.add(slot);
					System.out.println(morningQuizzing.size());
				} else {
					afternoonQuizzing.add(slot);
					System.out.println(afternoonQuizzing.size());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String status = checkCount(count) ? "Good" : "Bad";
		System.out.println("---AFTERNOON DONE--Status:" + status + "---");
		sortSlots(morningQuizzing, meet.getRoom().size());
		sortSlots(afternoonQuizzing, meet.getRoom().size());
		qMeet.getSlot().addAll(morningQuizzing);
		qMeet.getSlot().addAll(afternoonQuizzing);

		boolean allGood = checkCount(count);
		statusMsg.append(" Afternoon: " + (allGood ? "Good" : "Bad"));
		Date date = new Date();
		lblStatus.setText(meet.getLocation() + "-" + meet.getDate() + " " + statusMsg.toString() + " " + date.toString());
		return qMeet;
	}

	private void resetSlot(HashMap<String, Integer> count, Set<String> matches, Slot slot) {
		for (Quiz quiz : slot.getQuiz()) {
			if (quiz != null && getTeam(quiz.getTeam1()) != null && getTeam(quiz.getTeam2()) != null && getTeam(quiz.getTeam3()) != null) {
				matches.remove(getTeam(quiz.getTeam1()).getName() + ":" + getTeam(quiz.getTeam2()).getName());
				matches.remove(getTeam(quiz.getTeam1()).getName() + ":" + getTeam(quiz.getTeam3()).getName());
				matches.remove(getTeam(quiz.getTeam2()).getName() + ":" + getTeam(quiz.getTeam3()).getName());

				int num = count.get(getTeam(quiz.getTeam1()).getId());
				count.put(getTeam(quiz.getTeam1()).getId(), num - 1);

				int num2 = count.get(getTeam(quiz.getTeam2()).getId());
				count.put(getTeam(quiz.getTeam2()).getId(), num2 - 1);

				int num3 = count.get(getTeam(quiz.getTeam3()).getId());
				count.put(getTeam(quiz.getTeam3()).getId(), num3 - 1);
			}
		}
		slot.getQuiz().clear();

	}

	private boolean checkCount(HashMap<String, Integer> count) {
		boolean allGood = true;
		for (Integer i : count.values()) {
			if (i < 3) {
				allGood = false;
			}
		}
		return allGood;
	}

	private boolean refillTeamsIfNeeded(Set<String> matches, HashMap<String, Integer> count, ArrayList<Team> teams, ArrayList<Team> teamsToQuiz, Slot slot) {
		if (getQuiz(count, matches, teamsToQuiz, slot) == null) {
			teamsToQuiz.clear();
			teamsToQuiz.addAll(teams);
			return true;
		}
		return false;
	}

	private void sortSlots(List<Slot> slots, int roomSize) {
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
					if (inSlot(slots.get(i + 1), slots.get(i).getQuiz().get(j))) {
						if (!inSlot(slots.get(slots.size() - 1), slots.get(i).getQuiz().get(j)) && !inSlot(slots.get(slots.size() - 2), slots.get(i).getQuiz().get(j))) {
							Quiz quiz = slots.get(i).getQuiz().get(j);
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

	private boolean validateMeet(QuizMeet meet) {
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

	public boolean allTeamsQuizzed(Set<String> matches, List<Team> teams) {
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

	public Map<String, List<String>> getMatchups(List<Team> teams, Set<String> matches, HashMap<String, Integer> count) {
		Map<String, List<String>> teamMatchups = new HashMap<String, List<String>>();

		for (Team team : teams) {
			List<String> matchups = new ArrayList<String>();
			for (Team t : teams) {
				if (!sameTwoTeams(team, t)) {
					if (!hasQuizzed(matches, team.getName(), t.getName()) && !doneQuizzing(count, t, 3)) {
						matchups.add(t.getName());
					}
				}
			}
			teamMatchups.put(team.getName(), matchups);
		}
		return teamMatchups;
	}

	private void addLeftoverQuizzes(List<Quiz> leftOver, List<Slot> slots, int roomCount) {
		// /TreeSet<Slot> s = new TreeSet<Slot>(slots);
		System.out.println("Starting to add '" + leftOver.size() + "' unadded quizzes...");
		for (Quiz quiz : leftOver) {
			System.out.println(getTeam(quiz.getTeam1()).getName() + "\n");
			System.out.println(getTeam(quiz.getTeam2()).getName() + "\n");
			System.out.println(getTeam(quiz.getTeam3()).getName() + "\n");
			System.out.println(".................");
		}
		while (leftOver.size() > 0) {
			mainLoop: for (int i = 0; i < leftOver.size(); i++) {
				for (Slot s : slots) {
					// CHECK FOR THE NULL
					if (!inSlot(s, getTeam(leftOver.get(i).getTeam1()).getName(), getTeam(leftOver.get(i).getTeam2()).getName(), getTeam(leftOver.get(i).getTeam3()).getName())) {
						for (int j = 0; j < s.getQuiz().size(); j++) {
							if (s.getQuiz().get(j).getTeam1() != null) {
								if (!inSlot(slots.get(slots.size() - 1), s.getQuiz().get(j).getTeam1(), s.getQuiz().get(j).getTeam2(), s.getQuiz().get(j).getTeam3())) {
									s.getQuiz().add(swapTeamValues(leftOver.remove(i)));
									if (slots.get(slots.size() - 1).getQuiz().size() < roomCount) {
										slots.get(slots.size() - 1).getQuiz().add(s.getQuiz().remove(j));
									} else {
										for (int k = 0; k < slots.get(slots.size() - 1).getQuiz().size(); k++) {
											if (slots.get(slots.size() - 1).getQuiz().get(k).getTeam1() == null) {
												slots.get(slots.size() - 1).getQuiz().set(k, s.getQuiz().remove(j));
											}
										}
									}

									continue mainLoop;
								}
							}
						}
					}
				}
			}
		}
		System.out.println("finished adding unadded quizzes");
	}

	private Quiz swapTeamValues(Quiz remove) {
		Quiz quiz = new Quiz();
		quiz.setTeam1(getTeam(remove.getTeam1()).getName());
		quiz.setTeam2(getTeam(remove.getTeam2()).getName());
		quiz.setTeam3(getTeam(remove.getTeam3()).getName());
		return quiz;
	}

	private boolean noSkipTeams(List<Team> skipTeams, String team1, String team2, String team3) {
		for (Team team : skipTeams) {
			if (team1.equals(team.getId()) || team2.equals(team.getId()) || team3.equals(team.getId())) {
				return false;
			}
		}

		return true;
	}

	public void getNewSection(List<Team> teams, Set<String> matches, HashMap<String, Integer> count, List<Quiz> section) {
		Set<String> sectionMatches = (Set<String>) ((HashSet) matches).clone();
		while (amountRemaining(count, teams) > 0) {
			try {
				Quiz quiz = getQuiz(count, sectionMatches, (ArrayList<Team>) teams, null);

				updateCount(count, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());

				sectionMatches.add(quiz.getTeam1() + ":" + quiz.getTeam2());
				sectionMatches.add(quiz.getTeam1() + ":" + quiz.getTeam3());
				sectionMatches.add(quiz.getTeam2() + ":" + quiz.getTeam3());

				section.add(quiz);

			} catch (Exception e) {
				// start over
				System.out.println("START OVER");
				for (Team t : teams) {
					count.replace(t.getId(), 0);
				}
				section.clear();
				sectionMatches = (Set<String>) ((HashSet) matches).clone();
			}

		}

		for (Quiz quiz : section) {
			matches.add(quiz.getTeam1() + ":" + quiz.getTeam2());
			matches.add(quiz.getTeam1() + ":" + quiz.getTeam3());
			matches.add(quiz.getTeam2() + ":" + quiz.getTeam3());
		}

	}

	private Quiz getQuiz(HashMap<String, Integer> count, Set<String> matches, ArrayList<Team> teams, Slot slot) {
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
//				if (eliminatesTeamOptions(count, team, null, null, matches)) {
//					continue;
//				}
				Quiz q = new Quiz();

				Team team2 = new Team();

				for (Team t : ts) {
//					if (eliminatesTeamOptions(count, t, team, null, matches)) {
//						continue;
//					}
					if (!hasQuizzed(matches, team, t) && !sameTwoTeams(team, t)) {
						team2 = t;
					}
				}

				Team team3 = new Team();
				for (Team t : ts) {
//					if (eliminatesTeamOptions(count, t, team, team2, matches)) {
//						continue;
//					}
					if (!hasQuizzed(matches, team, t) && !hasQuizzed(matches, team2, t) && !sameThreeTeams(team, team2, t)) {
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

	private boolean eliminatesTeamOptions(HashMap<String, Integer> count, Team team, Team team1, Team team2, Set<String> matches) {
		// /THE FIRST TEAM IS THE ONE BEING REVIEWED AND TEAM1 AND TEAM2 ARE THE
		// TEAMS THAT IT WOULD BE QUIZZING AGAINST.
		HashMap<String, Integer> testCount = (HashMap<String, Integer>) count.clone();
		testCount.put(team.getId(), testCount.get(team.getId()) + 1);
		Set<String> testMatches = new HashSet<String>(matches);

		if (teamMatches == null || teams == null) {
			teamMatches = new HashMap<String, List<Team>>();
			teams = new ArrayList<Team>();
			for (TeamHelper helper : teamValues.values()) {
				if (!"".equals(helper.getTeamName())) {
					teamMatches.put(helper.getTeamObject().getId(), new ArrayList<Team>());
					teams.add(helper.getTeamObject());
				}
			}
		}

		for (Team compareTeam : teams) {
			if (doneQuizzing(testCount, compareTeam, 3)) {
				continue;
			}
			for (Team matchTeam : teams) {
				if (!doneQuizzing(testCount, matchTeam, 3) && !sameTwoTeams(compareTeam, matchTeam) && !hasQuizzed(testMatches, compareTeam, matchTeam)) {
					teamMatches.get(compareTeam.getId()).add(matchTeam);
				} else if (compareTeam.getId().equals(team.getId())) {
					if ((team1 != null && matchTeam.getId().equals(team1.getId())) || (team2 != null && matchTeam.getId().equals(team2.getId()))) {
						teamMatches.get(compareTeam.getId()).add(matchTeam);
					}
				}
			}
		}
		int index = 0;
		for (List<Team> list : teamMatches.values()) {
			if (list.size() == 0) {
				return true;
			}
			index++;
		}
		return false;
	}

	private String getSuitableTeam(HashMap<String, Integer> count, Team team1, Team team2) {
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

	public boolean everyTeamQuizzed(HashMap<String, Integer> count, int max) {
		for (int i : count.values()) {
			if (i < max) {
				return false;
			}
		}

		return true;

	}

	public Team getTeam(String id) {
		if (teamValues.containsKey(id)) {
			return teamValues.get(id).getTeamObject();
		} else {
			return getTeamByName(id);
		}
	}

	public Team getTeamByName(String name) {
		for (TeamHelper helper : teamValues.values()) {
			if (helper.getTeamName().equals(name)) {
				return helper.getTeamObject();
			}
		}
		return null;
	}

	public boolean invalidQuiz(final int quizMax, Set<String> matches, HashMap<String, Integer> count, Team team1, Team team2, Team team3) {
		return doneQuizzing(count, team1, quizMax) || doneQuizzing(count, team2, quizMax) || doneQuizzing(count, team3, quizMax) || hasQuizzed(matches, team1, team2) || hasQuizzed(matches, team1, team3) || hasQuizzed(matches, team2, team3);
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
					if (!hasQuizzed(matches, s, team1.getId()) && !hasQuizzed(matches, s, team2.getId()))
						return s;
				}
			}
		}

		return null;
	}

	public void printCount(HashMap<String, Integer> count) {
		for (String s : count.keySet()) {
			if (s != null) {
				System.out.print(teamValues.get(s).getTeamName() + " : " + count.get(s) + "\n");
			}
		}
	}

	private boolean inSlot(Slot slot, Quiz quiz) {
		if (slot == null || slot.getStart() == null || quiz == null) {
			return false;
		}
		return inSlot(slot, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());
	}

	private boolean inSlot(Slot slot, String team1, String team2, String team3) {
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

	private boolean inSlot(Slot slot, String team) {
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

	private int amountRemaining(HashMap<String, Integer> count, List<Team> teamList) {
		int amountRemaining = 0;

		for (Integer i : count.values()) {
			if (i < 3) {
				amountRemaining++;
			}
		}

		return amountRemaining;

	}

	private boolean doneQuizzing(HashMap<String, Integer> count, Team team, int max) {
		if (!count.containsKey(team.getId())) {
			return false;
		} else {
			return count.get(team.getId()) >= max;
		}
	}

	public void updateCount(HashMap<String, Integer> count, Team team1, Team team2, Team team3) {
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

	public void updateCount(HashMap<String, Integer> count, String team1, String team2, String team3) {
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

	private boolean hasQuizzed(Set<String> matches, String team1, String team2) {
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

	private boolean hasQuizzed(Set<String> matches, Team team1, Team team2) {
		// System.out.println("checking...." + team1.getId() + ":" +
		// team2.getId());
		for (String s : matches) {
			if (s.equals(team1.getId() + ":" + team2.getId())) {
				return true;
			} else if (s.equals(team2.getId() + ":" + team1.getId())) {
				return true;
			}

			if (s.equals(team1.getName() + ":" + team2.getName())) {
				return true;
			} else if (s.equals(team2.getName() + ":" + team1.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean sameTwoTeams(Team team1, Team team2) {
		if (team1.getId() == null) {
			return false;
		} else if (team2.getId() == null) {
			return false;
		} else {
			return team1.getId().equals(team2.getId());
		}
	}

	private boolean sameThreeTeams(Team team1, Team team2, Team team3) {
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

	public Team getRandomTeam(List<Team> teams) {
		int num = ((int) ((Math.random()) * teams.size()));
		Team team = teams.get(num);
		while (team == null) {
			num = ((int) ((Math.random()) * teams.size()));
			team = teams.get(num);
		}
		return team;

	}

	public void addSchedule(Schedule schedule) {
		schedules.getSchedule().clear();
		schedules.getSchedule().add(schedule);
		JAXB.marshal(schedules, scheduleFile);
	}

	public void generatePDF(Schedule schedule) throws COSVisitorException, IOException {
		int meetCounter = 1;
		for (QuizMeet qm : schedule.getQuizMeet()) {
			Meet meet = qm.getMeet().get(0);

			PDDocument doc = new PDDocument();
			PDPage page = new PDPage();
			doc.addPage(page);
			PDFont font = PDType1Font.HELVETICA_BOLD;

			// Start a new content stream which will "hold" the to be created
			// content
			PDPageContentStream page1ContentStream = new PDPageContentStream(doc, page);

			// Define a text content stream using the selected font, moving the
			// cursor and drawing the text "Hello World"
			page1ContentStream.beginText();
			page1ContentStream.setFont(font, 20);
			page1ContentStream.moveTextPositionByAmount(100, 750);
			page1ContentStream.drawString("Quiz meet " + meetCounter + " - " + meet.getLocation() + " - " + meet.getDate());
			page1ContentStream.moveTextPositionByAmount(100, -20);
			page1ContentStream.drawString("Morning Quizzing");

			page1ContentStream.endText();
			// Make sure that the content stream is closed:
			page1ContentStream.close();

			PDPageContentStream tableContentStream = new PDPageContentStream(doc, page, true, true);

			drawTable(page, tableContentStream, 700, 50, qm, doc, 0, (qm.getSlot().size() / 2));
			tableContentStream.close();

			PDPage page2 = new PDPage();
			doc.addPage(page2);

			// Start a new content stream which will "hold" the to be created
			// content
			PDPageContentStream page2ContentStream = new PDPageContentStream(doc, page2);

			page2ContentStream.beginText();
			page2ContentStream.setFont(font, 20);
			page2ContentStream.moveTextPositionByAmount(100, 750);
			page2ContentStream.drawString("Quiz meet " + meetCounter + " - " + meet.getLocation() + " - " + meet.getDate());

			page2ContentStream.moveTextPositionByAmount(100, -20);
			page2ContentStream.drawString("AfternoonQuizzing");

			page2ContentStream.endText();
			// Make sure that the content stream is closed:
			page2ContentStream.close();

			PDPageContentStream table2ContentStream = new PDPageContentStream(doc, page2, true, true);

			drawTable(page2, table2ContentStream, 700, 50, qm, doc, (qm.getSlot().size() / 2), qm.getSlot().size());
			table2ContentStream.close();

			// Save the results and ensure that the document is properly closed:
			doc.save("data/" + meet.getLocation() + "-" + meet.getDate() + ".pdf");
			doc.close();
			meetCounter++;
		}

	}

	public static void drawTable(PDPage page, PDPageContentStream contentStream, float y, float margin, QuizMeet qMeet, PDDocument doc, int start, int end) throws IOException {
		final int rows = (end - start) + 1;
		final int cols = qMeet.getMeet().get(0).getRoom().size() + 1;
		final float rowHeight = 70f;
		final float tableWidth = (page.findMediaBox().getWidth() - (2 * margin)) + 10;
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth / (float) cols;
		final float cellMargin = 5f;

		// draw the rows
		float nexty = y;
		for (int i = 0; i <= rows + 1; i++) {
			contentStream.drawLine(margin, nexty, margin + tableWidth, nexty);
			if (i == 0) {
				nexty -= 15;
			} else if (i == rows) {
				nexty -= rowHeight - 15;
			} else {
				nexty -= rowHeight;
			}
		}

		// draw the columns
		float nextx = margin;
		for (int i = 0; i <= cols; i++) {
			contentStream.drawLine(nextx, y, nextx, y - tableHeight);
			nextx += colWidth;
		}

		// now add the text
		contentStream.setFont(PDType1Font.HELVETICA, 10);

		float textx = margin + cellMargin;
		float texty = y - 13;
		textx += colWidth;
		for (int i = 0; i < (cols - 1); i++) {
			// fill in the the time columns
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(textx, texty);

			contentStream.drawString(qMeet.getMeet().get(0).getRoom().get(i).getName());
			contentStream.moveTextPositionByAmount(0, -10);

			contentStream.endText();
			textx += colWidth;

		}

		textx = margin + cellMargin;
		texty = y - 15;
		texty -= 15;
		textx += colWidth;
		for (int i = 0; i < (cols - 1); i++) {
			String qMasters[] = qMeet.getMeet().get(0).getRoom().get(i).getQuizmasters().split(",");

			// fill in the the time columns
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(textx, texty);
			for (String master : qMasters) {
				contentStream.drawString(master);
				contentStream.moveTextPositionByAmount(0, -10);
			}
			contentStream.endText();
			textx += colWidth;

		}

		texty -= rowHeight;
		textx = margin + cellMargin;

		for (int i = start; i < end; i++) {
			// fill in the the time columns
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(textx, texty);
			contentStream.drawString("Quiz " + (i + 1));
			contentStream.moveTextPositionByAmount(0, -10);
			contentStream.drawString(qMeet.getSlot().get(i).getStart() + " - " + qMeet.getSlot().get(i).getEnd());
			contentStream.endText();
			texty -= rowHeight;
		}

		texty = y - 15;
		texty -= 15;
		texty -= rowHeight;

		textx += textx + 15;
		for (int i = start; i < end; i++) {
			for (int j = 0; j < qMeet.getMeet().get(0).getRoom().size(); j++) {
				if (qMeet.getSlot().get(i).getQuiz().size() > j) {
					contentStream.beginText();
					contentStream.moveTextPositionByAmount(textx, texty);
					contentStream.drawString(qMeet.getSlot().get(i).getQuiz().get(j).getTeam1() != null ? qMeet.getSlot().get(i).getQuiz().get(j).getTeam1() : "");
					contentStream.moveTextPositionByAmount(0, -10);
					contentStream.drawString(qMeet.getSlot().get(i).getQuiz().get(j).getTeam2() != null ? qMeet.getSlot().get(i).getQuiz().get(j).getTeam2() : "");
					contentStream.moveTextPositionByAmount(0, -10);
					contentStream.drawString(qMeet.getSlot().get(i).getQuiz().get(j).getTeam3() != null ? qMeet.getSlot().get(i).getQuiz().get(j).getTeam3() : "");
					contentStream.endText();
					contentStream.close();
				}
				// PDPageContentStream team2 = new PDPageContentStream(doc,
				// page,
				// true, true);
				// team2.setFont(PDType1Font.HELVETICA, 10);
				// team2.beginText();
				// team2.moveTextPositionByAmount(textx, texty);
				// team2.drawString("TEST");
				// team2.close();

				textx += colWidth;
			}
			texty -= rowHeight;
			textx = margin + cellMargin;
			textx += textx + 15;
		}
	}

	public void swapIdWithNames(Quiz quiz) {
		quiz.setTeam1(getTeam(quiz.getTeam1()).getName());
		quiz.setTeam2(getTeam(quiz.getTeam2()).getName());
		quiz.setTeam3(getTeam(quiz.getTeam3()).getName());
	}

	public Church getChurch(String id) {
		for (Church c : churchs.getChurch()) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}
}
