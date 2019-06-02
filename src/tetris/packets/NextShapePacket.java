package tetris.packets;

public class NextShapePacket
{
	public int connectionID;


	public NextShapePacket() { }


	public NextShapePacket(int connectionID)
	{
		this.connectionID = connectionID;
	}
}
