package tetris.packets;

import java.awt.*;

public class SetCurrentShapePacket
{
	public int connectionID;
	public int shapeID;
	public int r, g, b;


	public SetCurrentShapePacket() { }


	public SetCurrentShapePacket(int connectionID, int shapeID, Color color)
	{
		this.connectionID = connectionID;
		this.shapeID = shapeID;
		this.r = color.getRed();
		this.g = color.getGreen();
		this.b = color.getBlue();
	}


	public Color color()
	{
		return new Color(r, g, b);
	}
}
