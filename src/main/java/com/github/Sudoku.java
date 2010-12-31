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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sudoku {

	// [] line
	// [] column
	private Integer[][] values;

	public Sudoku() {
	}

	public Sudoku(Integer[][] values) {
		super();
		this.values = values;
		recalcCandidates();
	}

	public Collection<Integer> getLine(int i) {
		return Arrays.asList(values[i]);
	}

	public Collection<Integer> getColumn(int i) {
		Collection<Integer> result = new ArrayList<Integer>();
		for (Integer[] row : values) {
			result.add(row[i]);
		}
		return result;
	}

	public Collection<Integer> getFieldForCell(int i, int j) {
		return getField(i / 3, j / 3);
	}

	public Collection<Integer> getField(int i, int j) {
		Collection<Integer> result = new ArrayList<Integer>();
		int firstRow = i * 3;
		int firstColumn = j * 3;
		for (int x = firstRow; x < firstRow + 3; x++) {
			for (int y = firstColumn; y < firstColumn + 3; y++) {
				result.add(values[x][y]);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("+++++++++++++++++++++++++++++++++++++++++\n");
		for (int s = 0; s < 3; s++) {
			result.append("++---+---+---++---+---+---++---+---+---++\n");
			for (int t = 0; t < 3; t++) {
				int l = s * 3 + t;
				result.append(formatLine(l));
				result.append("++---+---+---++---+---+---++---+---+---++\n");
			}
		}
		result.append("+++++++++++++++++++++++++++++++++++++++++\n");
		return result.toString();
	}

	private String formatLine(int i) {
		StringBuffer result = new StringBuffer();
		result.append("|");
		for (int s = 0; s < 3; s++) {
			result.append("" +
					"|");
			for (int t = 0; t < 3; t++) {
				int c = s * 3 + t;
				Integer val = values[i][c];
				if (val == null) {
					List<Integer> cand = candidates[i][c];
					if (cand.size() == 2) {
						result.append(String.format("%s,%s|", cand.get(0), cand.get(1)));
					} else {
						result.append(String.format("(%s)|", candidates[i][c].size()));
					}
					// result.append("   |");
				} else {
					result.append(" " + values[i][c] + " |");
				}
			}
		}
		result.append("|\n");
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	private List<Integer>[][] candidates = new List[9][9];
	int count = 0;
	private boolean changed;

	public void solve() {
		for (int i = 0; i < 100; i++) {
			iterateSolution();
			if (!changed) {
				System.out.println("break up after" + i + " iterations");
				return;
			}
		}
	}

	private void setValue(int i, int j, int value) {
		if (values[i][j] != null) {
			throw new IllegalArgumentException();
		}

		// validate
		final Collection<Integer> line = getLine(i);
		if (line.contains(value)) {
			throw new IllegalStateException();
		}
		final Collection<Integer> column = getColumn(j);
		if (column.contains(value)) {
			throw new IllegalStateException();
		}
		final Collection<Integer> fieldForCell = getFieldForCell(i, j);
		if (fieldForCell.contains(values)) {
			throw new IllegalStateException();
		}

		changed = true;
		values[i][j] = value;
		candidates[i][j] = null;
	}

	private void iterateSolution() {
		recalcCandidates();
		changed = false;
		checkForSingletonCandidates();
		eliminateGroupCandidates();
	}

	public boolean isSolved() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (values[i][j] == null) {
					return false;
				}
			}
		}
		return true;
	}

	private void checkForSingletonCandidates() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				List<Integer> cand = candidates[i][j];
				if (cand != null && cand.size() == 1) {
					setValue(i, j, cand.get(0));
				}
			}
		}
	}

	private void eliminateGroupCandidates() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				List<Integer> realCand = getCanditates(i, j);
				if (realCand == null) {
					continue;
				}
				List<Integer> cand = new ArrayList<Integer>(realCand);
				Set<Integer> setOfRowCandidates = getSetOfRowCandidates(i, j);
				cand.removeAll(setOfRowCandidates);
				if (cand.size() == 1) {
					setValue(i, j, cand.get(0));
					continue;
				}

				cand = new ArrayList<Integer>(realCand);
				Set<Integer> setOfColumnCandidates = getSetOfColumnCandidates(j, i);
				cand.removeAll(setOfColumnCandidates);
				if (cand.size() == 1) {
					setValue(i, j, cand.get(0));
					continue;
				}

				cand = new ArrayList<Integer>(realCand);
				Set<Integer> setOfFieldCandidates = getSetOfFieldCandidates(i / 3, j / 3, i, j);
				cand.removeAll(setOfFieldCandidates);
				if (cand.size() == 1) {
					setValue(i, j, cand.get(0));
					continue;
				}
			}
		}
	}

	public Set<Integer> getSetOfRowCandidates(int row, int skipcol) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < 9; i++) {
			if (i == skipcol) {
				continue;
			}
			final List<Integer> c = candidates[row][i];
			if (c != null) {
				result.addAll(c);
			}
		}
		return result;
	}

	public Set<Integer> getSetOfColumnCandidates(int col, int skiprow) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = 0; i < 9; i++) {
			if (i == skiprow) {
				continue;
			}
			final List<Integer> c = candidates[i][col];
			if (c != null) {
				result.addAll(c);
			}
		}
		return result;
	}

	public Set<Integer> getSetOfFieldCandidates(int x, int y, int skipi, int skipj) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i = x * 3; i < x * 3 + 3; i++) {
			for (int j = y * 3; j < y * 3 + 3; j++) {
				if (i == skipi && j == skipj) {
					continue;
				}
				final List<Integer> c = candidates[i][j];
				if (c != null) {
					result.addAll(c);
				}
			}
		}
		return result;
	}

	private void recalcCandidates() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				candidates[i][j] = getCanditates(i, j);
			}
		}
	}

	private List<Integer> getCanditates(int i, int j) {
		Integer val = values[i][j];
		if (val != null) {
			return null;
		}
		List<Integer> candidates = makeCandidates();
		candidates.removeAll(getLine(i));
		candidates.removeAll(getColumn(j));
		candidates.removeAll(getFieldForCell(i, j));
		return candidates;
	}

	private List<Integer> makeCandidates() {
		List<Integer> candidates = new ArrayList<Integer>();
		for (int i : new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, }) {
			candidates.add(i);
		}
		return candidates;
	}
}
