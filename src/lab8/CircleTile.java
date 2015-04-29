package lab8;

import java.awt.Color;
import java.awt.Graphics;

public class CircleTile extends RankTile {
	

	public CircleTile(int rank) {
        super(rank);
		
		setToolTipText(toString());
    }
    
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int size;
		int xStart;
		int yStart;
		int spacing;
		Color[] colors;
		
		switch (rank) {
		case 1:
			size = 50;
			g.setColor(GREEN);
			g.fillOval(12, 9, size, size);
			
			size = 13;
			g.setColor(RED);
			g.fillOval(31, 28, size, size);
			drawSymbol(31, 28, size, g);
			
                        // logic applied to draw my circles
			size = 5;
			int numCircles = 12;
			int x1 = 35;
			int y1 = 32;
			double rotation = 2 * Math.PI / numCircles;
			
			for (int circle = 0; circle < numCircles; circle ++) {
				double position = circle * rotation;
				
				g.setColor(WHITE);
				g.fillOval((int) (x1 + Math.cos(position) * 20), 
						(int) (y1 + Math.sin(position) * 20), 
						size, size);
			}
			break;
		case 2:
			size = 23;
			xStart = 26;
			yStart = 8;
			spacing = 4;
			
			straightCirlces(size, 2, xStart, yStart, spacing, new Color[] {GREEN, RED}, g);
			break;
		case 3:
			size = 17;
			xStart = 13;
			yStart = 8;
			colors = new Color[] {BLUE, RED, GREEN};
			
			drawDiagonalCircles(size, 3, xStart, yStart, -1, 0, colors, g);
			break;
		case 4:
			size = 23;
			xStart = 15;
			yStart = 8;
			spacing = 4;
			
			straightCirlces(23, 2, xStart, yStart, spacing, new Color[]{BLUE, GREEN}, g);
			straightCirlces(23, 2, xStart + size, yStart, spacing, new Color[]{GREEN, BLUE}, g);			
			break;
		case 5:
			size = 17;
			xStart = 12;
			yStart = 5;
			spacing = 2 + size;
			colors = new Color[] {BLUE, GREEN};
			
			straightCirlces(size, 2, xStart, yStart, spacing, colors, g);
			
			colors = new Color[] {RED};
			straightCirlces(size, 1, xStart + size, yStart + size + 2, spacing, colors, g);
			
			colors = new Color[] {GREEN, BLUE};
			straightCirlces(size, 2, xStart + size * 2,yStart,  spacing, colors, g);
			break;
		case 6:
			size = 17;
			xStart = 18;
			yStart = 6;
			spacing = 2;
			colors = new Color[] {GREEN, RED, RED};
			
			straightCirlces(size, 3, xStart, yStart, spacing, colors, g);
			straightCirlces(size, 3, xStart + size + 5, yStart, spacing, colors, g);
			break;
		case 7:
			size = 13;
			xStart = 13;
			yStart = 4;
			spacing = -(size / 2) - 1;
			colors = new Color[] {GREEN};
			
			drawDiagonalCircles(size, 3, xStart, yStart, 5, spacing, colors, g);
			
			xStart = 18;
			yStart = (HEIGHT - 10) / 2;
			spacing = 4;
			colors = new Color[] {RED};
			
			straightCirlces(size, 2, xStart, yStart, spacing, colors, g);
			straightCirlces(size, 2, xStart + size * 2, yStart, spacing, colors, g);
			break;
		case 8:
			size = 13;
			xStart = 18;
			yStart = 3;
			spacing = 2;
			colors = new Color[] {BLUE};
			
			straightCirlces(size, 4, xStart, yStart, spacing, colors, g);
			straightCirlces(size, 4, xStart + size * 2, yStart, spacing, colors, g);
			break;
		case 9:
			size = 17;
			xStart = 12;
			yStart = 5;
			spacing = 2;
			colors = new Color[] {GREEN, RED, BLUE};
			
			straightCirlces(size, 3, xStart, yStart, spacing, colors, g);
			straightCirlces(size, 3, xStart + size, yStart, spacing, colors, g);
			straightCirlces(size, 3, xStart + size * 2, yStart, spacing, colors, g);
		default:
			break;
		}
	}
	
	private void straightCirlces(int size, int numberOfRepeats, 
			int xStart, int yStart, int spacing, 
			Color[] colors, Graphics g) {
		if (colors.length != numberOfRepeats) {
			if (colors.length == 1) {
				Color color = colors[0];
				colors = new Color[numberOfRepeats];
				for (int i = 0; i < numberOfRepeats; i++) {
					colors[i] = color;
				}
			}
			else {
				System.err.println("Unable to draw circles, number of colors " +
					"does not match desired repeats!");
				return;
			}
		}
		for (int i = 0; i < numberOfRepeats; i++) {
			int yAdd = spacing * i + size * i;
			g.setColor(colors[i]);
			g.fillOval(xStart, yStart + yAdd, size, size);
			drawSymbol(xStart, yStart + yAdd, size, g);
		}
	}
	
	private void drawDiagonalCircles(int size, int numCircles,
			int xStart, int yStart, int xSpacing, int ySpacing,
			Color[] colors, Graphics g) {
		
		if (colors == null || colors.length != numCircles) {
			if (colors.length == 1) {
				Color color = colors[0];
				colors = new Color[numCircles];
				for (int i = 0; i < numCircles; i++) {
					colors[i] = color;
				}
			}
			else {
				System.err.println("Invalid number of colors, unable to draw circles");
				return;
			}
		}
		
		for (int i = 0; i < numCircles; i++) {
			int x = xStart + i * (size + xSpacing);
			int y = yStart + i * (size + ySpacing);
			g.setColor(colors[i]);
			g.fillOval(x, y, size, size);
			drawSymbol(x, y, size, g);
		}
	}
	
	private void drawSymbol(int xStart, int yStart, int size, Graphics g) {
		int[] xPoints = {xStart + 4, xStart + (size / 3 * 2) + 3, xStart + size / 2, 
			xStart + (size / 3) - 2, xStart + size - 3};
		int[] yPoints = {yStart + size / 3 + 1, yStart + size - 4, yStart + 2,
			yStart + size - 4, yStart + size / 3 + 1};
		
		g.setColor(WHITE);
		g.fillPolygon(xPoints, yPoints, 5);
	}
	
	@Override
    public String toString() {
        if (rank < 1 || rank > 9) {
            return "Invalid Circle Tile";
        }
        return "Circle " + rank;
    }
}
