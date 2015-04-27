package lab8;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class PlayingBoard extends JPanel implements MouseListener {

	
	private static Random random;
	private Map<String, Tile> board = new HashMap<>();
	private Stack<Tile> tilesDeleted = new Stack<>();
	private Stack<Tile> tilesRestored = new Stack<>();
	private String[] backgrounds = {"scroll.png"};
	private String backgroundString;
	protected long trackGame;
	private Tile tileChosen;
	private JPanel removedPanel = null;
	private boolean init = true;

	public PlayingBoard(int width, int height) {
		this(width, height, true);
	}

	public PlayingBoard(int width, int height, boolean drawRound) {
		this(width, height, drawRound, null);
	}

	public PlayingBoard(int width, int height, Long randomNumber) {
		this(width, height, true, randomNumber);
	}

	public PlayingBoard(int width, int height, boolean drawRound,
			Long randomNumberGame) {
		if (randomNumberGame == null) {
			randomNumberGame = new Date().getTime() % 1000000;
		}
		trackGame = randomNumberGame;
		random = new Random(randomNumberGame);
		initialize(width, height, drawRound);
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (backgroundString != null) {
			ImageIcon image = new ImageIcon(getClass().getResource(backgroundString));
			int xDiff = getWidth() - image.getIconWidth();
			int yDiff = getHeight() - image.getIconHeight();
			if (xDiff < 0 || yDiff < 0) {
				int diff = xDiff - yDiff;
				image = new ImageIcon(image.getImage().getScaledInstance(diff >= 0 ? getWidth() : -1, diff < 0 ? getHeight() : -1, Image.SCALE_DEFAULT));
			}
			g.drawImage(image.getImage(), getWidth() / 2 - image.getIconWidth() / 2, getHeight() / 2 - image.getIconHeight() / 2, this);
		}

		redraw();
	}

	/**
	 * method that builds the board and decides which corners to implement
	 *
	 * @param width the integer width of the game board
	 * @param height the integer height of the game board
	 * @param drawRound a boolean indicating whether to draw round corners
	 */
	private void initialize(int width, int height, boolean drawRound) {
		setLayout(null);
		setSize(width, height);

		int number = random.nextInt(backgrounds.length);
		backgroundString = "images/" + backgrounds[number];

		List<Tile> deck = initializeDeck(drawRound);

		// Start at the top middle tile
		int xPos = 0;
		int yPos = 0;
		int zPos = 0;

		// Every tile gets set to the correct size, then we set the position
		// and put the tile into our board
		for (Tile tile : deck) {
			tile.setSize(Tile.WIDTH, Tile.HEIGHT);
			tile.xPos = xPos;
			tile.yPos = yPos;
			tile.zPos = zPos;

			tile.addMouseListener(this);
			board.put(getKey(xPos, yPos, zPos), tile);

			int[] newPositions = getNewPositions(xPos, yPos, zPos);
			xPos = newPositions[0];
			yPos = newPositions[1];
			zPos = newPositions[2];
		}

		addMouseListener(this);
	}

	/**
	 * This method handles the logic behind adding the coordinates for tiles,
	 * for example the transition from layer to layer, and the three special
	 * case tiles
	 *
	 * @param currentXPos
	 * @param currentYPos
	 * @param currentZPos
	 * @return a three element array, with 0 being the new xPos, 1 being the new
	 * yPos, and 2 being the new zPos
	 */
	private int[] getNewPositions(int currentXPos, int currentYPos, int currentZPos) {
		int maxX;
		int maxY;

		switch (currentZPos) {
			case 0:
				maxX = 0;
				maxY = 0;
				break;
			case 1:
				maxX = 1;
				maxY = 1;
				break;
			case 2:
				maxX = 2;
				maxY = 2;
				break;
			case 3:
				maxX = 3;
				maxY = 3;
				break;
			case 4:
				maxY = 4;
				switch (Math.abs(currentYPos)) {
					case 1:
					// Intentional fall-through, both have max as 6
					case 4:
						maxX = 6;
						break;
					case 2:
						maxX = 5;
						break;
					case 3:
						maxX = 4;
						break;
					default:
						maxX = 0;
				}
				break;
			default:
				maxX = 0;
				maxY = 0;
				break;
		}

		int absXPos = Math.abs(currentXPos);
		int absYPos = Math.abs(currentYPos);

		if (currentZPos == 0) {
			currentXPos = 1;
			currentYPos = 1;
			currentZPos = 1;
		} // For the middle three layers, it's just a square
		else {
			// Adds or subtracts one from the current x if we haven't reached
			// the end of the row
			if (currentYPos != 0 && absXPos < maxX) {
				// This is either 1 or -1, depending on which direction we are
				// going already
				currentXPos += currentXPos / absXPos;
			} // Otherwise, we have to decide whether to add/subtract one for y,
			// or whether the y side is done and we need to start at the next
			// center tile
			else if (currentYPos != 0 && absYPos < maxY) {
				currentYPos += currentYPos / absYPos;
				currentXPos = currentXPos / absXPos;
			} else if (currentXPos > 0 && currentYPos > 0) {
				currentXPos = 1;
				currentYPos = -1;
			} else if (currentXPos > 0 && currentYPos < 0) {
				currentXPos = -1;
				currentYPos = -1;
			} else if (currentXPos < 0 && currentYPos < 0) {
				currentXPos = -1;
				currentYPos = 1;
			} else if (currentXPos < 0 && currentYPos > 0 && currentZPos <= 4) {
				currentXPos = 1;
				currentYPos = 1;
				currentZPos++;
			} // The three special case tiles are inserted here
			else if (currentZPos == 4 && currentYPos == 0) {
				switch (currentXPos) {
					case -7:
						currentXPos = 7;
						break;
					case 7:
						currentXPos = 8;
						break;
					default:
						break;
				}
			}

			// This catches us if we started going one layer too deep
			if (currentZPos == 5) {
				currentXPos = -7;
				currentYPos = 0;
				currentZPos = 4;
			}
		}

		int[] positions = new int[]{currentXPos, currentYPos, currentZPos};
		return positions;
	}

	/**
	 * Adds all of the tiles to the deck, and 'shuffles' them
	 *
	 * @param drawRound a boolean indicating whether to draw rounded corners
	 * @return
	 */
	private List<Tile> initializeDeck(Boolean drawRound) {
		List<Tile> deck = new ArrayList<>();

		for (int i = 0; i < 4; i++) {

			// //adding all chineese character tiles
			deck.add(new CharacterTile('1'));
			deck.add(new CharacterTile('2'));
			deck.add(new CharacterTile('3'));
			deck.add(new CharacterTile('4'));
			deck.add(new CharacterTile('5'));
			deck.add(new CharacterTile('6'));
			deck.add(new CharacterTile('7'));
			deck.add(new CharacterTile('8'));
			deck.add(new CharacterTile('9'));

			// //adding all wind tiles
			deck.add(new CharacterTile('N'));
			deck.add(new CharacterTile('S'));
			deck.add(new CharacterTile('E'));
			deck.add(new CharacterTile('W'));

			// //adding all dragon tiles
			deck.add(new CharacterTile('C'));
			deck.add(new CharacterTile('F'));
			deck.add(new WhiteDragonTile());

			//adding all bamboo tiles
			deck.add(new Bamboo1Tile());
			deck.add(new BambooTile(2));
			deck.add(new BambooTile(3));
			deck.add(new BambooTile(4));
			deck.add(new BambooTile(5));
			deck.add(new BambooTile(6));
			deck.add(new BambooTile(7));
			deck.add(new BambooTile(8));
			deck.add(new BambooTile(9));

			//adding all circles tiles
			deck.add(new CircleTile(1));
			deck.add(new CircleTile(2));
			deck.add(new CircleTile(3));
			deck.add(new CircleTile(4));
			deck.add(new CircleTile(5));
			deck.add(new CircleTile(6));
			deck.add(new CircleTile(7));
			deck.add(new CircleTile(8));
			deck.add(new CircleTile(9));

		}

		deck.add(new FlowerTile("Chrysanthemum"));
		deck.add(new FlowerTile("Orchid"));
		deck.add(new FlowerTile("Plum"));
		deck.add(new FlowerTile("Bamboo"));
		deck.add(new SeasonTile("Spring"));
		deck.add(new SeasonTile("Summer"));
		deck.add(new SeasonTile("Fall"));
		deck.add(new SeasonTile("Winter"));

                   //randomly shuffles deck
		Collections.shuffle(deck, random);

		// this tells the tile how to draw
		if (drawRound != null) {
			for (Tile tile : deck) {
				tile.drawRound = drawRound;
			}
		}

		return deck;
	}
       

	protected int getRemovedTileCount() {
		return tilesDeleted.size();
	}
        
	

	/**
	 * NOw we apply the tiles to the game board
	 */
	private void redraw() {
		List<Tile> deck = new ArrayList<>();
		deck.addAll(board.values());
		Collections.sort(deck);

		for (Tile tile : deck) {
			if (!init && !tile.isDirty) {
				continue;
			}
			tile.place(getWidth(), getHeight());
			add(tile);
			tile.isDirty = false;
		}

		// zorder command
		if (!init) {
			int newZOrder = 0;
			for (Tile tile: deck) {
				setComponentZOrder(tile, newZOrder++);
			}
		}
		init = false;
	}

	/**
	 * Returns the delimited key for the given x, y, and z values This function
	 * is intended to help prevent typing or coding errors
	 *
	 * @param xPos
	 * @param yPos
	 * @param zPos
	 * @return a string in the format required for the board (current
	 * <xPos>_<yPos>_<zPos>, for version 0.1 - don't count on it!)
	 */
	private String getKey(int xPos, int yPos, int zPos) {
		return xPos + "_" + yPos + "_" + zPos;
	}

	/**
	 * Removes a specified tile and its listener from the game board
	 *
	 * @param xPos the integer logical x position of the tile to remove
	 * @param yPos the integer logical y position of the tile to remove
	 * @param zPos the integer logical z position of the tile to remove
	 * @return the Tile that was removed from the game board
	 */
	public Tile tilesDeletion(int xPos, int yPos, int zPos) {
		Tile tile = board.remove(getKey(xPos, yPos, zPos));
		int stackSize = tilesDeleted.size();
		if (tile == null) {
			System.err.println("missing the title");
		} else {
			
			tile.isDirty = true;
			tilesDeleted.push(tile);
			remove(tile);
			redraw();
			repaint();
		}
		if (stackSize % 2 != 0 && tilesDeleted.size() > stackSize && removedPanel != null) {
			removedPanel.removeAll();
			for (Tile t: tilesDeleted) {
				removedPanel.add(t, 0);
			}
			resizeRemovedFrame();
			removedPanel.revalidate();
		}
		return tile;
	}

	protected void resizeRemovedFrame() {
			int tileCount = getRemovedTileCount() <= 0 ? 1 : getRemovedTileCount();
			int height = tileCount * (Tile.HEIGHT + 120 / tileCount) / 2 + 50;
			height = height < Tile.HEIGHT + 50 ? Tile.HEIGHT + 50 :
				height > 450 ? 500 : height;
			removedPanel.getTopLevelAncestor().setSize(250, height);

			if (height > 500) {
				removedPanel.getTopLevelAncestor().revalidate();
			}
	}
	/**
	 * Removes a specified tile from the game board
	 *
	 * @param tile the Tile to remove
	 * @return the Tile that was removed, or null if the tile didn't exist
	 */
	public Tile tilesDeletion(Tile tile) {
		if (tile == null) {
			System.err.println("The tile to remove was null.");
			return null;
		}
		if (tile.xPos == null || tile.yPos == null || tile.zPos == null) {
			System.err.println("The tile hasn't been placed yet.");
			return null;
		}
		return tilesDeletion(tile.xPos, tile.yPos, tile.zPos);
	}
	

	/**
	 * Gets the tile at a given position, if it exists, or null otherwise
	 *
	 * @param xPos
	 * @param yPos
	 * @param zPos
	 * @return
	 */
	public Tile getTile(int xPos, int yPos, int zPos) {
		return board.get(getKey(xPos, yPos, zPos));
	}

	public boolean isOpen(int xPos, int yPos, int zPos) {
		// Checks the z to see if it is closed from above
		if (getTile(xPos, yPos, zPos - 1) != null) {
			return false;
		}

		boolean isOpenLeft = getTile(xPos - 1, yPos, zPos) == null
				&& (xPos != 1 || getTile(xPos - 2, yPos, zPos) == null);
		boolean isOpenRight = getTile(xPos + 1, yPos, zPos) == null
				&& (xPos != -1 || getTile(xPos + 2, yPos, zPos) == null);
		boolean isMiddleY = Math.abs(yPos) == 1;
		boolean isZeroY = yPos == 0;
		// Checks for the individual tiles on the end in the middle rows
		if (isOpenLeft && isMiddleY) {
			isOpenLeft = getTile(xPos - 1, 0, zPos) == null;
		} else if (isOpenLeft && isZeroY) {
			isOpenLeft = getTile(xPos - 1, 1, zPos) == null
					&& getTile(xPos - 1, -1, zPos) == null;
		}
		if (isOpenRight && isMiddleY) {
			isOpenRight = getTile(xPos + 1, 0, zPos) == null;
		}

		// Checks for the z being the top middle square covering the four
		// top tiles
		if (isMiddleY && Math.abs(xPos) == 1 && getTile(0, 0, 0) != null) {
			return false;
		}
		if (!isOpenLeft && !isOpenRight) {
			return false;
		}

		if (Math.abs(xPos) == 1) {
		}

		return true;
	}

	public boolean isOpen(Tile tile) {
		if (tile == null
				|| tile.xPos == null || tile.yPos == null || tile.zPos == null) {
			System.err.println("Invalid tile to check for open.");
			return false;
		}

		return isOpen(tile.xPos, tile.yPos, tile.zPos);
	}

	/**
	 * Returns true if there are any moves available to redo
	 *
	 * @return
	 */
	

	/**
	 * Returns true if there are any moves to undo
	 *
	 * @return
	 */
	

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && !(e.getSource() instanceof Tile)) {
			JPopupMenu popup = new JPopupMenu();

			
			

			popup.show(this, e.getX(), e.getY());
			return;
		} else if (e.isPopupTrigger()) {
			return;
		}
		Object source = e.getSource();
		if (source instanceof Tile && isOpen((Tile) source)) {
			Tile tile = (Tile) source;
			tilesRestored.clear();
			if (tileChosen != null && tileChosen != tile
					&& tileChosen.matches(tile)) {
				tilesDeletion(tile);
				tileChosen.highlight(false);
				tilesDeletion(tileChosen);
				
				
				tileChosen = null;
			} else if (tileChosen == tile) {
				tileChosen.highlight(false);
				tileChosen = null;
				repaint();
			} else if (tileChosen != null) {
				tileChosen.highlight(false);
				tileChosen = tile;
				tile.highlight(true);
				repaint();
			} else {
				tileChosen = tile;
				tileChosen.highlight(true);
				repaint();
			}
		} else if (!(source instanceof Tile)) {
			if (tileChosen != null) {
				tileChosen.highlight(false);
				tileChosen = null;
				repaint();
			}
		}
	}

	protected JPanel getRemovedPanel() {
		if (removedPanel == null) {
			GridLayout layout = new GridLayout(0, 2, 5, 5);
			removedPanel = new JPanel(layout);
			for (Tile tile: tilesDeleted) {
				removedPanel.add(tile, 0);
			}
		}
		return removedPanel;
	}

	
}
