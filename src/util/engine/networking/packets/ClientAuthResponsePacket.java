package util.engine.networking.packets;


public class ClientAuthResponsePacket
{
	public boolean canJoin;
	public int id;


	public ClientAuthResponsePacket() { }


	public ClientAuthResponsePacket(int id, boolean canJoin)
	{
		this.canJoin = canJoin;
		this.id = id;
	}
}
