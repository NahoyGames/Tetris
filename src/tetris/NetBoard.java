package tetris;


public class NetBoard extends Board
{
	private int id;
	private int linesCleared, linesReceived;


	public NetBoard(String username, int id)
	{
		super(username);
		this.id = id;
	}


	public int id()
	{
		return id;
	}


	public void clearLine(int y)
	{
		this.grid().clearLine(y);
		this.linesCleared++;
	}


	public void addLine()
	{
		this.grid().addLine();
		this.linesReceived++;
	}


	public int getLinesCleared()
	{
		return linesCleared;
	}


	public int getLinesReceived()
	{
		return linesReceived;
	}
}
