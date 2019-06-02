package util.engine.networking.server;


import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import util.ArrayUtil;
import util.engine.Engine;
import util.engine.networking.GenericNetManager;
import util.engine.networking.INetworkListener;
import util.engine.networking.packets.ClientAuthRequestPacket;
import util.engine.networking.packets.ClientAuthResponsePacket;
import util.engine.networking.packets.PlayerSuccessfullyJoinedPacket;

import java.io.IOException;

public class ServerNetManager extends GenericNetManager
{
	public ServerNetManager()
	{
		// Generic
		super(new Server(), Engine.config().REGISTERED_PACKETS);
	}


	@Override
	public void init()
	{
		// Init Transport
		try
		{
			transport.start();
			((Server)transport).bind(Engine.config().TCP_PORT, Engine.config().UDP_PORT);
			System.out.println("Server connected on ports TCP::" + Engine.config().TCP_PORT + " and UDP::" + Engine.config().UDP_PORT);
		}
		catch (IOException e)
		{
			System.out.println("An error occured while attempting to start the server... " + e);
		}
	}


	public void sendReliable(Connection connection, Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		connection.sendTCP(packet);
	}


	public void sendReliable(int connection, Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		((Server)transport).sendToTCP(connection, packet);
	}


	public void sendReliable(Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		((Server)transport).sendToAllTCP(packet);
	}


	public void sendUnreliable(Connection connection, Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		connection.sendUDP(packet);
	}


	public void sendUnreliable(int connection, Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		((Server)transport).sendToUDP(connection, packet);
	}


	public void sendUnreliable(Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		((Server)transport).sendToAllUDP(packet);
	}


	public Connection[] getConnections()
	{
		return ((Server)transport).getConnections();
	}


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{
		super.onReceivePacket(sender, packet);

		System.out.println("Received packet typeof " + packet.getClass().getName());

		if (packet instanceof ClientAuthRequestPacket)
		{
			ClientAuthRequestPacket requestPacket = (ClientAuthRequestPacket)packet;

			// Check for unsupported version
			boolean successfulJoin = ArrayUtil.contains(Engine.config().SUPPORTED_VERSIONS, requestPacket.version);

			// Debug
			System.out.println("Player " + requestPacket.username + "#" + sender.getID() + " joined the server " + (successfulJoin ? "successfully." : "unsuccessfully."));

			// Notify player who just joined
			sendReliable(sender, new ClientAuthResponsePacket(sender.getID(), successfulJoin)); // Auth

			// Notify listeners
			for (INetworkListener listener : listeners)
			{
				listener.onPlayerJoin(sender.getID(), requestPacket.username, successfulJoin);
			}

			// Notify everyone
			if (successfulJoin)
			{
				sendReliable(new PlayerSuccessfullyJoinedPacket(sender.getID(), requestPacket.username));
			}
		}
	}
}
