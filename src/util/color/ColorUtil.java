package util.color;

import util.math.Mathf;

import java.awt.*;

public class ColorUtil
{
	public static Color random()
	{
		return random(0, 255, 0, 255, 0, 255);
	}


	public static Color random(int rMin, int rMax, int gMin, int gMax, int bMin, int bMax)
	{
		int r = rMin + (int)(Math.random() * (rMax - rMin + 1));
		int g = gMin + (int)(Math.random() * (gMax - gMin + 1));
		int b = bMin + (int)(Math.random() * (bMax - bMin + 1));

		return new Color(r, g, b);
	}


	public static Color random(float minSaturation, float maxSaturation, float minValue, float maxValue)
	{
		float h = (float)Math.random();
		float s = minSaturation + (float)(Math.random() * (maxSaturation - minSaturation));
		float v = minValue + (float)(Math.random() * (maxValue - minValue));

		return new Color(Color.HSBtoRGB(h, s, v));
	}


	public static Color lerp(Color a, Color b, float lerp)
	{
		lerp = Mathf.clamp(lerp, 0 ,1);
		float oneMinus = 1 - lerp;

		return (new Color(
				Mathf.clamp(((a.getRed() / 255f) * lerp) + ((b.getRed() / 255f) * oneMinus), 0, 1),
				Mathf.clamp(((a.getGreen() / 255f) * lerp) + ((b.getGreen() / 255f) * oneMinus), 0 , 1),
				Mathf.clamp(((a.getBlue() / 255f) * lerp) + ((b.getBlue() / 255f) * oneMinus), 0, 1)
		));
	}
}
