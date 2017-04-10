package Sudoku;

import java.util.ArrayList;
import java.util.List;

public class Cellule {
	private List<Integer> possibles;
	private int value;
	
	public Cellule() {
		value = 0;
		possibles = new ArrayList<Integer>();
		for (int i=1; i < 10 ; i++)
			possibles.add(i);
	}
	public Cellule(int val) {
		possibles = new ArrayList<Integer>();
		if (val <= 0) {
			value = 0;
			for (int i=1; i<10; i++)
				possibles.add(i);
		} else {
			value = val;
		}
	}
	public Cellule(List<Integer> possibleValues) {
		value = 0;
		possibles = new ArrayList<Integer>(possibleValues);
	}
	
	public int getValue() {return value;}
	public void setValue(int val) {value = val;}
	public List<Integer> getPossibles() {return new ArrayList<Integer>(possibles);}
	public void setPossibles(List<Integer> poss) {possibles = new ArrayList<Integer>(poss);}
	
	public void removePossible(int val) {
		possibles.remove((Integer)val);
	}
	
	
	public void defineValue(int val) {
		value = val;
		possibles.clear();
	}
	
	// Redéfinit la liste des possibles avec la nouvelle liste en argument
	public void redefinePossibles(List<Integer> val) {
		possibles.retainAll(val);
	}
	
	public void print() {
		System.out.print("val:" + value + "    - ");
		for (int i : possibles) {
			System.out.print(" - " + i);
		}
		System.out.println();
	}

}
