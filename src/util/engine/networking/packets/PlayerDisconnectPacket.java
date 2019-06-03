package util.engine.networking.packets;

public class PlayerDisconnectPacket
{
	public int connectionID;


	public PlayerDisconnectPacket() { }


	public PlayerDisconnectPacket(int connectionID)
	{
		this.connectionID = connectionID;
	}
}
