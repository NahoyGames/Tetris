package tetris;


import tetris.packets.NetBoardPacket;
import util.ArrayUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NetBoard extends Board
{
	private int id;
	private int linesCleared, linesReceived;


	public NetBoard(String username, int id)
	{
		super(username);
		this.id = id;
	}


	public int id()
	{
		return id;
	}


	public void clearLine(int y)
	{
		this.grid().clearLine(y);
		this.linesCleared++;
	}


	public void addLine()
	{
		this.grid().addLine();
		this.linesReceived++;
	}


	public int getLinesCleared()
	{
		return linesCleared;
	}


	public int getLinesReceived()
	{
		return linesReceived;
	}


	public NetBoardPacket serializeToPacket()
	{
		NetBoardPacket packet = new NetBoardPacket();

		// ConnectionID
		packet.connectionID = this.id();

		// Lines Received
		packet.linesReceived = this.linesReceived;

		// Grid(blocks which are on/off) & Colors
		boolean[][] grid = new boolean[this.grid().height() - this.linesReceived][this.grid().width()];
		List<Integer> colors = new ArrayList<>();
		for (int y = 0; y < grid.length; y++)
		{
			for (int x = 0; x < grid[0].length; x++)
			{
				if (this.grid().hasBlockAt(x, y))
				{
					grid[y][x] = true;
					colors.add(this.grid().colorOfBlockAt(x, y).getRGB());
				}
			}
		}
		packet.grid = grid;

		// Colors
		packet.colors = ArrayUtil.toArray(colors);

		return packet;
	}


	public void deserializePacket(NetBoardPacket packet)
	{
		// ConnectionID
		if (packet.connectionID != this.id())
		{
			System.out.println("The ConnectionID doesn't match this board's, therefore deserialization was skipped.");
			return;
		}

		// Lines Received
		this.linesReceived = packet.linesReceived;
		for (int y = this.grid().height() - linesReceived; y < this.grid().height(); y++)
		{
			for (int x = 0; x < this.grid().width(); x++)
			{
				this.grid().setBlockAt(x, y, true, Grid.RECEIVED_LINE_COLOR);
			}
		}

		// Grid & Color
		int colorIndex = 0;
		for (int y = 0; y < packet.grid.length; y++)
		{
			for (int x = 0; x < this.grid().width(); x++)
			{
				if (packet.grid[y][x])
				{
					this.grid().setBlockAt(x, y, true, new Color(packet.colors[colorIndex++]));
				}
				else
				{
					this.grid().setBlockAt(x, y, false, null);
				}
			}
		}
	}
}
