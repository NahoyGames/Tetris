package tetris.packets;

import java.awt.*;

public class QueueShapePacket
{
	public int shapeID;
	public byte r, g, b;


	public QueueShapePacket() { }


	public QueueShapePacket(int shapeID, Color color)
	{
		this.shapeID = shapeID;
		this.r = (byte)color.getRed();
		this.g = (byte)color.getGreen();
		this.b = (byte)color.getBlue();
		System.out.println(color.getRed());
	}


	public Color color()
	{
		return new Color((r & 0xFF), (g & 0xFF), (b & 0xFF), 255);
	}
}
