package util.math;


import java.awt.*;

// Designed to be immutable
public class Vec2f
{
	public float x;
	public float y;


	public Vec2f(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public Vec2f() { this(0, 0); }

	public static Vec2f one() { return new Vec2f(1, 1); }
	public static Vec2f zero() { return new Vec2f(0, 0); }
	public static Vec2f up() { return new Vec2f(0, 1); }
	public static Vec2f right() { return new Vec2f(1, 0); }
	public static Vec2f rand(float length) { return (new Vec2f((float)(Math.random() - 0.5), (float)(Math.random() - 0.5))).normalized().scale(length); }


	public static Vec2f add(Vec2f a, Vec2f b)
	{
		return new Vec2f((a.x + b.x), (a.y + b.y));
	}
	public Vec2f add(Vec2f a)
	{
		return add(this, a);
	}


	public static Vec2f subtract(Vec2f a, Vec2f b)
	{
		return new Vec2f((a.x - b.x), (a.y - b.y));
	}
	public Vec2f subtract(Vec2f a)
	{
		return subtract(this, a);
	}


	public static Vec2f scale(Vec2f a, float s)
	{
		return new Vec2f((a.x * s), (a.y * s));
	}
	public Vec2f scale(float s)
	{
		return scale(this, s);
	}


	public static Vec2f scale(Vec2f a, Vec2f b)
	{
		return new Vec2f(a.x * b.x, a.y * b.y);
	}
	public Vec2f scale(Vec2f a) { return scale(this, a); }


	public float lengthSquared()
	{
		return x * x + y * y;
	}
	public float length()
	{
		return (float)Math.sqrt(lengthSquared());
	}


	public static float distanceSquared(Vec2f a, Vec2f b)
	{
		return ((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	public static float distance(Vec2f a, Vec2f b)
	{
		return (float)Math.sqrt(distanceSquared(a, b));
	}


	public static Vec2f normalized(Vec2f a)
	{
		float length = a.length();
		return length == 0 ? Vec2f.zero() : a.scale(1 / length);
	}
	public Vec2f normalized()
	{
		return normalized(this);
	}


	public static Vec2f abs(Vec2f a)
	{
		return new Vec2f(Math.abs(a.x), Math.abs(a.y));
	}
	public Vec2f abs() { return abs(this); }


	public static Vec2f max(Vec2f a, Vec2f b)
	{
		return new Vec2f(Math.max(a.x, b.x), Math.max(a.y, b.y));
	}
	public Vec2f max(Vec2f a) { return max(this, a); }


	public static Vec2f min(Vec2f a, Vec2f b)
	{
		return new Vec2f(Math.min(a.x, b.x), Math.min(a.y, b.y));
	}
	public Vec2f min(Vec2f a) { return min(this, a); }


	public static float vMax(Vec2f a) { return Math.max(a.x, a.y); }
	public float vMax() { return vMax(this); }


	public static Vec2f lerp(Vec2f a, Vec2f b, float lerp)
	{
		lerp = Math.max(0, Math.min(1, lerp));
		return a.scale(lerp).add(b.scale(1 - lerp));
	}


	public Vec2 rounded() { return new Vec2(Math.round(x), Math.round(y)); }


	public boolean equals(Vec2f a) { return this.x == a.x && this.y == a.y; }


	public Vec2f clone() { return new Vec2f(x, y); }


	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
