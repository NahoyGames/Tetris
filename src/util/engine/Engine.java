package util.engine;


import util.engine.networking.GenericNetManager;
import util.engine.networking.client.ClientNetManager;
import util.engine.networking.server.ServerNetManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


public class Engine
{
	// Configuration
	private static EngineConfig config;

	// Canvas
	private static Canvas canvas;

	// Networking
	private static GenericNetManager netManager;

	// Gameplay
	private static boolean isPlaying, isPaused;
	private static List<IEngineEventListener> eventListeners;


	public static void init(EngineConfig config)
	{
		Engine.config = config;

		// Gameplay
		isPlaying = true; isPaused = false;
		eventListeners = new ArrayList<>();

		// Network
		if (config.IS_SERVER_BUILD)
		{
			netManager = new ServerNetManager();
		}
		else
		{
			netManager = new ClientNetManager();
		}

		// Graphics
		if (!config.HEADLESS_MODE)
		{
			canvas = new Canvas(config.WINDOW_WIDTH, config.WINDOW_HEIGHT, config.WINDOW_NAME);
			new Input();
		}

		// Game Loop
		Time.step();
		Timer timer = new Timer(1000 / config.FRAMES_PER_SECOND, (ActionEvent) ->
		{
			if (!isPlaying)
			{
				for (IEngineEventListener e : eventListeners)
				{
					e.onApplicationQuit();
				}

				System.exit(0);
				return;
			}

			if (!isPaused)
			{
				// Update listeners
				for (int i = 0; i < eventListeners.size(); i++)
				{
					if (eventListeners.get(i) == null)
					{
						eventListeners.remove(i);
					}
					else
					{
						eventListeners.get(i).onUpdate();
					}
				}

				// Graphics
				if (!Engine.config.HEADLESS_MODE)
				{
					Graphics2D renderBuffer = canvas().getRenderBuffer();

					renderBuffer.setColor(Engine.config.BACKGROUND_COLOR); // Background
					renderBuffer.fillRect(0, 0, canvas().getCurrentWidth(), canvas().getCurrentHeight());

					for (IEngineEventListener e : eventListeners)
					{
						e.onGraphicCull(renderBuffer);
					}
					canvas().repaint();
				}

				// Step time
				Time.step();
			}
		});

		timer.setRepeats(true);
		timer.start();
	}


	public static void initNetwork()
	{
		if (Engine.config() == null)
		{
			System.err.println("No configuration found! Make sure you init the engine first");
			return;
		}

		Engine.network().init();
	}


	public static Canvas canvas() { return canvas; }

	public static EngineConfig config() { return config; }

	public static GenericNetManager network() { return netManager; }

	public static void addListener(IEngineEventListener listener) { eventListeners.add(listener); }

	public static File getResource(String path)
	{
		return new File(Engine.class.getClassLoader().getResource(path).getFile());
	}

	public static void quit()
	{
		for (IEngineEventListener e : eventListeners)
		{
			e.onApplicationQuit();
		}

		System.exit(0);
	}
}
