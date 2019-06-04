package tetris;

import com.esotericsoftware.kryonet.Connection;
import launcher.Launcher;
import tetris.packets.*;
import util.ArrayUtil;
import util.color.ColorUtil;
import util.engine.Engine;
import util.engine.networking.NetworkAdapter;
import util.engine.networking.server.ServerNetManager;
import util.math.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;


public class TetrisServer extends NetworkAdapter
{
	public static void main(String[] args)
	{
		TetrisConfig config = new TetrisConfig(true);
		String[][] launcherData;

		try
		{
			launcherData = Launcher.getLauncherMetaData();
			config.TCP_PORT = Integer.parseInt(launcherData[1][1]);
			config.UDP_PORT = Integer.parseInt(launcherData[2][1]);
			config.PLAYERS_TO_START = Integer.parseInt(launcherData[5][1]);
		}
		catch (Exception e)
		{
			System.err.println("Could not fetch launcher metadata. Using default values instead.");
		}

		Engine.init(config);
		Engine.initNetwork();
		new TetrisServer();
	}


	private HashMap<Integer, NetBoard> boards;

	private boolean gameStarted = false;


	public TetrisServer()
	{
		boards = new HashMap<>();
	}


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{
		super.onReceivePacket(sender, packet);

		ServerNetManager netManager = ((ServerNetManager)Engine.network());

		if (packet instanceof LockCurrentShapePacket)
		{
			LockCurrentShapePacket lockPacket = (LockCurrentShapePacket)packet;
			NetBoard board = boards.get(lockPacket.connectionID);
			Shape shape = board.getCurrentShape();

			Vec2 upOffset = Vec2.zero();
			int rotationAmount = shape.getRotation() - lockPacket.rotation;
			while (!shape.setRotationThenPosition(true, rotationAmount, lockPacket.position().add(upOffset), false))
			{
				upOffset = upOffset.add(Vec2.up().scale(-1));
			}
			//shape.setPosition(lockPacket.position().add(upOffset), false);
			//shape.rotate(true, shape.getRotation() - lockPacket.rotation, true);
			shape.lock();

			// Forward the message
			//netManager.sendReliableExcept(sender.getID(), packet);
			netManager.sendReliable(board.serializeToPacket());

			// Queue in another shape to replace
			// This is done every time, rather dumbly, but it doesn't hurt the program so why bother
			board.setNextShape();
			this.queueShapeForEveryone();
		}
	}


	private void queueShapeForEveryone()
	{
		int randomID = Shape.getRandomShapeID();
		Color color = ColorUtil.random(0.45f, 0.55f, 0.93f, 0.97f);

		// request more blocks
		for (NetBoard board : boards.values())
		{
			board.queueShape(Shape.getShape(randomID, board.grid(), color));
		}
		((ServerNetManager)Engine.network()).sendReliable(new QueueShapePacket(randomID, color));
	}


	@Override
	public void onPlayerJoin(int senderID, String username, boolean successful)
	{
		super.onPlayerJoin(senderID, username, successful);

		if (successful)
		{
			boards.put(senderID, new NetBoard(username, senderID));

			int[] connections = ArrayUtil.toArray(boards.keySet());
			String[] usernames = new String[boards.values().size()];

			int i = 0;
			for (Board b : boards.values()) { usernames[i++] = b.getUsername(); }

			((ServerNetManager)Engine.network()).sendReliable(senderID, new GameStatePacket(connections, usernames));
		}
	}


	@Override
	public void onPlayerDisconnect(int senderID)
	{
		super.onPlayerDisconnect(senderID);

		boards.remove(senderID);
	}

	@Override
	public void onGraphicCull(Graphics2D buffer)
	{
		super.onGraphicCull(buffer);

		AffineTransform transform = buffer.getTransform();
		for (NetBoard board : boards.values())
		{
			board.grid().draw(buffer);
			if (board.getCurrentShape() != null)
			{
				board.getCurrentShape().draw(buffer);
			}


			buffer.translate(board.grid().width() * board.grid().blockSize() + 40, 0);
		}
		buffer.setTransform(transform);
	}


	@Override
	public void onUpdate()
	{
		super.onUpdate();

		/** START GAME **/
		if (!gameStarted)
		{
			if (boards.values().size() >= ((TetrisConfig)Engine.config()).PLAYERS_TO_START)
			{
				gameStarted = true;

				for (int i = 0; i < ((TetrisConfig)Engine.config()).SHAPE_QUEUE_SIZE + 1; i++)
				{
					queueShapeForEveryone();
				}

				for (NetBoard board : boards.values())
				{
					board.setNextShape();
				}
			}
			else
			{
				return;
			}
		}

		/** CLEAR LINES **/
		for (NetBoard board : boards.values())
		{
			for (int y = 0; y < board.grid().height() - board.getLinesReceived(); y++)
			{
				if (board.grid().hasFullLine(y))
				{
					board.clearLine(y);

					// Add a line to everyone else
					for (NetBoard b : boards.values())
					{
						if (b != board && !b.hasLost())
						{
							b.addLine();
						}
					}
				}
			}
		}

		/** FIND WINNER, IF ANY **/
		int playersLeft = boards.values().size();
		NetBoard winner = null;

		for (NetBoard board : boards.values())
		{
			if (board.hasLost())
			{
				playersLeft--;
				continue;
			}
			else
			{
				winner = board;
			}
		}

		/** ANNOUNCE WINNER **/
		if (playersLeft == 1)
		{
			((ServerNetManager)Engine.network()).sendReliable(new PlayerWonPacket(winner.id()));
			Engine.quit();
		}
	}
}
