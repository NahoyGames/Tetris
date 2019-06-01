package util.engine.networking.client;


import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import tetris.packets.GameStatePacket;
import util.engine.Engine;
import util.engine.networking.GenericNetManager;
import util.engine.networking.INetworkListener;
import util.engine.networking.packets.ClientAuthRequestPacket;
import util.engine.networking.packets.ClientAuthResponsePacket;

import java.io.IOException;
import java.util.Collection;


public class ClientNetManager extends GenericNetManager
{
	private int id = -1;

	public ClientNetManager()
	{
		// Generic
		super(new Client(), Engine.config().REGISTERED_PACKETS);
	}


	@Override
	public void init()
	{
		// Init Transport
		try
		{
			transport.start();
			((Client)transport).connect(5000, Engine.config().SERVER_IP, Engine.config().TCP_PORT, Engine.config().UDP_PORT);
		}
		catch (IOException e)
		{
			System.out.println("An error occured while attempting to connect the client... " + e);
		}

		// Send Auth packet
		sendReliable(new ClientAuthRequestPacket("myUsername"));
	}

	public void sendReliable(Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		((Client)transport).sendTCP(packet);
	}


	public void sendUnreliable(Object packet)
	{
		if (this.transport == null)
		{
			System.out.println("Cannot send a message because the transport is missing or is not connected!");
			return;
		}

		((Client)transport).sendUDP(packet);
	}


	public int id() { return id; }


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{
		super.onReceivePacket(sender, packet);

		if (packet instanceof ClientAuthResponsePacket)
		{
			ClientAuthResponsePacket responsePacket = (ClientAuthResponsePacket)packet;

			System.out.println("Received auth packet...");

			this.id = responsePacket.id;

			for (INetworkListener listener : super.listeners)
			{
				listener.onPlayerJoin(responsePacket.id, responsePacket.canJoin);
			}

			if (!responsePacket.canJoin)
			{
				System.err.println("Could not join because the client is on an outdated version!");
				System.exit(0);
			}
		}
	}
}
