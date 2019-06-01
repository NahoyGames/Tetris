package tetris.packets;

public class LockCurrentShapePacket
{
	public int connectionID;


	public LockCurrentShapePacket() { }


	public LockCurrentShapePacket(int connectionID)
	{
		this.connectionID = connectionID;
	}
}
