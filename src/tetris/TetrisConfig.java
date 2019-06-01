package tetris;


import tetris.packets.*;
import util.engine.EngineConfig;

import java.awt.*;


public class TetrisConfig extends EngineConfig
{
	public float SHAPE_LOCK_TIME = 1f;


	public TetrisConfig(boolean isServer)
	{
		super("Tetris " + (isServer ? "Server" : "Client"), isServer ? 10 : 30);

		BACKGROUND_COLOR = new Color(0x444E5C);
		IS_SERVER_BUILD = isServer;

		REGISTERED_PACKETS.add(InputPacket.class);
		REGISTERED_PACKETS.add(int[].class);
		REGISTERED_PACKETS.add(String[].class);
		REGISTERED_PACKETS.add(GameStatePacket.class);
		REGISTERED_PACKETS.add(SetShapePositionPacket.class);
		REGISTERED_PACKETS.add(SetCurrentShapePacket.class);
		REGISTERED_PACKETS.add(RotateShapePacket.class);
		REGISTERED_PACKETS.add(LockCurrentShapePacket.class);
		REGISTERED_PACKETS.add(ClearLinePacket.class);
	}
}
