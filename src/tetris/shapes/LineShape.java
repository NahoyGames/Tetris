package tetris.shapes;

import tetris.Grid;
import tetris.Shape;
import util.math.Vec2;

import java.awt.*;

public class LineShape extends Shape
{
	public LineShape(Grid grid, Color color)
	{
		super(new Vec2[]{new Vec2(0, 0), new Vec2(0,-1), new Vec2(0,-2), new Vec2(0,1)}, grid, color);
	}
}
