package tetris;


import util.ArrayUtil;

import java.awt.*;

public class Grid
{
	private class Block
	{
		private boolean on = false;
		private Color color = new Color(0x9D524A);
	}


	private Block[][] grid;
	private int width, height;


	public Grid(int width, int height)
	{
		this.grid = new Block[this.height = height][this.width = width];

		for (int x = 0; x < grid[0].length; x++)
		{
			for (int y = 0; y < grid.length; y++)
			{
				grid[y][x] = new Block();
			}
		}
	}
	public Grid() { this(10, 24); }


	public boolean hasBlockAt(int x, int y)
	{
		return grid[y][x].on;
	}


	public Color colorOfBlockAt(int x, int y) { return grid[y][x].color; }


	public void setBlockAt(int x, int y, boolean on, Color color)
	{
		grid[y][x].on = on;
		grid[y][x].color = color;
	}


	public void draw(Graphics2D buffer, int blockSize)
	{
		buffer.setColor(new Color(0x626D75));
		buffer.fillRect(0, 0, this.width * blockSize, this.height() * blockSize);

		for (int x = 0; x < this.width(); x++)
		{
			for (int y = 0; y < this.height(); y++)
			{
				if (this.hasBlockAt(x, y))
				{
					buffer.setColor(this.grid[y][x].color);
					buffer.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
				}

				//buffer.setColor(new Color(105, 116, 136));
				//buffer.drawRect(x * blockSize, y * blockSize, blockSize, blockSize);
			}
		}
	}


	public void clearLine(int index)
	{
		for (int clear = index; clear > 0; clear--)
		{
			for (int x = 0; x < this.width(); x++)
			{
				this.setBlockAt(x, clear, this.hasBlockAt(x, clear - 1), this.colorOfBlockAt(x, clear - 1));
			}
		}
	}


	public void addLine()
	{
		for (int y = 0; y < this.height() - 1; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setBlockAt(x, y, this.hasBlockAt(x, y + 1), this.colorOfBlockAt(x, y + 1));
			}
		}

		for (int x = 0; x < this.width; x++)
		{
			this.setBlockAt(x, this.height() - 1, true, Color.DARK_GRAY);
		}
	}


	public int width() { return width; }
	public int height() { return height; }
}
