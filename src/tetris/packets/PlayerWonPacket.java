package tetris.packets;

public class PlayerWonPacket
{
	public int connectionID;


	public PlayerWonPacket() { }


	public PlayerWonPacket(int connectionID)
	{
		this.connectionID = connectionID;
	}
}
