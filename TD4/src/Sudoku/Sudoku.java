package Sudoku;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sudoku {
	private Cellule[][] sudoku;
	
	public Sudoku() {
		sudoku = new Cellule[9][9];

		File file = new File("sudoku");
		Scanner sc = null;

		try {
			sc = new Scanner(file);  

		    for (int i=0; i<9; i++) {
		    	for (int j=0; j<9; j++) {
		    		sudoku[i][j] = new Cellule(sc.nextInt());
		    	}
		    }
		    print(false);
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		} finally {
			if (sc != null) {
			    try {
					sc.close();
				} catch (Exception e) {
				    System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public void print(boolean detail) {
		System.out.println();
		System.out.println();
		
	    for (int j=0; j<9; j++) {
	    	for (int i=0; i<9; i++) {
	    		if (detail) {
	    			System.out.println("[" + j + "][" + i + "]" + sudoku[i][j].getValue() + "-" +sudoku[i][j].getPossibles());
	    		} else {
	    			System.out.print(sudoku[i][j].getValue() + "  ");
	    		}
    		}
	    	if (!detail) {
	    		System.out.println();
	    	}
	    }		
	}
	
	
	
	protected List<Cellule> getCellules(int bloc) {
		List<Cellule> l = new ArrayList<Cellule>();
		if (bloc < 9) {
			for (int i=0; i<9; i++)
				l.add(sudoku[bloc][i]);
		} else if (bloc < 18) {
			for (int i=0; i<9; i++)
				l.add(sudoku[i][bloc-9]);
		} else if (bloc < 27) {
			int line = (bloc - 18)/3;
			int col = (bloc - 18)%3;
			for(int i=0; i<3; i++)
				for(int j=0; j<3; j++) {
					l.add(sudoku[3*line+i][3*col+j]);
				}
		}
		return l;
	}
	protected void setCellules(int bloc, List<Cellule> cellules) {
		List<Cellule> l = getCellules(bloc);
		for (int i=0; i<9; i++) {
			if (cellules.get(i).getValue() > 0)
				l.get(i).defineValue(cellules.get(i).getValue());
			else
				l.get(i).redefinePossibles(cellules.get(i).getPossibles());
		}
	}
	
	
	public int isDone() {
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (sudoku[i][j].getValue() == 0)
					return 0;
			}
		}
		return 1;
	}
	
	public int isCorrect() {
		List<Integer> found = new ArrayList<Integer>();
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (found.contains(sudoku[i][j].getValue()))
					return 0;
				else
					found.add(sudoku[i][j].getValue());
			}
			found.clear();
		}
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (found.contains(sudoku[j][i].getValue()))
					return 0;
				else
					found.add(sudoku[j][i].getValue());
			}
			found.clear();
		}
		return 1;
	}
	
}