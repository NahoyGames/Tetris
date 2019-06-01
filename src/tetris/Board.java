package tetris;


public class Board
{
	private Grid grid;
	private Shape currentShape;


	public Board()
	{
		grid = new Grid();
	}


	public Grid grid() { return grid; }


	public Shape getCurrentShape() { return currentShape; }


	public void setCurrentShape(Shape shape) { currentShape = shape; }
}
