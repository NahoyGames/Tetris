package util.engine;

import java.awt.*;

public class EngineEventAdapter implements IEngineEventListener
{
	public EngineEventAdapter()
	{
		Engine.addListener(this);
	}

	@Override
	public void onUpdate()
	{

	}

	@Override
	public void onGraphicCull(Graphics2D buffer)
	{

	}

	@Override
	public void onApplicationQuit()
	{

	}
}
