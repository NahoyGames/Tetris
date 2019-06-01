package tetris.shapes;

import tetris.Grid;
import tetris.Shape;
import util.math.Vec2;

import java.awt.*;

public class MirrorLShape extends Shape
{
	public MirrorLShape(Grid grid, Color color)
	{
		super(new Vec2[] {new Vec2(0, 0), new Vec2(0, 1), new Vec2(0, -1), new Vec2(1, -1)}, grid, color);
	}
}
