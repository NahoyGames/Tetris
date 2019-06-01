package util.math;


// Designed to be immutable
public class Vec2
{
	public int x;
	public int y;


	public Vec2(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public Vec2() { this(0, 0); }

	public static Vec2 one() { return new Vec2(1, 1); }
	public static Vec2 zero() { return new Vec2(0, 0); }
	public static Vec2 up() { return new Vec2(0, 1); }
	public static Vec2 right() { return new Vec2(1, 0); }
	public static Vec2 rand(float length) { return (new Vec2((int)(Math.random() - 0.5), (int)(Math.random() - 0.5))).normalized().scale(length); }


	public static Vec2 add(Vec2 a, Vec2 b)
	{
		return new Vec2((a.x + b.x), (a.y + b.y));
	}
	public Vec2 add(Vec2 a)
	{
		return add(this, a);
	}


	public static Vec2 subtract(Vec2 a, Vec2 b)
	{
		return new Vec2((a.x - b.x), (a.y - b.y));
	}
	public Vec2 subtract(Vec2 a)
	{
		return subtract(this, a);
	}


	public static Vec2 scale(Vec2 a, float s)
	{
		return new Vec2((int)(a.x * s), (int)(a.y * s));
	}
	public Vec2 scale(float s)
	{
		return scale(this, s);
	}


	public static Vec2 scale(Vec2 a, Vec2 b)
	{
		return new Vec2(a.x * b.x, a.y * b.y);
	}
	public Vec2 scale(Vec2 a) { return scale(this, a); }


	public float lengthSquared()
	{
		return x * x + y * y;
	}
	public float length()
	{
		return (float)Math.sqrt(lengthSquared());
	}


	public static float distanceSquared(Vec2 a, Vec2 b)
	{
		return ((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	public static float distance(Vec2 a, Vec2 b)
	{
		return (float)Math.sqrt(distanceSquared(a, b));
	}


	public static Vec2 normalized(Vec2 a)
	{
		float length = a.length();
		return length == 0 ? Vec2.zero() : a.scale(1 / length);
	}
	public Vec2 normalized()
	{
		return normalized(this);
	}


	public static Vec2 abs(Vec2 a)
	{
		return new Vec2(Math.abs(a.x), Math.abs(a.y));
	}
	public Vec2 abs() { return abs(this); }


	public static Vec2 max(Vec2 a, Vec2 b)
	{
		return new Vec2(Math.max(a.x, b.x), Math.max(a.y, b.y));
	}
	public Vec2 max(Vec2 a) { return max(this, a); }


	public static Vec2 min(Vec2 a, Vec2 b)
	{
		return new Vec2(Math.min(a.x, b.x), Math.min(a.y, b.y));
	}
	public Vec2 min(Vec2 a) { return min(this, a); }


	public static float vMax(Vec2 a) { return Math.max(a.x, a.y); }
	public float vMax() { return vMax(this); }


	public static Vec2f toFloat(Vec2 a) { return new Vec2f(a.x, a.y); }
	public Vec2f toFloat() { return toFloat(this); }


	public boolean equals(Vec2 a) { return this.x == a.x && this.y == a.y; }


	public Vec2 clone() { return new Vec2(x, y); }


	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
