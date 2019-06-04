package util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParseUtil
{
	public static String[][] parseTSV(String file)
	{
		String[] rows = file.split("\\r?\\n");

		return parseTSV(rows);
	}


	public static String[][] parseTSV(String[] fileRows)
	{
		String[][] out = new String[fileRows.length][];

		int i = 0;
		for (String s : fileRows)
		{
			out[i++] = s.split("\\t");
		}
		return out;
	}
}
