package util;

import util.math.Vec2;

import java.awt.*;

public class StringUtil
{
	public static void drawCentered(Graphics2D buffer, String text, Font font, Vec2 position, float width)
	{
		float defaultWidth = buffer.getFontMetrics(font).stringWidth(text);

		if (width <= 0)
		{
			width = defaultWidth;
		}
		Font derivedFont = font.deriveFont((width / defaultWidth) * font.getSize2D());

		buffer.setFont(derivedFont);
		buffer.drawString(text, (width / -2f) + position.x, (buffer.getFontMetrics(font).getHeight() / 2f) + position.y);
	}


	public static void drawCentered(Graphics2D buffer, String text, Font font, Vec2 position)
	{
		drawCentered(buffer, text, font, position, -1);
	}


	public static void drawCentered(Graphics2D buffer, String text, Font font)
	{
		drawCentered(buffer, text, font, Vec2.zero());
	}
}
