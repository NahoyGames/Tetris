package tetris.packets;

import tetris.Shape;
import util.math.Vec2;

public class LockCurrentShapePacket
{
	public int connectionID;
	public int x, y, rotation;


	public LockCurrentShapePacket() { }


	public LockCurrentShapePacket(int connectionID, Shape shape)
	{
		this.connectionID = connectionID;
		this.x = shape.getPosition().x;
		this.y = shape.getPosition().y;
		this.rotation = shape.getRotation();
	}


	public Vec2 position() { return new Vec2(x, y); }
}
