package tetris;


import com.esotericsoftware.kryonet.Connection;
import tetris.packets.*;
import util.engine.Engine;
import util.engine.Input;
import util.engine.networking.NetworkAdapter;
import util.engine.networking.client.ClientNetManager;
import util.engine.networking.packets.PlayerSuccessfullyJoinedPacket;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.HashMap;


public class TetrisClient extends NetworkAdapter
{
	public static void main(String[] args)
	{
		Engine.init(new TetrisConfig(false));
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

			boards.put(joinedPacket.id, new Board());
		}
		else if (packet instanceof GameStatePacket)
		{
			GameStatePacket statePacket = (GameStatePacket)packet;

			System.out.println(Arrays.toString(statePacket.connections));

			for (int i : statePacket.connections)
			{
				boards.put(i, new Board());
			}
		}
		else if (packet instanceof SetShapePositionPacket)
		{
			SetShapePositionPacket positionPacket = (SetShapePositionPacket)packet;

			boards.get(positionPacket.connectionID).getCurrentShape().setPosition(positionPacket.getPosition(), true);
		}
		else if (packet instanceof SetCurrentShapePacket)
		{
			SetCurrentShapePacket shapePacket = (SetCurrentShapePacket)packet;

			Board board = boards.get(shapePacket.connectionID);
			board.setCurrentShape(Shape.getShape(shapePacket.shapeID, board.grid(), shapePacket.color()));
		}
		else if (packet instanceof RotateShapePacket)
		{
			boards.get(((RotateShapePacket) packet).connectionID).getCurrentShape().rotate(true);
		}
		else if (packet instanceof LockCurrentShapePacket)
		{
			boards.get(((LockCurrentShapePacket) packet).connectionID).getCurrentShape().lock();
		}
		else if (packet instanceof ClearLinePacket)
		{
			ClearLinePacket linePacket = (ClearLinePacket)packet;
			boards.get(linePacket.connectionID).grid().clearLine(linePacket.lineIndex);
			for (HashMap.Entry<Integer, Board> entry : boards.entrySet())
			{
				if (entry.getKey() != linePacket.connectionID)
				{
					entry.getValue().grid().addLine();
				}
			}
		}
	}


	@Override
	public void onGraphicCull(Graphics2D buffer)
	{
		super.onGraphicCull(buffer);

		AffineTransform transform = buffer.getTransform();
		for (Board board : boards.values())
		{
			int blockSize = Math.min(Engine.canvas().getCurrentWidth() / (board.grid().width() + 1), Engine.canvas().getCurrentHeight() / (board.grid().height() + 1));

			board.grid().draw(buffer, blockSize);
			if (board.getCurrentShape() != null)
			{
				board.getCurrentShape().draw(buffer, blockSize, 1);
			}

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
