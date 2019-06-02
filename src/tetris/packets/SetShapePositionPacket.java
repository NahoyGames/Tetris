package tetris.packets;


import util.math.Vec2;

public class SetShapePositionPacket
{
	public int x, y;


	public SetShapePositionPacket() { }


	public SetShapePositionPacket(Vec2 pos)
	{
		this.x = pos.x;
		this.y = pos.y;
	}


	public Vec2 getPosition()
	{
		return new Vec2(x, y);
	}
}
