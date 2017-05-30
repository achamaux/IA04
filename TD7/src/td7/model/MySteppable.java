package td7.model;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public abstract class MySteppable implements Steppable {

	public int x;
	public int y;
	public Stoppable stoppable;
	
	@Override
	public abstract void step(SimState arg0);
	
	protected void meurt(Beings beings) {
		beings.yard.set(x, y, null);
		stoppable.stop();
	}

	public MySteppable(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public String toString(){
		return this.getClass().getName();
	}
}
