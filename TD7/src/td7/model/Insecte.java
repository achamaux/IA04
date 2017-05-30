package td7.model;

import sim.engine.SimState;
import sim.util.Bag;

public class Insecte extends MySteppable {
	
	private static final long serialVersionUID = -6091343998610385541L;

	public static int NBRE_POINTS=10;
	public static int ENERGIE_MAX=15;
	public static int CHARGE_MAX=7;
	public static int ENERGIE_MANGER=3;

	public int distanceDeplacement=1;
	public int distancePerception=1;
	public int chargeMax=1;
	
	public double energieActuelle=ENERGIE_MAX;
	public int chargeActuelle=0;
	
	protected int lastMove;

	public int getLastMove() {
		return lastMove;
	}

	public void setLastMove(int lastMove) {
		this.lastMove = lastMove;
	}

	public Insecte(int x, int y) {
		super(x,y);
		
		int i=0;
		while(i<NBRE_POINTS){
			switch ((int)(Math.random()*3)) {
				case 0: distanceDeplacement++; ++i; break;
				case 1: distancePerception++; ++i; break;
				case 2:
					if (chargeMax<CHARGE_MAX) {
						chargeMax++;
						++i;
					}
					break;
			}
		}
	}

	public int getDistanceDeplacement() {
		return distanceDeplacement;
	}

	public void setDistanceDeplacement(int distanceDeplacement) {
		this.distanceDeplacement = distanceDeplacement;
	}

	public int getDistancePerception() {
		return distancePerception;
	}

	public void setDistancePerception(int distancePerception) {
		this.distancePerception = distancePerception;
	}

	public int getChargeMax() {
		return chargeMax;
	}

	public void setChargeMax(int chargeMax) {
		this.chargeMax = chargeMax;
	}

	public double getEnergieActuelle() {
		return energieActuelle;
	}

	public void setEnergieActuelle(int energieActuelle) {
		this.energieActuelle = energieActuelle;
	}

	public int getChargeActuelle() {
		return chargeActuelle;
	}

	public void setChargeActuelle(int chargeActuelle) {
		this.chargeActuelle = chargeActuelle;
	}

	public static int LEVEL = 2;
	@Override
	public void step(SimState state) {
		Beings beings = (Beings) state;
		move(beings);
		if(energieActuelle<=0) meurt(beings);
		energieActuelle--;
	}
	

	public void move(Beings beings) {
		Object currentCase=beings.yard.get(x,y);
		
		// Si je vais mourir, je mange une charge
		if(energieActuelle<=2 && chargeActuelle>0){
			energieActuelle+=ENERGIE_MANGER;
			chargeActuelle--;
			return;
		}
		
		// Sinon je cherche à manger et je mange ou je me charge
		Bag b = beings.yard.getNeighborsMaxDistance(x, y, 1, true, null, null, null);
		for(Object o : b){
			if (o instanceof Nourriture) {
				Nourriture n = (Nourriture)o;
				if (n.quantite > 0) {
					// Si mon energie n'est pas au maximum, je mange
					if(energieActuelle < ENERGIE_MAX){ 
						energieActuelle+=ENERGIE_MANGER;
						n.quantite-=1;
						return;
					}
					// Sinon si ma charge n'est pas au maximum, je me charge
					else if(chargeActuelle < chargeMax){
						chargeActuelle+=1;
						n.quantite-=1;
						return;
					}
				}
			}
		}
		
		// Si il n'y a rien a manger autour de moi je cherche de la nourriture dans mon champ de vision
		b = beings.yard.getNeighborsMaxDistance(x, y, distancePerception, true, null, null, null);
		Nourriture n = null;
		int minDist = 1000000;
		// Je choisis la nourriture la plus proche
		for (Object o : b) {
			if (o instanceof Nourriture) {
				int d = Math.abs(((Nourriture) o).x - x) + Math.abs(((Nourriture) o).y - y);
				if (minDist > d) {
					n = (Nourriture) o;
					minDist = d;
				}
			}
		}

		// Calcul du déplacement le plus court tout en essayant de contourner les autres insectes
		//------------------------------DEBUT CALCUL------------------------------
		int x2=0,y2=0;
		if (n != null) {
			if (x < n.x) {
				if (n.x - x < x+beings.yard.getWidth()-n.x)
					x2++;
				else
					x2--;
			}
			else if (x > n.x) {
				if (x - n.x > n.x+beings.yard.getWidth()-x)
					x2++;
				else
					x2--;
			}
			if (y < n.y) {
				if (n.y - y < y+beings.yard.getHeight()-n.y)
					y2++;
				else
					y2--;
			}
			else if (y > n.y) {
				if (y - n.y > n.y+beings.yard.getHeight()-y)
					y2++;
				else
					y2--;
			}
			
		}
		else {
			int rand=beings.random.nextInt(4);
			while(Math.abs(rand-lastMove) == 2) rand=beings.random.nextInt(4);
			lastMove=rand;
			
			switch (rand) {
				case 0: x2++; break;
				case 2: x2--; break;
				case 1: y2++; break;
				case 3: y2--; break;
				default: break;
			}
		}
		
		int ax=beings.yard.stx(x+x2);
		int ay=beings.yard.stx(y+y2);
		int bx=beings.yard.stx(x);
		int by=beings.yard.stx(y+y2);
		int cx=beings.yard.stx(x+x2);
		int cy=beings.yard.stx(y);
		
		int ox=x2,oy=y2;
		if (beings.yard.get(ax,ay) == null ||  
				(beings.yard.get(ax,ay) instanceof Nourriture)) {
			x2=ax;
			y2=ay;
		}
		else if (beings.yard.get(bx,by) == null ||  
				(beings.yard.get(bx,by) instanceof Nourriture)) {
			x2=bx;
			y2=by;
		}
		else if (beings.yard.get(cx,cy) == null ||  
				(beings.yard.get(cx,cy) instanceof Nourriture)) {
			x2=cx;
			y2=cy;
		}
		else {
			x2 = x;
			y2 = y;
		}
		
		if (beings.yard.get(x2,y2) != null && !(beings.yard.get(x2,y2) instanceof Nourriture)) {
			if (ox == 0 && oy != 0) {
				if (beings.yard.get(beings.yard.stx(x2+1),beings.yard.stx(y+oy)) == null || 
						beings.yard.get(beings.yard.stx(x2+1),beings.yard.stx(y+oy)) instanceof Nourriture)
					x2 = beings.yard.stx(x2+1);
				else
					x2 = beings.yard.stx(x2-1);

				y2 = beings.yard.stx(y+oy);
			}
			else if (oy == 0 && ox != 0) {
				if (beings.yard.get(beings.yard.stx(x+ox),beings.yard.stx(y2+1)) == null || 
						beings.yard.get(beings.yard.stx(x+ox),beings.yard.stx(y2+1)) instanceof Nourriture)
					y2 = beings.yard.stx(y2+1);
				else
					y2 = beings.yard.stx(y2-1);

				x2 = beings.yard.stx(x+ox);
			}
		}
		//------------------------------FIN CALCUL------------------------------
		
		// Je me déplace sur la position calcule si elle est vide
		if (beings.yard.get(x2,y2) == null) {
			beings.yard.set(x, y, null);
			beings.yard.set(x2, y2, this);
			x=x2;
			y=y2;
			
			// Perte de l'énergie liée au déplacement
			double d=(x2-x!=0 && y2-y!=0)? 1.41 : 1;
			//d*=4;
			energieActuelle-=d/distanceDeplacement;
		}
	}
}