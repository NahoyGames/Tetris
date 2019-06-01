package util.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;


/**
 * A simple class which initializes a window, from which you can draw pixels individually.
 */
public class Canvas
{

	private int width;
	private int height;

	private Panel panel;
	private JFrame frame;


	public Canvas(int width, int height, String name, boolean resizeable)
	{
		this.width = width;
		this.height = height;
		this.panel = new Panel(width, height);

		frame = new JFrame(name);

		frame.setFocusable(true);
		frame.requestFocus();

		frame.setSize(width, height);
		frame.setResizable(resizeable);
		if (resizeable)
		{
			panel.addComponentListener(new ComponentAdapter()
			{
				@Override
				public void componentResized(ComponentEvent e)
				{
					super.componentResized(e);

					Dimension size = e.getComponent().getSize();
					panel.bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
				}
			});
		}

		frame.setContentPane(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public Canvas(int width, int height, String name)
	{
		this(width, height, name, true);
	}


	public JFrame getFrame()
	{
		return frame;
	}


	public JPanel getPanel() { return panel; }


	public Graphics2D getRenderBuffer()
	{
		return (Graphics2D) panel.bufferedImage.getGraphics();
	}


	public int getCurrentWidth()
	{
		return getFrame().getWidth();
	}


	public int getCurrentHeight()
	{
		return getFrame().getHeight();
	}


	public void repaint()
	{
		panel.repaint();
	}


	private class Panel extends JPanel
	{
		public BufferedImage bufferedImage;


		public Panel(int width, int height)
		{
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}


		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D) g;
			g2D.drawImage(bufferedImage, null, null);
		}
	}

}
