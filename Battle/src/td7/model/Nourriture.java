package td7.model;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class Nourriture extends MySteppable { 

	
	static public int MAX_QUANTITE=5;
	public int quantite=MAX_QUANTITE;
	
	public Nourriture(int x, int y) {
		super(x,y);
	}


	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	@Override
	public void step(SimState state) {
		if(quantite<=0)
			meurt((Beings)state);
	}
	
	@Override
	public void meurt(Beings state) {
		super.meurt(state);
		state.addAgentNourriture();
	}

}