package util.engine;

import util.engine.networking.packets.ClientAuthRequestPacket;
import util.engine.networking.packets.ClientAuthResponsePacket;
import util.engine.networking.packets.PlayerDisconnectPacket;
import util.engine.networking.packets.PlayerSuccessfullyJoinedPacket;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class EngineConfig
{
	/** Window **/
	public String WINDOW_NAME = "My Engine";
	public int FRAMES_PER_SECOND = 60;
	public int WINDOW_WIDTH = 600;
	public int WINDOW_HEIGHT = 400;
	public Color BACKGROUND_COLOR = Color.GRAY;
	public boolean HEADLESS_MODE = false; // if (true) --> no graphics, no input

	/** GRAPHICS **/
	public boolean ANTI_ALIASING = false;
	public boolean TEXT_ANTI_ALIASING = true;

	/** Engine **/
	public String[] SUPPORTED_VERSIONS = {"0.0.1"};
	public String VERSION = "0.0.1";

	/** Network **/
	public boolean IS_SERVER_BUILD = true;
	public int TCP_PORT = 54555;
	public int UDP_PORT = 54777;
	public String SERVER_IP = "localhost";
	public Collection<Class> REGISTERED_PACKETS;
	public String USERNAME = "Player";


	public EngineConfig() { }


	public EngineConfig(String name, int fps)
	{
		WINDOW_NAME = name;
		FRAMES_PER_SECOND = fps;

		REGISTERED_PACKETS = new ArrayList<>();

		/** Build-in Packets **/
		REGISTERED_PACKETS.add(ClientAuthResponsePacket.class);
		REGISTERED_PACKETS.add(ClientAuthRequestPacket.class);
		REGISTERED_PACKETS.add(PlayerSuccessfullyJoinedPacket.class);
		REGISTERED_PACKETS.add(PlayerDisconnectPacket.class);
	}
}
