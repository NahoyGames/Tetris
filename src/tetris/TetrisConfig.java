package tetris;


import tetris.packets.*;
import util.engine.Engine;
import util.engine.EngineConfig;

import java.awt.*;


public class TetrisConfig extends EngineConfig
{
	public float SHAPE_LOCK_TIME = 1f;
	public int SHAPE_QUEUE_SIZE = 5;
	public Font TETRIS_FONT;


	public TetrisConfig(boolean isServer)
	{
		this(isServer, "TetrisLad");
	}


	public TetrisConfig(String serverIP, String username)
	{
		this(false, username);

		SERVER_IP = serverIP;
	}


	public TetrisConfig(boolean isServer, String username)
	{
		super("Tetris " + (isServer ? "Server" : "Client"), isServer ? 10 : 30);

		BACKGROUND_COLOR = new Color(0x444E5C);
		ANTI_ALIASING = TEXT_ANTI_ALIASING = false;
		IS_SERVER_BUILD = isServer;

		USERNAME = username;

		try
		{
			TETRIS_FONT = Font.createFont(Font.TRUETYPE_FONT, Engine.getResource("tetris/resources/pixel.ttf")).deriveFont(25f);
		}
		catch (Exception e)
		{
			System.out.println("Tetris font was not found!");
			e.printStackTrace();
		}

		/** REGISTER PACKETS HERE **/
		REGISTERED_PACKETS.add(InputPacket.class);
		REGISTERED_PACKETS.add(int[].class);
		REGISTERED_PACKETS.add(String[].class);
		REGISTERED_PACKETS.add(GameStatePacket.class);
		REGISTERED_PACKETS.add(SetShapePositionPacket.class);
		REGISTERED_PACKETS.add(RotateShapePacket.class);
		REGISTERED_PACKETS.add(LockCurrentShapePacket.class);
		REGISTERED_PACKETS.add(ClearLinePacket.class);
		REGISTERED_PACKETS.add(QueueShapePacket.class);
		REGISTERED_PACKETS.add(NextShapePacket.class);
		REGISTERED_PACKETS.add(PlayerWonPacket.class);
	}
}
