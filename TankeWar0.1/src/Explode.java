import java.awt.Color;
import java.awt.Graphics;

public class Explode {
	int x, y;
	private boolean live = true;
	
	 TankClient  tc;
	
	
	int step = 0;
	
	int[] diameter = { 4, 7, 12, 18, 26, 32, 48, 30, 14, 6 };

	
	public Explode (int x, int y, TankClient tc) {
		this.tc = tc;
		this.x = x;
		this.y = y;
	}
	
	public void draw(Graphics g) {
		if (!live){
			tc.explodes.remove(this);
			return;
		}
			
		if(step == diameter.length) {
			live = false;
			step = 0;
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		g.fillOval(x, y, diameter[step], diameter[step]);
		g.setColor(c);
		step++;
		
	}
	
	

}
