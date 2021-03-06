package lab8;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;


public class MahjongGame extends JFrame {

	private static final int BOARD_WIDTH = 950;
	private static final int BOARD_HEIGHT = 650;
	private PlayingBoard playingBoard;

	private final String title = "Garrett Glenn Mah-Jong \t";
	
	private JFrame myJframe = null;
	private JMenuItem menuItems = null;
	private JFrame helperFrame = new JFrame();

	public MahjongGame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(BOARD_WIDTH, BOARD_HEIGHT);
		setLayout(new BorderLayout());

		playingBoard = new PlayingBoard(getContentPane().getWidth(),
				getContentPane().getHeight(), true);

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("New Game");
		item.setToolTipText("Begins a new, random game.");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = JOptionPane.showConfirmDialog(
						(JMenuItem) e.getSource(),
						"Abandon current game and start a new one?",
						"New Game",
						JOptionPane.YES_NO_OPTION
						);
				if (selection == 0) {
						newGame();
				}
			}
		});
		menu.add(item);

		item = new JMenuItem("New Numbered Game");
		item.setToolTipText("Begins a new game given a game number.");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String number = JOptionPane.showInputDialog(
						"Track a game");
				if (Helper.isInteger(number) && number.length() == 6) {
					newGame(Long.parseLong(number));
				}
				else if (number != null) {
					JOptionPane.showMessageDialog((JMenuItem) e.getSource(),
							"error enter a six digit number",
							"retry",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		menu.add(item);

		item = new JMenuItem("Restart");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = JOptionPane.showConfirmDialog(
						(JMenuItem) e.getSource(),
						"Quit now and start over?",
						"New Game",
						JOptionPane.YES_NO_OPTION
						);
				if (selection == 0) {
					newGame(playingBoard.trackGame);
				}
			}
		});
		menu.add(item);

		menu.addSeparator();



	

		item = new JMenuItem("Save Game");
		item.setEnabled(false);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		item.setEnabled(false);

		item = new JMenuItem("Exit Game");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		menu.add(item);	

		menubar.add(menu);

		menu = new JMenu("Options");

		item = new JMenuItem("LeaderBoard");
		item.setEnabled(false);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		menu.add(item);

		menuItems = new JMenuItem("Removed Tiles");
		menuItems.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (myJframe == null) {
					initRemovedFrame();
				}
				myJframe.setVisible(true);
				menuItems.setEnabled(false);
			}
		});
		menu.add(menuItems);

		menubar.add(menu);
		setJMenuBar(menubar);

		item = new JMenuItem("Game Rules");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel();
				JTextArea text = new JTextArea();
				JScrollPane scrollPane = new JScrollPane(text);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				text.setTabSize(2);
				scrollPane.setPreferredSize(new Dimension(550, 350));
				panel.add(scrollPane);
				helperFrame.setTitle("Game Rules");
				helperFrame.add(panel);
				helperFrame.setVisible(true);
				helperFrame.setSize(600, 400);
			}
		});
		menu.add(item);

		menubar.add(menu);

		helperFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				helperFrame.dispose();
				helperFrame = new JFrame();
				helperFrame.addWindowListener(this);
			}
		});

		add(playingBoard);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int selection =
						JOptionPane.showConfirmDialog(e.getComponent(),
						"Do you want to quit the game?",
						"Exit Game",
						JOptionPane.YES_NO_OPTION);
				if (selection == 0) {
					System.exit(0);
				}
			}
		});

	setTitle(title + playingBoard.trackGame);
		setResizable(false);
		setVisible(true);
	}

	private void initRemovedFrame() {
		myJframe = new JFrame("Removed Tiles");
		JScrollPane scrollPane = new JScrollPane(playingBoard.getRemovedPanel());
		myJframe.add(scrollPane);
		myJframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				menuItems.setEnabled(true);
			}
		});
		playingBoard.resizeRemovedFrame();
	}


	private void close() {
		WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
	}
	private void newGame() {
		newGame(null);
	}
	private void newGame(Long randomNumber) {
		
		remove(playingBoard);

		int width = getContentPane().getWidth();
		int height = getContentPane().getHeight();

		if (randomNumber == null) {
			playingBoard = new PlayingBoard(width, height);
		}
		else {
			playingBoard = new PlayingBoard(width, height, randomNumber);
		}

		add(playingBoard);

		

		setTitle(title + playingBoard.trackGame);
		repaint();
	}

	public static void main(String[] args) {
		MahjongGame mahjongBoard = new MahjongGame();
	}

}
