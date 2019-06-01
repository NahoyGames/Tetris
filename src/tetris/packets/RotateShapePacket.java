package tetris.packets;

public class RotateShapePacket
{
	public int connectionID;


	public RotateShapePacket() { }

	public RotateShapePacket(int connectionID)
	{
		this.connectionID = connectionID;
	}
}
