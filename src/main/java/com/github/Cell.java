package com.github;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cell {

	public enum Flag {
		Column,
		Row,
		Field,
	}

	private Map<Flag, Integer> flags = new HashMap<Cell.Flag, Integer>();
	private Set<Integer> candidates = new HashSet<Integer>(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, }));
	private Integer value;

	public Cell(Map<Flag, Integer> flags) {
		this.flags = flags;
	}

	public Map<Flag, Integer> getFlags() {
		return flags;
	}

	public Set<Integer> getCandidates() {
		return Collections.unmodifiableSet(candidates);
	}

	public void removeAllCandidates(Collection<Integer> toRemove) {
		if (value != null) {
			return;
		}
		if (toRemove.containsAll(candidates)) {
			System.err.println(candidates);
			System.err.println(toRemove);
			System.err.println(flags);
			throw new IllegalArgumentException("cannot remove all candidates");
		}
		candidates.removeAll(toRemove);
		if (candidates.size() == 1) {
			value = candidates.iterator().next();
			candidates.clear();
		}
	}

	public void removeCandidate(Integer num) {
		if (value != null) {
			return;
		}
		candidates.remove(num);
		if (candidates.size() == 1) {
			value = candidates.iterator().next();
			candidates.clear();
		}
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		if (value == null) {
			return;
		}
		candidates.clear();
//		candidates.add(value);
		this.value = value;
	}

	public String renderValue() {
		if (value != null) {
			return "  " + value + "  ";
		}
		if (candidates.size() <= 5) {
			StringBuffer result = new StringBuffer();
			for (Integer k : candidates) {
				result.append(k);
			}
			while (result.length() < 5) {
				result.append(" ");
			}
			return result.toString();
		}
		return String.format(" (%s) ", candidates.size());
	}

	public Integer getFlagValue(Flag flag) {
		return flags.get(flag);
	}

	@Override
	public String toString() {
		return String.format("(%d,%d): %s %s", getFlagValue(Flag.Row), getFlagValue(Flag.Column), value, candidates);
	}
}
