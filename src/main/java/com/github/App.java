/*
The MIT License

Copyright (c) 2010 Christoph Gritschenberger

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.github;

import com.sun.xml.internal.messaging.saaj.util.ParseUtil;

/**
 * Hello world!
 *
 */
public class App {

	public static Integer[][] parseSudoku(String string) {
		String[] lines = string.split("\n");
		Integer[][] result = new Integer[9][9];
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			for (int j = 0; j < line.length(); j++) {
				char charAt = line.charAt(j);
				try {
					result[i][j] = new Integer("" + charAt);
				} catch (NumberFormatException e) {
					// ignore
					result[i][j] = null;
				}
			}
		}
		return result;
	}

	public static String s1 = "" +
			"9 681354 \n" +
			"2 1 45 63\n" +
			" 4       \n" +
			"   62   9\n" +
			"  9   2  \n" +
			"7   34   \n" +
			"       9 \n" +
			"59 36 1 4\n" +
			" 274593 6\n";

	public static String nr1546a = "" +
			"3 4 5 2 9\n" +
			"   2 9   \n" +
			"1 94 68 7\n" +
			" 63   12 \n" +
			"8       6\n" +
			" 91   43 \n" +
			"6 78 19 3\n" +
			"   5 3   \n" +
			"4 2 9 5 1\n";

	public static String nr1546b = "" +
			"    9  6 \n" +
			"9 5 1   7\n" +
			"   265   \n" +
			"3   7 9  \n" +
			"6    935 \n" +
			"  1   6  \n" +
			"  49   1 \n" +
			" 7       \n" +
			"    23 9 \n";

	public static String nr1547b = "" +
			"      81 \n" +
			"3  1    6\n" +
			"    2 5  \n" +
			"63  74   \n" +
			"  4 9 2  \n" +
			"  9 8    \n" +
			"4    6  2\n" +
			" 17      \n" +
			"    23 9 \n";

	public static void main(String[] args) {

		Integer[][] works2 = parseSudoku(nr1546a);
		Integer[][] doesNotWork2 = parseSudoku(nr1546b);

		Integer[][] test = parseSudoku(nr1547b);

		Integer[][] works = new Integer[][] {
				new Integer[] { 9, null, 6, 8, 1, 3, 5, 4, null, },
				new Integer[] { 2, null, 1, null, 4, 5, null, 6, 3, },
				new Integer[] { null, 4, null, null, null, null, null, null, null, },
				new Integer[] { null, null, null, 6, 2, null, null, null, 9, },
				new Integer[] { null, null, 9, null, null, null, 2, null, null, },
				new Integer[] { 7, null, null, null, 3, 4, null, null, null, },
				new Integer[] { null, null, null, null, null, null, null, 9, null, },
				new Integer[] { 5, 9, null, 3, 6, null, 1, null, 4, },
				new Integer[] { null, 2, 7, 4, 5, 9, 3, null, 6, }, };

		Integer[][] doesNotWork = new Integer[][] {
				new Integer[] { null, 6, null, null, null, null, null, 9, null, },
				new Integer[] { 7, null, 2, null, null, 4, null, 8, null, },
				new Integer[] { null, 9, 8, null, null, null, null, null, 1, },
				new Integer[] { null, null, null, null, 3, null, null, null, 8, },
				new Integer[] { null, null, null, 7, null, 2, 4, null, null, },
				new Integer[] { null, 3, null, null, 6, null, 2, null, null, },
				new Integer[] { null, null, null, null, 4, 5, null, 7, null, },
				new Integer[] { 3, 1, null, null, null, null, 8, null, null, },
				new Integer[] { null, null, 5, 6, null, null, null, null, null, },
				};

		Sudoku s = new Sudoku(doesNotWork);
		System.out.println(s);
		s.solve();
		System.out.println(s);
	}
}
