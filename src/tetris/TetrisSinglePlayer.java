package tetris;


import util.engine.*;
import util.math.Vec2;

import java.awt.*;
import java.awt.event.KeyEvent;


public class TetrisSinglePlayer extends EngineEventAdapter
{
	public static void main(String[] args)
	{
		Engine.init(new TetrisConfig(true));
		new TetrisSinglePlayer();
	}

	private Board board;

	private float gravitySpeed, gravitySpeedTimer; // Time, in seconds, to move one down


	public TetrisSinglePlayer()
	{
		board = new Board();

		for (int i = 0; i < 2; i++) { board.queueShape(Shape.getRandomShape(board.grid())); }
		board.setNextShape();

		gravitySpeed = 0.5f;
	}


	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (Input.getButton(KeyEvent.VK_DOWN))
		{
			board.getCurrentShape().move(Vec2.up());
		}
		else if (Input.getButton(KeyEvent.VK_LEFT))
		{
			if (board.getCurrentShape().move(Vec2.right().scale(-1)))
			{
				if (gravitySpeedTimer >= gravitySpeed)
				{
					gravitySpeedTimer = gravitySpeed;
				}
			}
		}
		else if (Input.getButton(KeyEvent.VK_RIGHT))
		{
			if (board.getCurrentShape().move(Vec2.right()))
			{
				if (gravitySpeedTimer >= gravitySpeed)
				{
					gravitySpeedTimer = gravitySpeed;
				}
			}
		}
		else if (Input.getButtonDown(KeyEvent.VK_UP))
		{
			board.getCurrentShape().rotate(true);
		}

		// Gravity
		gravitySpeedTimer += Time.deltaTime(true);
		if (gravitySpeedTimer >= gravitySpeed)
		{
			if (board.getCurrentShape().move(Vec2.up()))
			{
				gravitySpeedTimer = 0;
			}
			else if (gravitySpeedTimer - gravitySpeed >= ((TetrisConfig)Engine.config()).SHAPE_LOCK_TIME)
			{
				board.getCurrentShape().lock();
				clearLines();
				board.queueShape(Shape.getRandomShape(board.grid()));
				board.setNextShape();
			}
		}
	}


	@Override
	public void onGraphicCull(Graphics2D buffer)
	{
		int blockSize = Math.min(Engine.canvas().getCurrentWidth() / (board.grid().width() + 1), Engine.canvas().getCurrentHeight() / (board.grid().height() + 1));

		board.grid().draw(buffer, blockSize);
		board.getCurrentShape().draw(buffer, blockSize, (gravitySpeedTimer - gravitySpeed) / ((TetrisConfig)Engine.config()).SHAPE_LOCK_TIME);
	}


	private void clearLines()
	{
		for (int y = 0; y < board.grid().height(); y++)
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
			}
		}
	}
}
