package util.engine;

import java.awt.*;

public interface IEngineEventListener
{
	void onUpdate();

	void onGraphicCull(Graphics2D buffer);

	void onApplicationQuit();
}
