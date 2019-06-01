package util;

import java.util.Set;

public class ArrayUtil
{
	public static<T> boolean contains(T[] arr, T value)
	{
		for (T v : arr)
		{
			if (v.equals(value))
			{
				return true;
			}
		}

		return false;
	}


	public static boolean contains(char[] arr, char value)
	{
		for (char v : arr)
		{
			if (v == value)
			{
				return true;
			}
		}

		return false;
	}


	public static boolean contains(int[] arr, int value)
	{
		for (int v : arr)
		{
			if (v == value)
			{
				return true;
			}
		}

		return false;
	}


	public static int[] toArray(Set<Integer> set)
	{
		int[] out = new int[set.size()];

		int index = 0;
		for (Integer i : set)
		{
			out[index++] = i;
		}

		return out;
	}
}
