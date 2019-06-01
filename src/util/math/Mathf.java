package util.math;

import java.awt.*;

public class Mathf
{




	public static float clamp(float value, float min, float max)
	{
		return Math.max(min, Math.min(max, value));
	}

}
