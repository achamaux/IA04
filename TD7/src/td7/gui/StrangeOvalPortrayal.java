package td7.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import td7.model.Insecte;

public class StrangeOvalPortrayal extends OvalPortrayal2D {

	private static final long serialVersionUID = 8276324584259783509L;

	public StrangeOvalPortrayal() {
		super();
	paint = Color.GRAY;
	filled = true;
	}

	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		Insecte agent = (Insecte)object;
		if (agent.x % 5 == 0 && agent.y % 5 == 0)
           scale = 2; 
		else scale = 1;
		super.draw(object, graphics, info);
	}
	

}
