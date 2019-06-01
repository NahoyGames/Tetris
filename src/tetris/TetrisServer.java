package tetris;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import tetris.packets.*;
import util.ArrayUtil;
import util.color.ColorUtil;
import util.engine.Engine;
import util.engine.Time;
import util.engine.networking.NetworkAdapter;
import util.engine.networking.server.ServerNetManager;
import util.math.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class TetrisServer extends NetworkAdapter
{
	public static void main(String[] args)
	{
		Engine.init(new TetrisConfig(true));
		Engine.initNetwork();
		new TetrisServer();
	}


	private class ClientBoard extends Board
	{
		private int id;

		private Vec2 dir = Vec2.zero();
		private boolean needsRotation = false;
		private Queue<Shape> shapeQueue = new LinkedList<>();

		private float gravitySpeed = 0.5f, gravitySpeedTimer; // Time, in seconds, to move one down

		private int linesCleared, linesReceived;

		public ClientBoard(int id) { this.id = id; }

		public void nextShape()
		{
			if (shapeQueue.size() <= 0)
			{
				int randomID = Shape.getRandomShapeID();
				Color color = ColorUtil.random(0.45f, 0.55f, 0.93f, 0.97f);

				// request more blocks
				for (ClientBoard board : TetrisServer.this.boards.values())
				{
					board.shapeQueue.add(Shape.getShape(randomID, board.grid(), color));
				}
			}
			Shape shape = shapeQueue.remove();
			((ServerNetManager)Engine.network()).sendReliable(new SetCurrentShapePacket(id, Shape.getIdOf(shape), shape.getColor()));
			setCurrentShape(shape);
		}
	}


	private HashMap<Integer, ClientBoard> boards;


	public TetrisServer()
	{
		boards = new HashMap<>();
	}


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{
		super.onReceivePacket(sender, packet);

		// Input
		if (packet instanceof InputPacket)
		{
			InputPacket inputPacket = (InputPacket)packet;
			ClientBoard board = boards.get(sender.getID());

			if (inputPacket.input() == TetrisInput.Down) { board.dir.y = inputPacket.on ? 1 : 0; }
			else if (inputPacket.input() == TetrisInput.Right) { board.dir.x = inputPacket.on ? 1 : Math.min(board.dir.x, 0); }
			else if (inputPacket.input() == TetrisInput.Left) { board.dir.x = inputPacket.on ? -1 : Math.max(board.dir.x, 0); }
			else if (inputPacket.input() == TetrisInput.Rotate) { board.needsRotation = true; }
		}
	}


	@Override
	public void onPlayerJoin(int senderID, boolean successful)
	{
		super.onPlayerJoin(senderID, successful);

		if (successful)
		{
			boards.put(senderID, new ClientBoard(senderID));

			((ServerNetManager)Engine.network()).sendReliable(senderID, new GameStatePacket(ArrayUtil.toArray(boards.keySet()), new String[] {"NOT IMPLEMENTED YET"}));
		}
	}


	@Override
	public void onGraphicCull(Graphics2D buffer)
	{
		super.onGraphicCull(buffer);

		AffineTransform transform = buffer.getTransform();
		for (ClientBoard board : boards.values())
		{
			int blockSize = Math.min(Engine.canvas().getCurrentWidth() / (board.grid().width() + 1), Engine.canvas().getCurrentHeight() / (board.grid().height() + 1));

			board.grid().draw(buffer, blockSize);
			if (board.getCurrentShape() != null)
			{
				board.getCurrentShape().draw(buffer, blockSize, (board.gravitySpeedTimer - board.gravitySpeed) / ((TetrisConfig) Engine.config()).SHAPE_LOCK_TIME);
			}
			buffer.translate(board.grid().width() * blockSize + 40, 0);
		}
		buffer.setTransform(transform);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (boards.values().size() < 2)
		{
			return;
		}

		for (HashMap.Entry<Integer, ClientBoard> entry : boards.entrySet())
		{
			ClientBoard board = entry.getValue();

			if (board.getCurrentShape() == null)
			{
				board.nextShape();
			}

			boolean didMove = false;

			// Movement
			if (!board.dir.equals(Vec2.zero()))
			{
				board.dir.x = (didMove = board.getCurrentShape().move(board.dir)) ? board.dir.x : 0;
			}
			if (board.needsRotation)
			{
				if (board.needsRotation = board.getCurrentShape().rotate(true))
				{
					((ServerNetManager)Engine.network()).sendReliable(new RotateShapePacket(entry.getKey()));
				}
			}

			// Gravity
			board.gravitySpeedTimer += Time.deltaTime(true);
			if (board.gravitySpeedTimer >= board.gravitySpeed)
			{
				if (board.getCurrentShape().move(Vec2.up()))
				{
					board.gravitySpeedTimer = 0;
					didMove |= true;
				}
				else if (board.gravitySpeedTimer - board.gravitySpeed >= ((TetrisConfig)Engine.config()).SHAPE_LOCK_TIME)
				{
					board.getCurrentShape().lock();
					((ServerNetManager)Engine.network()).sendReliable(new LockCurrentShapePacket(entry.getKey()));

					// Clear lines
					for (int y = 0; y < board.grid().height() - board.linesReceived; y++)
					{
						boolean fullLine = true;
						for (int x = 0; x < board.grid().width(); x++)
						{
							if (!board.grid().hasBlockAt(x, y))
							{
								fullLine = false;
								break;
							}
						}

						if (fullLine)
						{
							board.grid().clearLine(y);
							board.linesCleared++;

							for (ClientBoard b : boards.values())
							{
								if (b != board)
								{
									b.grid().addLine();
									b.linesReceived++;
								}
							}

							((ServerNetManager)Engine.network()).sendReliable(new ClearLinePacket(entry.getKey(), y));
						}
					}

					board.nextShape();
				}
				else if ((board.dir.x != 0 || board.needsRotation))
				{
					board.gravitySpeedTimer = board.gravitySpeed;
				}
			}

			board.needsRotation = false;

			if (didMove)
			{
				((ServerNetManager)Engine.network()).sendReliable(new SetShapePositionPacket(entry.getKey(), board.getCurrentShape().getPosition()));
			}
		}
	}
}
