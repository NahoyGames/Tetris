package tetris.packets;


public class NetBoardPacket
{
	public int connectionID;

	public boolean[][] grid;
	public int colors[]; // Only colors which are used will be stored
	public int linesReceived; // Reduces amount of data needed to be stored in grid & colors
	public byte[] holes;
	public boolean hasLost;


	public NetBoardPacket() { }
}
