package tetris.packets;

public class ClearLinePacket
{
	public int connectionID;
	public int lineIndex;


	public ClearLinePacket() { }

	public ClearLinePacket(int connectionID, int lineIndex)
	{
		this.connectionID = connectionID;
		this.lineIndex = lineIndex;
	}
}
