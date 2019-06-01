package util.engine.networking.packets;

public class PlayerSuccessfullyJoinedPacket
{
	public int id;
	public String username;


	public PlayerSuccessfullyJoinedPacket() { }


	public PlayerSuccessfullyJoinedPacket(int id, String username)
	{
		this.id = id;
		this.username = username;
	}
}
