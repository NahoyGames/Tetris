package tetris;

import tetris.shapes.*;
import util.color.ColorUtil;
import util.math.Mathf;
import util.math.Vec2;

import java.awt.*;


public class Shape
{
	private Vec2[] points;
	private Vec2 position;
	private Color color;

	private Grid grid;


	protected Shape(Vec2[] points, Grid grid, Color color)
	{
		this.points = points;
		this.grid = grid;
		this.position = new Vec2(grid.width() / 2, 0);
		this.color = color;
	}


	public static Shape getRandomShape(Grid grid)
	{
		return getShape(getRandomShapeID(), grid, ColorUtil.random());
	}


	public static int getRandomShapeID()
	{
		return (int)(Math.random() * 7);
	}


	public static Shape getShape(int id, Grid grid, Color color)
	{
		switch (id)
		{
			case 0: return new TShape(grid, color);
			case 1: return new LShape(grid, color);
			case 2: return new MirrorLShape(grid, color);
			case 3: return new SShape(grid, color);
			case 4: return new ZShape(grid, color);
			case 5: return new LineShape(grid, color);
			case 6: return new SquareShape(grid, color);
			default: return new TShape(grid, color);
		}
	}


	public static int getIdOf(Shape shape)
	{
		if (shape instanceof TShape) { return 0; }
		else if (shape instanceof LShape) { return 1; }
		else if (shape instanceof MirrorLShape) { return 2; }
		else if (shape instanceof SShape) { return 3; }
		else if (shape instanceof ZShape) { return 4; }
		else if (shape instanceof LineShape) { return 5; }
		else if (shape instanceof SquareShape) { return 6; }
		else { return -1; }
	}


	public Color getColor() { return this.color; }


	public void draw(Graphics2D buffer, int blockSize, float lerp)
	{
		buffer.setColor(ColorUtil.lerp(color, new Color(0xD4B22A), lerp));
		for (Vec2 p : points)
		{
			buffer.fillRect((p.x + position.x) * blockSize, (p.y + position.y) * blockSize, blockSize, blockSize);
		}
	}


	public boolean move(Vec2 dir)
	{
		for (Vec2 p : points)
		{
			Vec2 inGrid = p.add(position).add(dir);

			if (inGrid.y < 0)
			{
				continue;
			}

			if (inGrid.x >= grid.width() || inGrid.x < 0 || inGrid.y >= grid.height() || grid.hasBlockAt(inGrid.x, inGrid.y))
			{
				return false;
			}
		}

		position = position.add(dir);
		return true;
	}


	public boolean rotate(boolean clockwise)
	{
		Vec2[] newPoints = new Vec2[points.length];

		int i = 0;
		for (Vec2 p : points)
		{
			Vec2 rotated;
			Vec2 inGrid;
			if (clockwise)
			{
				rotated = new Vec2(-p.y, p.x);
			}
			else
			{
				rotated = new Vec2(p.y, -p.x);
			}
			inGrid = rotated.add(position);

			if (inGrid.x >= grid.width() || inGrid.x < 0 || inGrid.y >= grid.height() || grid.hasBlockAt(inGrid.x, inGrid.y))
			{
				return false;
			}

			newPoints[i++] = rotated;
		}

		points = newPoints;
		return true;
	}


	public void lock()
	{
		for (Vec2 p : points)
		{
			Vec2 inGrid = p.add(position);

			grid.setBlockAt(inGrid.x, inGrid.y,true, color);
		}
	}


	public boolean setPosition(Vec2 position, boolean overrideLogic)
	{
		if (overrideLogic)
		{
			this.position = position.clone();
			return true;
		}

		Vec2 dir = position.subtract(this.position);

		while (!position.equals(this.position))
		{
			if (!move(dir))
			{
				return false;
			}
		}

		return true;
	}


	public Vec2 getPosition()
	{
		return position;
	}
}
