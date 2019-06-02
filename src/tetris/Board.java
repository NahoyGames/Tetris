package tetris;


import util.engine.Engine;
import util.math.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Queue;

public class Board
{
	private Grid grid;
	private Shape currentShape;
	private Queue<Shape> shapeQueue;
	private String username;


	public Board()
	{
		this("Player");
	}


	public Board(String username)
	{
		this.grid = new Grid();
		this.shapeQueue = new LinkedList<>();
		this.username = username;
	}


	public Grid grid() { return grid; }


	public Shape getCurrentShape() { return currentShape; }


	public void queueShape(Shape shape) { shapeQueue.add(shape); }


	public Shape setNextShape()
	{
		return (this.currentShape = shapeQueue.remove());
	}


	public Shape peekNextShape() { return this.shapeQueue.peek(); }


	public int getQueueSize() { return shapeQueue.size(); }


	public void drawQueue(Graphics2D buffer, int width, Vec2 offset)
	{
		int blockSize = width / 5;

		AffineTransform transform = buffer.getTransform();

		try
		{
			int i = 0;
			for (Shape shape : shapeQueue)
			{
				if (++i > ((TetrisConfig) Engine.config()).SHAPE_QUEUE_SIZE)
				{
					break;
				}

				buffer.setColor(new Color(0x354155));
				buffer.fillRect(offset.x, offset.y, width, width);
				shape.drawCentered(buffer, blockSize, 1, new Vec2(blockSize * 2, blockSize * 2).add(offset));

				buffer.translate(0, width + 20);
			}
		}
		catch (ConcurrentModificationException e) { }

		buffer.setTransform(transform);
	}


	public String getUsername() { return username; }


	public boolean hasLost()
	{
		boolean hasLost = grid.hasLost();

		if (hasLost)
		{
			currentShape = null;
		}

		return hasLost;
	}
}
