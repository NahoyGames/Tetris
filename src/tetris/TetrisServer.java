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
		Engine.init(new TetrisConfig(true));
		Engine.initNetwork();
		new TetrisServer();
	}


	private HashMap<Integer, NetBoard> boards;

	private int playersToStart = 2;
	private boolean gameStarted = false;


	public TetrisServer()
	{
		boards = new HashMap<>();

		try
		{
			playersToStart = Integer.parseInt(Launcher.getLauncherMetaData()[3]);
		}
		catch (Exception e)
		{
			System.err.println("Could not determine the required number of players to start... Using the default \"" + playersToStart + "\"");
		}
	}


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{
		super.onReceivePacket(sender, packet);

		ServerNetManager netManager = ((ServerNetManager)Engine.network());

		if (packet instanceof LockCurrentShapePacket)
		{
			LockCurrentShapePacket lockPacket = (LockCurrentShapePacket)packet;
			Shape shape = boards.get(lockPacket.connectionID).getCurrentShape();

			shape.setPosition(lockPacket.position(), true);
			shape.rotate(true, shape.getRotation() - lockPacket.rotation, true);
			shape.lock();

			// Forward the message
			netManager.sendReliableExcept(sender.getID(), packet);

			// Queue in another shape to replace
			// This is done every time, rather dumbly, but it doesn't hurt the program so why bother
			boards.get(lockPacket.connectionID).setNextShape();
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
			if (boards.values().size() >= playersToStart)
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
