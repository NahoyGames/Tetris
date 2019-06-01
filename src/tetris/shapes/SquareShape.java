package tetris.shapes;

import tetris.Grid;
import tetris.Shape;
import util.math.Vec2;

import java.awt.*;

public class SquareShape extends Shape
{
	public SquareShape(Grid grid, Color color)
	{
		super(new Vec2[] {new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 0), new Vec2(1, 1)}, grid, color);
	}


	@Override
	public boolean rotate(boolean clockwise)
	{
		return false;
	}
}
