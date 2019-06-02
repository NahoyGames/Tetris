package util.engine.networking;

import com.esotericsoftware.kryonet.Connection;

public interface INetworkListener
{
	void onReceivePacket(Connection sender, Object packet);

	void onPlayerJoin(int senderID, String username, boolean successful); // SERVER-side: When a client connects \\\ CLIENT-side: When this client connects
}
