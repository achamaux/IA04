package Sudoku;

import java.util.ArrayList;

public class Cellule {

	protected int Valeur;
	protected ArrayList<Integer> ListeValeursPossibles=new ArrayList<Integer>();
	protected int ligne; protected int colonne;
	
	public Cellule(int v, int l, int c){
		Valeur=v;
		this.ligne = l;
		this.colonne = c;
		
		//initialisation des valeurs possibles	
		if (v==0)
		for (int i=1; i<10; ++i){
			ListeValeursPossibles.add(i);
		}
	}
	
	public synchronized void videListePossibles(ArrayList<Integer> v){
		ListeValeursPossibles.removeAll(v);
	}
	
	public synchronized boolean ValeurPossible(int v){
		return ListeValeursPossibles.contains(v);
	}
	
	public synchronized int ValeursBinairePossibles(){
		int nb = 0;
		for (int i : ListeValeursPossibles){
			nb += (1 << i);
		}
		return nb;
	}
	
	public int NbValeursPossible(){
		return ListeValeursPossibles.size();
	}
	
	public void setValeur(int v){
		Valeur = v;
		ListeValeursPossibles.clear();
	}
	
	public int getValeur() {
		return Valeur;
	}
	
}
