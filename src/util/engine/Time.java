package util.engine;

public class Time
{

	private static long lastTime;


	public static void step()
	{
		lastTime = System.currentTimeMillis();
	}


	/**
	 * Delta time between last frame and current frame
	 * @return Milliseconds elapsed
	 */
	public static float deltaTime()
	{
		return (System.currentTimeMillis() - lastTime);
	}


	public static float deltaTime(boolean inSeconds)
	{
		return inSeconds ? deltaTime() / 1000f : deltaTime();
	}

}
