package util.engine.networking;


import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class GenericNetManager implements INetworkListener
{
	protected EndPoint transport;

	protected List<INetworkListener> listeners;


	protected GenericNetManager(EndPoint transportType, Collection<Class> registeredPackets)
	{
		// Transport
		this.transport = transportType;

		// Packets
		for (Class c : registeredPackets)
		{
			this.transport.getKryo().register(c);
		}

		// Listeners
		listeners = new ArrayList<>();

		transport.addListener(new Listener()
		{
			@Override
			public void received(Connection connection, Object o)
			{
				// Best alternative to c# delegates which I could come up with
				// It uses toArray to avoid concurrent modification exceptions
				for (INetworkListener listener : listeners.toArray(new INetworkListener[listeners.size()]))
				{
					try
					{
						listener.onReceivePacket(connection, o);
					}
					catch (Exception e) { continue; }
				}
			}
		});

		// Listen for packets
		addListener(this);
	}


	public abstract void init();


	public void addListener(INetworkListener listener)
	{
		this.listeners.add(listener);
	}

	public void removeListener(INetworkListener listener)
	{
		this.listeners.remove(listener);
	}


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{

	}


	@Override
	public void onPlayerJoin(int senderID, String username, boolean successful)
	{

	}
}
