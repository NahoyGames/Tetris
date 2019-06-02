package util.engine.networking;

import com.esotericsoftware.kryonet.Connection;
import util.engine.Engine;
import util.engine.EngineEventAdapter;

public class NetworkAdapter extends EngineEventAdapter implements INetworkListener
{
	public NetworkAdapter()
	{
		Engine.network().addListener(this);
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
