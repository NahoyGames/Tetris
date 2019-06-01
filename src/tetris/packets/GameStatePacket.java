package tetris.packets;


public class GameStatePacket
{
	public int[] connections;
	public String[] usernames;


	public GameStatePacket() { }


	public GameStatePacket(int[] connections, String[] usernames)
	{
		this.connections = connections;
		this.usernames = usernames;
	}
}
