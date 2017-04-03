package Sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Sudoku {

	protected Cellule[][] Grille=new Cellule[9][9];
	
	public Sudoku(String fichier){
		Scanner sc;
		try {
			sc = new Scanner(new File(fichier));
			
			int i=0, j=0;
			for(i=0;i<9;i++){
				for(j=0;j<9;j++){
					Grille[i][j]=new Cellule(sc.nextInt(),i,j);
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void affichageSudoku() {
		for(Cellule[] ligne : Grille){
			for(Cellule v : ligne){
				System.out.print(v.getValeur()+" ");
			}
			System.out.print("\n");
		}
	}
	
	public boolean isDone(){
		for (Cellule[] ligne : Grille){
			for (Cellule c : ligne){
				if (c.Valeur == 0)
					return false;
				
			}
		}
		return true;
	}
	
}