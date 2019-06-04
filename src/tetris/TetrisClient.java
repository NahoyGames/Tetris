package tetris;


import com.esotericsoftware.kryonet.Connection;
import tetris.packets.*;
import util.engine.Engine;
import util.engine.Input;
import util.engine.Time;
import util.engine.networking.NetworkAdapter;
import util.engine.networking.client.ClientNetManager;
import util.engine.networking.packets.PlayerSuccessfullyJoinedPacket;
import util.math.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.HashMap;


public class TetrisClient extends NetworkAdapter
{
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			Engine.init(new TetrisConfig(false));
		}
		else
		{
			String username = args[1].replaceAll("[^a-zA-Z0-9]", "");
			username = username.substring(0, username.length() >= 12 ? 12 : username.length()); // Trim username to 12 characters
			Engine.init(new TetrisConfig(args[0], username));
		}
		new TetrisClient();
		Engine.initNetwork();
	}

	private HashMap<Integer, NetBoard> boards;
	private NetBoard myBoard;

	private float gravitySpeed, gravitySpeedTimer, shapeLockTimer, movementTimer;
	private Vec2 dir;

	private boolean gameStarted = false;


	public TetrisClient()
	{
		boards = new HashMap<>();
		dir = Vec2.zero();
		gravitySpeed = 0.5f;
	}


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{
		super.onReceivePacket(sender, packet);

		if (packet instanceof PlayerSuccessfullyJoinedPacket)
		{
			PlayerSuccessfullyJoinedPacket joinedPacket = (PlayerSuccessfullyJoinedPacket)packet;

			boards.put(joinedPacket.id, new NetBoard(joinedPacket.username, joinedPacket.id));
		}
		else if (packet instanceof GameStatePacket)
		{
			GameStatePacket statePacket = (GameStatePacket)packet;

			int i = 0;
			for (int c : statePacket.connections)
			{
				boards.put(c, new NetBoard(statePacket.usernames[i++], c));
			}
		}
//		else if (packet instanceof LockCurrentShapePacket)
//		{
//			LockCurrentShapePacket lockPacket = (LockCurrentShapePacket)packet;
//			Shape shape = boards.get(lockPacket.connectionID).getCurrentShape();
//
//			shape.setPosition(lockPacket.position(), true);
//			shape.rotate(true, shape.getRotation() - lockPacket.rotation, true);
//			shape.lock();
//
//			boards.get(lockPacket.connectionID).setNextShape();
//		}
		else if (packet instanceof NetBoardPacket)
		{
			NetBoardPacket boardPacket = (NetBoardPacket)packet;

			boards.get(boardPacket.connectionID).deserializePacket(boardPacket);
		}
		else if (packet instanceof QueueShapePacket)
		{
			gameStarted = true;

			QueueShapePacket shapePacket = (QueueShapePacket)packet;

			for (Board b : boards.values())
			{
				b.queueShape(Shape.getShape(shapePacket.shapeID, b.grid(), shapePacket.color()));

				if (b.getCurrentShape() == null)
				{
					b.setNextShape();
				}
			}
		}
		else if (packet instanceof PlayerWonPacket)
		{
			Engine.pause();
			JOptionPane.showMessageDialog(Engine.canvas().getPanel(),
					boards.get(((PlayerWonPacket) packet).connectionID).getUsername() + " has won!",
					"Game Over",
					JOptionPane.PLAIN_MESSAGE);
			Engine.quit();
		}
	}


	@Override
	public void onGraphicCull(Graphics2D buffer)
	{
		super.onGraphicCull(buffer);

		AffineTransform transform = buffer.getTransform();

		/** DRAW MY BOARD **/
		Board myBoard = boards.get(((ClientNetManager)Engine.network()).id());
		if (myBoard != null)
		{
			myBoard.drawQueue(buffer, 40, Vec2.zero());

			buffer.translate(40, 0);

			myBoard.grid().draw(buffer);
			if (myBoard.getCurrentShape() != null && !myBoard.hasLost())
			{
				myBoard.getCurrentShape().draw(buffer, shapeLockTimer / ((TetrisConfig)Engine.config()).SHAPE_LOCK_TIME);
			}

			buffer.translate(myBoard.grid().width() * myBoard.grid().blockSize() + 40, 0);
		}

		/** DRAW OTHER'S BOARDS **/
		buffer.scale(0.4f, 0.4f);
		for (Board board : boards.values())
		{
			if (board == myBoard) { continue; }

			board.grid().draw(buffer);

			// Username
			buffer.setColor(new Color(0xB2C6C7));
			buffer.setFont(((TetrisConfig)Engine.config()).TETRIS_FONT);

			FontMetrics metrics = buffer.getFontMetrics(((TetrisConfig)Engine.config()).TETRIS_FONT);
			String string = board.getUsername().toUpperCase();

			buffer.drawString(string, ((board.grid().width() * board.grid().blockSize()) - metrics.stringWidth(string)) / 2, board.grid().height() * board.grid().blockSize() + metrics.getHeight());

			buffer.translate(board.grid().width() * board.grid().blockSize() + 40, 0);
		}
		buffer.setTransform(transform);

		/** DRAW LOBBY GUI **/
		if (!gameStarted)
		{
			buffer.setColor(new Color(18, 18, 18, 190));
			buffer.fillRect(0,0, Engine.canvas().getCurrentWidth(), Engine.canvas().getCurrentHeight());

			Font font;
			buffer.setColor(new Color(0xF1E7F1));

			buffer.setFont(font = ((TetrisConfig)Engine.config()).TETRIS_FONT.deriveFont(30f));
			FontMetrics metrics = buffer.getFontMetrics(font);
			String string = "Waiting    for    players";
			buffer.drawString(string, (Engine.canvas().getCurrentWidth() - metrics.stringWidth(string)) / 2, (Engine.canvas().getCurrentHeight() + metrics.getHeight()) / 2);

			buffer.setFont(font = ((TetrisConfig)Engine.config()).TETRIS_FONT.deriveFont(15f));
			metrics = buffer.getFontMetrics(font);
			string = "The    game    will    start    shortly";
			buffer.drawString(string, (Engine.canvas().getCurrentWidth() - metrics.stringWidth(string)) / 2, (Engine.canvas().getCurrentHeight() + metrics.getHeight()) / 2 + 50);

		}
	}


	@Override
	public void onPlayerDisconnect(int senderID)
	{
		super.onPlayerDisconnect(senderID);

		boards.remove(senderID);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		ClientNetManager netManager = ((ClientNetManager)Engine.network());

		/** NULL CHECK **/
		if (myBoard == null)
		{
			System.out.println("My board is null");

			if ((myBoard = boards.get(netManager.id())) == null)
			{
				return;
			}
		}
		if (myBoard.getCurrentShape() == null)
		{
			System.out.println("My shape is null");

			if (myBoard.setNextShape() == null)
			{
				return;
			}
		}

		/** INPUT & ROTATION **/
		if (Input.getButtonUp(KeyEvent.VK_LEFT) || Input.getButtonDown(KeyEvent.VK_LEFT))
		{
			this.dir.x = Input.getButton(KeyEvent.VK_LEFT) ? -1 : Math.max(this.dir.x, 0);
		}
		if (Input.getButtonUp(KeyEvent.VK_RIGHT) || Input.getButtonDown(KeyEvent.VK_RIGHT))
		{
			this.dir.x = Input.getButton(KeyEvent.VK_RIGHT) ? 1 : Math.min(this.dir.x, 0);
		}
		if (Input.getButtonDown(KeyEvent.VK_UP) || Input.getButtonDown(KeyEvent.VK_Z))
		{
			if (myBoard.getCurrentShape().rotate(true))
			{
				shapeLockTimer = 0f;
			}
		}
		if (Input.getButtonUp(KeyEvent.VK_DOWN) || Input.getButtonDown(KeyEvent.VK_DOWN))
		{
			this.dir.y = Input.getButton(KeyEvent.VK_DOWN) ? 1 : 0;
		}

		/** MOVEMENT **/
		movementTimer += Time.deltaTime(true);
		if (movementTimer >= 1f / ((TetrisConfig)Engine.config()).SHAPE_MOVEMENT_SPEED)
		{
			if (!this.dir.equals(Vec2.zero()) && myBoard.getCurrentShape().move(this.dir))
			{
				shapeLockTimer = 0f;
			}
			movementTimer = 0f;
		}

		/** GRAVITY **/
		gravitySpeedTimer += Time.deltaTime(true);
		if (gravitySpeedTimer >= gravitySpeed)
		{
			if (myBoard.getCurrentShape().move(Vec2.up())) // Was able to fall
			{
				gravitySpeedTimer = shapeLockTimer = 0f;
			}
			else
			{
				shapeLockTimer += Time.deltaTime(true); // Else, start locking shape
			}
		}

		/** SHAPE LOCK & NETWORKING **/
		if (shapeLockTimer >= ((TetrisConfig)Engine.config()).SHAPE_LOCK_TIME)
		{
			myBoard.getCurrentShape().lock(); // Lock locally

			netManager.sendReliable(new LockCurrentShapePacket(netManager.id(), myBoard.getCurrentShape())); // Notify server

			myBoard.setNextShape(); // Next shape
		}

		/** CLEAR LINES **/
		for (NetBoard board : boards.values())
		{
			for (int y = 0; y < board.grid().height() - board.getLinesReceived(); y++)
			{
				if (board.grid().hasFullLine(y))
				{
					board.clearLine(y);
					if (board == myBoard) { this.gravitySpeed *= 0.9f; } // Increase gravity and make the game harder

					// Add a line to everyone else
					for (NetBoard b : boards.values())
					{
						if (b != board && !b.hasLost())
						{
							b.addLine();

							if (b == myBoard)
							{
								b.getCurrentShape().setPosition(b.getCurrentShape().getPosition().subtract(Vec2.up()), true);
							}
						}
					}
				}
			}
		}
	}
}
