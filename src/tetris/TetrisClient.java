package tetris;


import com.esotericsoftware.kryonet.Connection;
import tetris.packets.*;
import util.engine.Engine;
import util.engine.Input;
import util.engine.networking.NetworkAdapter;
import util.engine.networking.client.ClientNetManager;
import util.engine.networking.packets.PlayerSuccessfullyJoinedPacket;
import util.math.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
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
			Engine.init(new TetrisConfig(args[0], args[1]));
		}
		new TetrisClient();
		Engine.initNetwork();
	}

	private HashMap<Integer, Board> boards;


	public TetrisClient()
	{
		boards = new HashMap<>();
	}


	@Override
	public void onReceivePacket(Connection sender, Object packet)
	{
		super.onReceivePacket(sender, packet);

		if (packet instanceof PlayerSuccessfullyJoinedPacket)
		{
			PlayerSuccessfullyJoinedPacket joinedPacket = (PlayerSuccessfullyJoinedPacket)packet;

			boards.put(joinedPacket.id, new Board(joinedPacket.username));
		}
		else if (packet instanceof GameStatePacket)
		{
			GameStatePacket statePacket = (GameStatePacket)packet;

			System.out.println(Arrays.toString(statePacket.connections));

			int i = 0;
			for (int c : statePacket.connections)
			{
				boards.put(c, new Board(statePacket.usernames[i++]));
			}
		}
		else if (packet instanceof SetShapePositionPacket)
		{
			SetShapePositionPacket positionPacket = (SetShapePositionPacket)packet;

			boards.get(positionPacket.connectionID).getCurrentShape().setPosition(positionPacket.getPosition(), true);
		}
		else if (packet instanceof RotateShapePacket)
		{
			boards.get(((RotateShapePacket) packet).connectionID).getCurrentShape().rotate(true);
		}
		else if (packet instanceof LockCurrentShapePacket)
		{
			LockCurrentShapePacket lockPacket = (LockCurrentShapePacket)packet;
			Shape shape = boards.get(lockPacket.connectionID).getCurrentShape();

			shape.setPosition(lockPacket.position(), true);
			while (shape.getRotation() != lockPacket.rotation)
			{
				shape.rotate(true);
			}
			shape.lock();
		}
		else if (packet instanceof ClearLinePacket)
		{
			ClearLinePacket linePacket = (ClearLinePacket)packet;
			boards.get(linePacket.connectionID).grid().clearLine(linePacket.lineIndex);
			for (HashMap.Entry<Integer, Board> entry : boards.entrySet())
			{
				if (entry.getKey() != linePacket.connectionID && !entry.getValue().hasLost())
				{
					entry.getValue().grid().addLine();
				}
			}
		}
		else if (packet instanceof QueueShapePacket)
		{
			QueueShapePacket shapePacket = (QueueShapePacket)packet;

			for (Board b : boards.values())
			{
				b.queueShape(Shape.getShape(shapePacket.shapeID, b.grid(), shapePacket.color()));
			}
		}
		else if (packet instanceof NextShapePacket)
		{
			boards.get(((NextShapePacket) packet).connectionID).setNextShape();
		}
		else if (packet instanceof PlayerWonPacket)
		{
			JOptionPane.showMessageDialog(Engine.canvas().getPanel(), boards.get(((PlayerWonPacket) packet).connectionID).getUsername() + " has won!");
			Engine.quit();
		}
	}


	@Override
	public void onGraphicCull(Graphics2D buffer)
	{
		super.onGraphicCull(buffer);

		AffineTransform transform = buffer.getTransform();

		buffer.translate(40, 0);

		// Draw my board
		Board myBoard = boards.get(((ClientNetManager)Engine.network()).id());
		if (myBoard != null)
		{
			int blockSize = Math.min(Engine.canvas().getCurrentWidth() / (myBoard.grid().width() + 1), Engine.canvas().getCurrentHeight() / (myBoard.grid().height() + 1));

			myBoard.grid().draw(buffer, blockSize);
			if (myBoard.getCurrentShape() != null && !myBoard.hasLost())
			{
				myBoard.getCurrentShape().draw(buffer, blockSize, 1);
			}
			myBoard.drawQueue(buffer, 40, Vec2.right().scale(-40));

			buffer.translate(myBoard.grid().width() * blockSize + 40, 0);
		}

		// Draw other's boards
		buffer.scale(0.4f, 0.4f);
		for (Board board : boards.values())
		{
			if (board == myBoard) { continue; }

			int blockSize = Math.min(Engine.canvas().getCurrentWidth() / (board.grid().width() + 1), Engine.canvas().getCurrentHeight() / (board.grid().height() + 1));

			board.grid().draw(buffer, blockSize);
			if (board.getCurrentShape() != null && !board.hasLost())
			{
				board.getCurrentShape().draw(buffer, blockSize, 1);
			}

			// Username
			buffer.setColor(new Color(0xB2C6C7));
			buffer.setFont(((TetrisConfig)Engine.config()).TETRIS_FONT);

			FontMetrics metrics = buffer.getFontMetrics(((TetrisConfig)Engine.config()).TETRIS_FONT);
			String string = board.getUsername().toUpperCase();

			buffer.drawString(string, ((board.grid().width() * blockSize) - metrics.stringWidth(string)) / 2, board.grid().height() * blockSize + metrics.getHeight());

			buffer.translate(board.grid().width() * blockSize + 40, 0);
		}
		buffer.setTransform(transform);
	}


	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (Input.getButtonUp(KeyEvent.VK_LEFT) || Input.getButtonDown(KeyEvent.VK_LEFT))
		{
			((ClientNetManager)Engine.network()).sendReliable(new InputPacket(TetrisInput.Left, Input.getButtonDown(KeyEvent.VK_LEFT)));
		}
		if (Input.getButtonUp(KeyEvent.VK_RIGHT) || Input.getButtonDown(KeyEvent.VK_RIGHT))
		{
			((ClientNetManager)Engine.network()).sendReliable(new InputPacket(TetrisInput.Right, Input.getButtonDown(KeyEvent.VK_RIGHT)));
		}
		if (Input.getButtonDown(KeyEvent.VK_UP))
		{
			((ClientNetManager)Engine.network()).sendReliable(new InputPacket(TetrisInput.Rotate, true));
		}
		if (Input.getButtonUp(KeyEvent.VK_DOWN) || Input.getButtonDown(KeyEvent.VK_DOWN))
		{
			((ClientNetManager)Engine.network()).sendReliable(new InputPacket(TetrisInput.Down, Input.getButtonDown(KeyEvent.VK_DOWN)));
		}
	}
}
