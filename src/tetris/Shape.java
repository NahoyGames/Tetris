package tetris;

import tetris.shapes.*;
import util.color.ColorUtil;
import util.math.Mathf;
import util.math.Vec2;
import util.math.Vec2f;

import java.awt.*;


public class Shape
{
	private Vec2[] points;
	private Vec2 position;
	private int rotation;
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


	public void draw(Graphics2D buffer, int blockSize, float lerp, Vec2 offset)
	{
		// Draw Shape
		buffer.setColor(ColorUtil.lerp(color, Color.WHITE, lerp));
		for (Vec2 p : points)
		{
			buffer.fillRect((p.x + position.x) * blockSize + offset.x, (p.y + position.y) * blockSize + offset.y, blockSize, blockSize);
		}

		// Draw Prediction
		buffer.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
		Vec2 farthestDown = this.getPosition().clone();
		while (canMove(farthestDown))
		{
			farthestDown = farthestDown.add(Vec2.up());
		}
		for (Vec2 p : points)
		{
			buffer.fillRect((p.x + farthestDown.x) * blockSize + offset.x, (p.y + (farthestDown.y - 1)) * blockSize + offset.y, blockSize, blockSize);
		}
	}


	public void draw(Graphics2D buffer, int blockSize, float lerp)
	{
		this.draw(buffer, blockSize, lerp, Vec2.zero());
	}


	public void draw(Graphics2D buffer, float lerp)
	{
		this.draw(buffer, this.grid.blockSize(), lerp);
	}


	public void draw(Graphics2D buffer)
	{
		this.draw(buffer, 1f);
	}


	private Vec2f centerOffset;
	public void drawCentered(Graphics2D buffer, int blockSize, float lerp, Vec2 position)
	{
		if (centerOffset == null)
		{
			Vec2 min = new Vec2(Integer.MAX_VALUE, Integer.MAX_VALUE);
			Vec2 max = new Vec2(Integer.MIN_VALUE, Integer.MIN_VALUE);

			for (Vec2 v : points)
			{
				min = min.min(v);
				max = max.max(v);
			}

			centerOffset = new Vec2f(min.x + ((max.x - min.x) / 2f), min.y + ((max.y - min.y) / 2f));
		}

		buffer.setColor(ColorUtil.lerp(color, Color.WHITE, lerp));
		for (Vec2 p : points)
		{
			buffer.fillRect((int)((p.x - centerOffset.x) * blockSize) + position.x , (int)((p.y - centerOffset.y) * blockSize) + position.y, blockSize, blockSize);
		}
	}


	public boolean move(Vec2 dir)
	{
		if (canMove(this.position.add(dir)))
		{
			position = position.add(dir);
			return true;
		}

		return false;
	}


	private boolean canMove(Vec2 position, Vec2[] points)
	{
		for (Vec2 p : points)
		{
			Vec2 inGrid = p.add(position);

			if (inGrid.y < 0)
			{
				continue;
			}

			if (inGrid.x >= grid.width() || inGrid.x < 0 || inGrid.y >= grid.height() || grid.hasBlockAt(inGrid.x, inGrid.y))
			{
				return false;
			}
		}

		return true;
	}


	public boolean canMove(Vec2 position)
	{
		return this.canMove(position, this.points);
	}


	public boolean rotate(boolean clockwise)
	{
		return rotate(clockwise, 1, false);
	}


	public boolean rotate(boolean clockwise, int amount, boolean overrideLogic)
	{
		amount = Math.abs(amount);

		Vec2[] rotatedPoints = getPointsRotated(clockwise, amount);


		for (Vec2 p : rotatedPoints)
		{
			Vec2 inGrid;
			inGrid = p.add(position);

			if ((inGrid.x >= grid.width() || inGrid.x < 0 || inGrid.y >= grid.height() || grid.hasBlockAt(inGrid.x, inGrid.y)) && !overrideLogic)
			{
				return false;
			}
		}

		points = rotatedPoints;
		rotation = (rotation + (clockwise ? amount : -amount)) % 4;
		return true;
	}


	private Vec2[] getPointsRotated(boolean clockwise, int amount)
	{
		amount = Math.abs(amount);

		Vec2[] newPoints = points.clone();

		for (int j = 0; j < amount; j++)
		{
			int i = 0;
			for (Vec2 p : newPoints)
			{
				Vec2 rotated;
				if (clockwise)
				{
					rotated = new Vec2(-p.y, p.x);
				} else
				{
					rotated = new Vec2(p.y, -p.x);
				}
				newPoints[i++] = rotated;
			}
		}

		return newPoints;
	}


	public void lock()
	{
		for (Vec2 p : points)
		{
			Vec2 inGrid = p.add(position);

			if (!grid.hasBlockAt(inGrid.x, inGrid.y))
			{
				grid.setBlockAt(inGrid.x, inGrid.y, true, color);
			}
		}
	}


	public boolean setPosition(Vec2 position, boolean overrideLogic)
	{
		if (overrideLogic || canMove(position))
		{
			this.position = position.clone();
			return true;
		}

		return false;
	}


	public boolean setRotationThenPosition(boolean clockwise, int rotationAmount, Vec2 position, boolean overrideLogic)
	{
		Vec2[] pointsRotated = getPointsRotated(clockwise, rotationAmount);
		if (overrideLogic || canMove(position, pointsRotated))
		{
			this.position = position.clone();
			this.points = pointsRotated;
			this.rotation = (rotation + (clockwise ? rotationAmount : -rotationAmount)) % 4;
			return true;
		}

		return false;
	}


	public Vec2 getPosition()
	{
		return position;
	}


	public int getRotation() { return rotation; }
}
