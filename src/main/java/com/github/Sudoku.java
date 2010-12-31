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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.Cell.Flag;

public class Sudoku {

	private static final int CELL_LENGTH = 5;
	// [] line
	// [] column
	// private Integer[][] values;
	private Collection<Cell> allCells = new ArrayList<Cell>();
	private List<List<Cell>> rows = new ArrayList<List<Cell>>();
	private List<List<Cell>> columns = new ArrayList<List<Cell>>();
	private List<List<Cell>> fields = new ArrayList<List<Cell>>();

	public Sudoku() {
		for (int i = 0; i < 9; i++) {
			rows.add(i, new ArrayList<Cell>());
			columns.add(i, new ArrayList<Cell>());
			fields.add(i, new ArrayList<Cell>());
		}
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				Map<Flag, Integer> flags = new HashMap<Cell.Flag, Integer>();
				flags.put(Flag.Row, i);
				flags.put(Flag.Column, j);
				final int field = i / 3 * 3 + j / 3;
				flags.put(Flag.Field, field);
				Cell cell = new Cell(flags);
				allCells.add(cell);
				rows.get(i).add(cell);
				columns.get(j).add(cell);
				fields.get(field).add(cell);
			}
		}
	}

	public Sudoku(Integer[][] values) {
		this();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				rows.get(i).get(j).setValue(values[i][j]);
			}
		}
	}

	public Set<Integer> getLine(int i) {
		return getAllCellValues(rows.get(i));
	}

	public Set<Integer> getColumn(int i) {
		return getAllCellValues(columns.get(i));
	}

	public Set<Integer> getField(int k) {
		return getAllCellValues(fields.get(k));
	}

	private Set<Integer> getAllCellValues(Collection<Cell> cells) {
		Set<Integer> result = new HashSet<Integer>();
		for (Cell c : cells) {
			if (c.getValue() != null) {
				result.add(c.getValue());
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
		for (int s = 0; s < 3; s++) {
			result.append("++-----+-----+-----++-----+-----+-----++-----+-----+-----++\n");
			for (int t = 0; t < 3; t++) {
				int l = s * 3 + t;
				result.append(formatLine(l));
				result.append("++-----+-----+-----++-----+-----+-----++-----+-----+-----++\n");
			}
		}
		result.append("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
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
				final Cell cell = rows.get(i).get(c);
				result.append(cell);
				result.append("|");
			}
		}
		result.append("|\n");
		return result.toString();
	}

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

	// private void setValue(int i, int j, int value) {
	// if (values[i][j] != null) {
	// throw new IllegalArgumentException();
	// }
	//
	// // validate
	// final Collection<Integer> line = getLine(i);
	// if (line.contains(value)) {
	// throw new IllegalStateException();
	// }
	// final Collection<Integer> column = getColumn(j);
	// if (column.contains(value)) {
	// throw new IllegalStateException();
	// }
	// final Collection<Integer> fieldForCell = getFieldForCell(i, j);
	// if (fieldForCell.contains(values)) {
	// throw new IllegalStateException();
	// }
	//
	// changed = true;
	// values[i][j] = value;
	// candidates[i][j] = null;
	// }

	private void iterateSolution() {
		changed = false;
		recalcCandidates();
		validate();
		checkForSingletonCandidates();
		validate();
		eliminateGroupUniqueCandidates();
		validate();
		checkColumnCandidatesBlockingAFieldOrRow();
		validate();
	}

	private List<Cell> getCellCandidatesForNumber(List<Cell> cells, Integer number) {
		List<Cell> result = new ArrayList<Cell>();
		for (Iterator<Cell> iterator = cells.iterator(); iterator.hasNext();) {
			Cell cell = iterator.next();
			if (!cell.getCandidates().contains(number)) {
				result.add(cell);
			}
		}
		return result;
	}

	private void checkColumnCandidatesBlockingAFieldOrRow() {
		for (int j = 0; j < 9; j++) {
			Set<Integer> allCandidates = getAllCandidates(columns.get(j));
			for (Integer num : allCandidates) {
				Set<Integer> validRows = new HashSet<Integer>();
				Set<Integer> validFields = new HashSet<Integer>();

				List<Cell> cellCandidatesForNumber = getCellCandidatesForNumber(columns.get(j), num);
				for (Cell c : cellCandidatesForNumber) {
					validRows.add(c.getFlags().get(Flag.Row));
					validFields.add(c.getFlags().get(Flag.Field));
				}

				// System.err.println(validRows);
				// System.err.println(validFields);

				if (validRows.size() == 1) {
					System.err.println("! Only 1 valid row for value " + num + " in column " + j);
				}

				if (validFields.size() == 1) {
					System.err.println("! Only 1 valid field for value " + num + " in column " + j);
					System.err.println(validFields);
				}
			}
		}
		// for (int x = 0; x < 3; x++) {
		// for (int y = 0; y < 3; y++) {
		// for (Integer k : getAllCandidtesForField(x, y)) {
		// Set<Integer> validColumns = new HashSet<Integer>();
		// Set<Integer> validRows = new HashSet<Integer>();
		// for (int i = x * 3; i < x * 3 + 3; i++) {
		// for (int j = y * 3; j < y * 3 + 3; j++) {
		// List<Integer> canditates = getCanditates(i, j);
		// if (canditates != null && canditates.contains(k)) {
		// validRows.add(i);
		// validColumns.add(j);
		// }
		// }
		// }
		// if (validColumns.size() == 1) {
		// Integer col = validColumns.iterator().next();
		// for (int i = 0; i < 9; i++) {
		// if (i / 3 != x) {
		// if (this.candidates[i][col] != null) {
		// this.candidates[i][col].remove(k);
		// }
		// }
		// }
		// System.err.println(String.format("%s in field (%s,%s) is locked to column %s",
		// k, x, y, col));
		// }
		// if (validRows.size() == 1) {
		// Integer row = validRows.iterator().next();
		// for (int j = 0; j < 9; j++) {
		// if (j / 3 != y) {
		// if (this.candidates[row][j] != null) {
		// this.candidates[row][j].remove(k);
		// }
		// }
		// }
		// System.err.println(String.format("%s in field (%s,%s) is locked to column %s",
		// k, x, y, row));
		// System.err.println("_____________________");
		// }
		// }
		// }
		//
		// }
	}

	public boolean isSolved() {
		for (Cell c : allCells) {
			if (c.getValue() == null) {
				return false;
			}
		}
		return true;
	}

	private void checkForSingletonCandidates() {
		for (Cell cell : allCells) {
			Set<Integer> cand = cell.getCandidates();
			if (cand.size() == 1) {
				changed = true;
				cell.setValue(cand.iterator().next());
			}
		}
	}

	private List<List<Cell>> getCellListForFlag(Flag flag) {
		switch (flag) {
		case Row:
			return rows;
		case Column:
			return columns;
		case Field:
			return fields;
		default:
			throw new IllegalArgumentException();
		}
	}

	private Set<Integer> getAllCandidates(Flag flag, Integer value, Cell origin) {
		List<Cell> copy = getAllCellsExceptOrigin(getCellListForFlag(flag).get(value), origin);
		return getAllCandidates(copy);
	}

	private Set<Integer> getAllCandidates(List<Cell> cells) {
		Set<Integer> result = new HashSet<Integer>();
		for (Cell c : cells) {
			if (c.getCandidates() != null) {
				result.addAll(c.getCandidates());
			}
		}
		return result;
	}

	private List<Cell> getAllCellsExceptOrigin(List<Cell> list, Cell origin) {
		List<Cell> copy = new ArrayList<Cell>(list);
		copy.remove(origin);
		return copy;
	}

	private void eliminateGroupUniqueCandidates() {
		for (Cell c : allCells) {
			if (c.getValue() != null) {
				continue;
			}
			solveValueIfCandidateIsUnique(c, Flag.Row);
			solveValueIfCandidateIsUnique(c, Flag.Column);
			solveValueIfCandidateIsUnique(c, Flag.Field);
		}
	}

	private void solveValueIfCandidateIsUnique(Cell c, Flag flag) {
		List<Integer> cand = new ArrayList<Integer>(c.getCandidates());
		Set<Integer> setOfRowCandidates = getAllCandidates(flag, c.getFlags().get(flag), c);
		cand.removeAll(setOfRowCandidates);
		validate();
	}

	private void recalcCandidates() {
		for (Cell cell : allCells) {
			recalcCandidatesForCell(cell);
		}
	}

	private Set<Integer> recalcCandidatesForCell(Cell cell) {
		cell.removeAllCandidates(getLine(cell.getFlags().get(Flag.Row)));
		cell.removeAllCandidates(getColumn(cell.getFlags().get(Flag.Column)));
		cell.removeAllCandidates(getField(cell.getFlags().get(Flag.Field)));
		validate();
		return cell.getCandidates();
	}

	private void validate() {
		for (Cell c : allCells) {
			if(c.getValue() == null){
				continue;
			}
			for (Cell other : rows.get(c.getFlags().get(Flag.Row))) {
				if (c.equals(other)) {
					continue;
				}
				if (c.getValue() == other.getValue()) {
					System.err.println(this);
					throw new IllegalStateException(c + " " + other);
				}
			}
			for (Cell other : columns.get(c.getFlags().get(Flag.Column))) {
				if (c.equals(other)) {
					continue;
				}
				if (c.getValue() == other.getValue()) {
					System.err.println(this);
					throw new IllegalStateException(c + " " + other);
				}
			}
			for (Cell other : fields.get(c.getFlags().get(Flag.Field))) {
				if (c.equals(other)) {
					continue;
				}
				if (c.getValue() == other.getValue()) {
					System.err.println(this);
					throw new IllegalStateException(c + " " + other);
				}
			}
		}

	}
}
