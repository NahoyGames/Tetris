package util.engine;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;


public class Input extends EngineEventAdapter
{
	private static Input instance;

	private KeyAdapter adapter;

	private HashMap<Integer, Boolean> buttonsDown;
	private HashMap<Integer, Integer> buttonsClicked, buttonsReleased;


	public Input()
	{
		buttonsDown = new HashMap<>();
		buttonsClicked = new HashMap<>();
		buttonsReleased = new HashMap<>();

		adapter = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				buttonsDown.put(e.getKeyCode(), true);

				if (!buttonsClicked.containsKey(e.getKeyCode()))
				{
					buttonsClicked.put(e.getKeyCode(), 0);
				}
				buttonsReleased.remove(e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				buttonsDown.put(e.getKeyCode(), false);

				if (!buttonsReleased.containsKey(e.getKeyCode()))
				{
					buttonsReleased.put(e.getKeyCode(), 0);
				}
				buttonsClicked.remove(e.getKeyCode());
			}
		};

		Engine.canvas().getFrame().addKeyListener(adapter);
		Input.instance = this;
	}


	public static boolean getButton(int keyCode)
	{
		if (instance.buttonsDown.containsKey(keyCode))
		{
			return instance.buttonsDown.get(keyCode);
		}

		return false;
	}


	public static boolean getButtonDown(int keyCode)
	{
		if (instance.buttonsClicked.containsKey(keyCode))
		{
			return instance.buttonsClicked.get(keyCode) <= 1;
		}

		return false;
	}


	public static boolean getButtonUp(int keyCode)
	{
		if (instance.buttonsReleased.containsKey(keyCode))
		{
			return instance.buttonsReleased.get(keyCode) <= 1;
		}

		return false;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		for (Map.Entry<Integer, Integer> entry : buttonsClicked.entrySet())
		{
			entry.setValue(entry.getValue() + 1);
		}
		for (Map.Entry<Integer, Integer> entry : buttonsReleased.entrySet())
		{
			entry.setValue(entry.getValue() + 1);
		}
	}
}
