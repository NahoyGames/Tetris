package util.engine;


import util.engine.networking.GenericNetManager;
import util.engine.networking.client.ClientNetManager;
import util.engine.networking.server.ServerNetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
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
	private static Timer timer;


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

		// OnApplicationQuit Callback
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				super.run();

				if (isPlaying)
				{
					for (IEngineEventListener e : eventListeners)
					{
						e.onApplicationQuit();
					}
				}
			}
		});

		// Game Loop
		Time.step();
		timer = new Timer(1000 / config.FRAMES_PER_SECOND, (ActionEvent) ->
		{
			if (!isPlaying)
			{
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

	public static InputStream getResource(String path, Object classpath) throws IOException
	{
		return classpath.getClass().getResource("/" + path).openStream();
	}

	public static void pause()
	{
		timer.stop();
	}

	public static void resume()
	{
		timer.start();
	}

	public static void quit()
	{
		for (IEngineEventListener e : eventListeners)
		{
			e.onApplicationQuit();
		}

		isPlaying = false;

		System.exit(0);
	}
}
