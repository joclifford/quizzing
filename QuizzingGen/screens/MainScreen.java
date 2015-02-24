package screens;

import helpers.Match;
import helpers.MeetHelper;
import helpers.MeetUtil;
import helpers.Possibilities;
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
			lblStatus.setBounds(300, 11, 700, 23);
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

			JButton optThis = new JButton("Optimize Meet");
			optThis.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					optimizeMeet(m.getId(), schedules.getSchedule().get(0));
				}
			});
			optThis.setBounds(550, 10, 150, 25);
			meetPanel.add(optThis);

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
		boolean morning = false;
		boolean afternoon = false;
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		for (TeamHelper th : teamValues.values()) {
			if (!"".equals(th.getTeamName()) && th.getTeamName() != null)
				count.put(th.getTeamName(), 0);
		}
		for (QuizMeet qm : schedules.getSchedule().get(0).getQuizMeet()) {
			if (qm.getId().equals(id)) {
				for (int i = 0; i < qm.getSlot().size(); i++) {
					if ((i - 1) > -1) {
						for (Quiz quiz : qm.getSlot().get(i).getQuiz()) {

							if (quiz.getTeam1() != null && quiz.getTeam2() != null && quiz.getTeam3() != null) {
								MeetUtil.updateCount(count, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());
								if (MeetUtil.inSlot(qm.getSlot().get(i - 1), quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3())) {
									backToBackCount++;
									if (MeetUtil.inSlot(qm.getSlot().get(i - 1), quiz.getTeam1())) {
										if (backToBacks.contains(quiz.getTeam1())) {
											teamsWithTwo++;
										} else {
											backToBacks.add(quiz.getTeam1());
										}
									}
									if (MeetUtil.inSlot(qm.getSlot().get(i - 1), quiz.getTeam2())) {
										if (backToBacks.contains(quiz.getTeam2())) {
											teamsWithTwo++;
										} else {
											backToBacks.add(quiz.getTeam2());
										}
									}
									if (MeetUtil.inSlot(qm.getSlot().get(i - 1), quiz.getTeam3())) {
										if (backToBacks.contains(quiz.getTeam3())) {
											teamsWithTwo++;
										} else {
											backToBacks.add(quiz.getTeam3());
										}
									}
								}
							}
						}

					} else {
						for (Quiz quiz : qm.getSlot().get(i).getQuiz()) {
							MeetUtil.updateCount(count, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());
						}
					}
					if (i == 6) {
						morning = MeetUtil.checkCount(count);
						for (TeamHelper th : teamValues.values()) {
							if (!"".equals(th.getTeamName()) && th.getTeamName() != null)
								count.put(th.getTeamName(), 0);
						}
					}
				}
				afternoon = MeetUtil.checkCount(count);
			}
		}
		String msg = "There is '" + backToBackCount + "' back to back quizzes. And '" + teamsWithTwo + "' teams have more than one." + "Morning: " + (morning ? "Good" : "Bad") + ". Afternoon: " + (afternoon ? "Good" : "Bad");
		lblStatus.setText(msg);

	}

	private void printSlotStats(List<Slot> slots) {
		int backToBackCount = 0;
		int teamsWithTwo = 0;
		List<String> backToBacks = new ArrayList<String>();

		for (int i = 0; i < slots.size(); i++) {
			if ((i - 1) > -1) {
				for (Quiz quiz : slots.get(i).getQuiz()) {
					if (MeetUtil.inSlot(slots.get(i - 1), quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3())) {
						backToBackCount++;
						if (MeetUtil.inSlot(slots.get(i - 1), quiz.getTeam1())) {
							if (backToBacks.contains(quiz.getTeam1())) {
								teamsWithTwo++;
							} else {
								backToBacks.add(quiz.getTeam1());
							}
						}
						if (MeetUtil.inSlot(slots.get(i - 1), quiz.getTeam2())) {
							if (backToBacks.contains(quiz.getTeam2())) {
								teamsWithTwo++;
							} else {
								backToBacks.add(quiz.getTeam2());
							}
						}
						if (MeetUtil.inSlot(slots.get(i - 1), quiz.getTeam3())) {
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
				QuizMeet qm = generateQuizMeet(meet, matches); // generateMeet(meet,
																// matches);

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

			List<Slot> alreadyHappened = new ArrayList<Slot>();
			List<Quiz> quizzes = getAllPossibleQuizzes(alreadyHappened);

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

					addLeftoverQuizzes(quizzes, morningQuizzing, meet.getRoom().size());
					quizzes.clear();
					quizzes = getAllPossibleQuizzes(morningQuizzing);
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
						Iterator<Quiz> itr = quizzes.iterator();
						while (itr.hasNext()) {
							Quiz q = itr.next();
							if (!MeetUtil.inSlot(slot, getTeam(q.getTeam1()).getName(), getTeam(q.getTeam2()).getName(), getTeam(q.getTeam3()).getName())) {
								if (MeetUtil.inSlot(lastSlot, getTeam(q.getTeam1()).getName(), getTeam(q.getTeam2()).getName(), getTeam(q.getTeam3()).getName())) {
									if (backToBacks.contains(getTeam(q.getTeam1()).getName())) {
										continue;
									}
									if (backToBacks.contains(getTeam(q.getTeam2()).getName())) {
										continue;
									}
									if (backToBacks.contains(getTeam(q.getTeam3()).getName())) {
										continue;
									}

									if (MeetUtil.inSlot(lastSlot, getTeam(q.getTeam1()).getName())) {
										backToBacks.add(getTeam(q.getTeam1()).getName());
									}
									if (MeetUtil.inSlot(lastSlot, getTeam(q.getTeam2()).getName())) {
										backToBacks.add(getTeam(q.getTeam2()).getName());
									}
									if (MeetUtil.inSlot(lastSlot, getTeam(q.getTeam3()).getName())) {
										backToBacks.add(getTeam(q.getTeam3()).getName());
									}
								}
								team1 = getTeam(q.getTeam1());
								team2 = getTeam(q.getTeam2());
								team3 = getTeam(q.getTeam3());
								MeetUtil.updateCount(count, team1, team2, team3);

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
			addLeftoverQuizzes(quizzes, afternoonQuizzing, meet.getRoom().size());
			qMeet.getSlot().addAll(afternoonQuizzing);
			return qMeet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// New and hopefully improved version
	public QuizMeet generateQuizMeet(Meet meet, Set<String> matches) {
		StringBuilder statusMsg = new StringBuilder();
		QuizMeet qMeet = new QuizMeet();
		qMeet = new QuizMeet();
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		List<Slot> morningQuizzing = new ArrayList<Slot>();
		List<Slot> afternoonQuizzing = new ArrayList<Slot>();
		Set<String> matchesInThisQuiz = new HashSet<String>();
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

			ArrayList<Team> teamsToQuiz = new ArrayList<Team>(teams);
			for (int i = 0; i < slots; i++) {
				Slot slot = new Slot();
				MeetUtil.refillTeamsIfNeeded(matches, count, teams, teamsToQuiz, slot);

				// /////////TIME
				// UPDATING///////////////////////////////////////////
				if (start.toString().substring(0, 5).equals("12:20")) {
					start.add(hour);
					boolean allGood = MeetUtil.checkCount(count);
					statusMsg.append("Morning: " + (allGood ? "Good" : "Bad"));

					String status = MeetUtil.checkCount(count) ? "Good" : "Bad";
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

					Quiz quiz = MeetUtil.getQuiz(count, matches, teamsToQuiz, slot);
					boolean listRefilled = false;
					while (quiz == null || quiz.getTeam1() == null || quiz.getTeam2() == null || quiz.getTeam3() == null || MeetUtil.inSlot(slot, getTeam(quiz.getTeam1()).getName(), getTeam(quiz.getTeam2()).getName(), getTeam(quiz.getTeam3()).getName())) {
						if (MeetUtil.amountRemaining(count, teams) == 0) {
							break;
						}
						quiz = MeetUtil.getQuiz(count, matches, teamsToQuiz, slot);
						if (!listRefilled) {
							listRefilled = true;
						} else {
							MeetUtil.refillTeamsIfNeeded(matches, count, teams, teamsToQuiz, slot);
							// allTeamsQuizzed(matches, teams);
							j = -1;
							resetSlot(count, matches, slot);
							continue slot;
						}
					}

					if (quiz != null) {
						MeetUtil.updateCount(count, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());
						Iterator<Team> itr = teamsToQuiz.iterator();
						while (itr.hasNext()) {
							Team team = itr.next();
							if (team.getId().equals(quiz.getTeam1()) || team.getId().equals(quiz.getTeam2()) || team.getId().equals(quiz.getTeam3())) {
								itr.remove();
							}
						}
						swapIdWithNames(quiz);

						if (!MeetUtil.hasQuizzed(matches, quiz.getTeam1(), quiz.getTeam1())) {
							matches.add(quiz.getTeam1() + ":" + quiz.getTeam2());
						} else {
							matches.add(quiz.getTeam1() + "::" + quiz.getTeam2());
						}
						matchesInThisQuiz.add(quiz.getTeam1() + ":" + quiz.getTeam2());

						if (!MeetUtil.hasQuizzed(matches, quiz.getTeam1(), quiz.getTeam3())) {
							matches.add(quiz.getTeam1() + ":" + quiz.getTeam3());
						} else {
							matches.add(quiz.getTeam1() + "::" + quiz.getTeam3());
						}
						matchesInThisQuiz.add(quiz.getTeam1() + ":" + quiz.getTeam3());

						if (!MeetUtil.hasQuizzed(matches, quiz.getTeam2(), quiz.getTeam3())) {
							matches.add(quiz.getTeam2() + ":" + quiz.getTeam3());
						} else {
							matches.add(quiz.getTeam2() + "::" + quiz.getTeam3());
						}
						matchesInThisQuiz.add(quiz.getTeam1() + ":" + quiz.getTeam3());

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
		String status = MeetUtil.checkCount(count) ? "Good" : "Bad";
		System.out.println("---AFTERNOON DONE--Status:" + status + "---");
		MeetUtil.sortSlots(morningQuizzing, meet.getRoom().size());
		MeetUtil.sortSlots(afternoonQuizzing, meet.getRoom().size());
		qMeet.getSlot().addAll(morningQuizzing);
		qMeet.getSlot().addAll(afternoonQuizzing);

		boolean allGood = MeetUtil.checkCount(count);
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
					if (!MeetUtil.inSlot(s, getTeam(leftOver.get(i).getTeam1()).getName(), getTeam(leftOver.get(i).getTeam2()).getName(), getTeam(leftOver.get(i).getTeam3()).getName())) {
						for (int j = 0; j < s.getQuiz().size(); j++) {
							if (s.getQuiz().get(j).getTeam1() != null) {
								if (!MeetUtil.inSlot(slots.get(slots.size() - 1), s.getQuiz().get(j).getTeam1(), s.getQuiz().get(j).getTeam2(), s.getQuiz().get(j).getTeam3())) {
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

	@SuppressWarnings("unchecked")
	public void getNewSection(List<Team> teams, Set<String> matches, HashMap<String, Integer> count, List<Quiz> section) {
		Set<String> sectionMatches = (Set<String>) ((HashSet<String>) matches).clone();
		while (MeetUtil.amountRemaining(count, teams) > 0) {
			try {
				Quiz quiz = MeetUtil.getQuiz(count, sectionMatches, (ArrayList<Team>) teams, null);

				MeetUtil.updateCount(count, quiz.getTeam1(), quiz.getTeam2(), quiz.getTeam3());

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
				sectionMatches = (Set<String>) ((HashSet<String>) matches).clone();
			}

		}

		for (Quiz quiz : section) {
			matches.add(quiz.getTeam1() + ":" + quiz.getTeam2());
			matches.add(quiz.getTeam1() + ":" + quiz.getTeam3());
			matches.add(quiz.getTeam2() + ":" + quiz.getTeam3());
		}

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

	public List<Quiz> getAllPossibleQuizzes(List<Slot> alreadyHappened) {
		List<Quiz> quizzes = new ArrayList<Quiz>();

		Possibilities p = new Possibilities();
		p.findPossibilities(alreadyHappened);
		List<Match> ms = p.getMs();
		System.out.println("------------------------");
		Iterator<Match> itr = ms.iterator();
		while (itr.hasNext()) {
			Match m = itr.next();
			if (m.getRow() == -1) {
				itr.remove();
			}
		}
		for (Match m : ms) {
			Quiz quiz = new Quiz();
			quiz.setTeam1(m.getTeam(0).getName());
			quiz.setTeam2(m.getTeam(1).getName());
			quiz.setTeam3(m.getTeam(2).getName());
			quizzes.add(quiz);
		}
		return quizzes;

	}

	private void optimizeMeet(String id, Schedule schedule) {
		List<Slot> morningQuizzing = new ArrayList<Slot>();
		List<Slot> afternoonQuizzing = new ArrayList<Slot>();
		for (QuizMeet qm : schedule.getQuizMeet()) {
			if (qm.getId().equals(id)) {
				for (int i = 0; i < 7; i++) {
					morningQuizzing.add(qm.getSlot().get(i));
				}
				MeetUtil.sortSlots(morningQuizzing, 6);
				for (int i = 7; i < 14; i++) {
					afternoonQuizzing.add(qm.getSlot().get(i));
				}
				MeetUtil.sortSlots(afternoonQuizzing, 6);
			}
		}

		List<Team> teams = new ArrayList<Team>();
		for (TeamHelper helper : teamValues.values()) {
			if (!"".equals(helper.getTeamName())) {
				teams.add(helper.getTeamObject());
			}
		}

		// trips in the morning
		HashMap<Team, List<String>> trips = new HashMap<Team, List<String>>();
		MeetUtil.identifyTrips(morningQuizzing, teams, trips);

		HashMap<Team, List<String>> afternoonTrips = new HashMap<Team, List<String>>();
		MeetUtil.identifyTrips(afternoonQuizzing, teams, afternoonTrips);

		while (trips.size() != 0) {
			MeetUtil.sortTriples(morningQuizzing, trips);
			MeetUtil.identifyTrips(morningQuizzing, teams, trips);
		}
		while (afternoonTrips.size() != 0) {
			MeetUtil.sortTriples(afternoonQuizzing, afternoonTrips);
			MeetUtil.identifyTrips(afternoonQuizzing, teams, afternoonTrips);
		}
		MeetUtil.checkForRepeatMatches(schedules.getSchedule().get(0));
		System.out.println("Optimized");
		addSchedule(schedules.getSchedule().get(0));
		return;
	}
}
