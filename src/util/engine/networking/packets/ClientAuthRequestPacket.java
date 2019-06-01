package util.engine.networking.packets;


import util.engine.Engine;

public class ClientAuthRequestPacket
{
	public String version;
	public String username;


	public ClientAuthRequestPacket() { }


	public ClientAuthRequestPacket(String username)
	{
		this.version = Engine.config().VERSION;
		this.username = username;
	}
}
