import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class Millile {
	private static final int xsudu = 10;
	private static final int ysudu = 10;
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;

	int x;
	int y;
	Tank.Direction dir;
	private boolean live = true;

	private boolean good;

	TankClient tc;

	public Millile(int x, int y, Tank.Direction dir, TankClient tc, boolean good) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.tc = tc;
		this.good = good;
	}

	public void draw(Graphics g) {
		if (!live)
			return;
		Color c = g.getColor();
		if (good) {
			g.setColor(Color.PINK);
		} else {
			g.setColor(Color.BLACK);
		}
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);

		move();
	}

	private void move() {
		switch (dir) {
		case L:
			x -= xsudu;
			break;
		case LU:
			x -= xsudu;
			y -= ysudu;
			break;
		case LD:
			x -= xsudu;
			y += ysudu;
			break;
		case U:
			y -= ysudu;
			break;
		case R:
			x += xsudu;
			break;
		case RU:
			x += xsudu;
			y -= ysudu;
			break;
		case RD:
			x += xsudu;
			y += ysudu;
			break;
		case D:
			y += ysudu;
			break;
		}
		if (x < 0 || y < 0 || x > TankClient.WIDTH || y > TankClient.HEIGHT) {
			live = false;
		}
	}

	public boolean isLive() {
		return live;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public boolean hitTank(Tank t) {
		if (this.live && this.getRect().intersects(t.getRect()) && t.isLive() && this.good != t.isGood()) {
			t.setLive(false);
			this.live = false;
			Explode e = new Explode(x, y, tc);
			tc.explodes.add(e);
			return true;
		}
		return false;
	}

	public boolean hitTans(List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			if (hitTank(tanks.get(i))) {
				return true;
			}

		}
		return false;
	}

	public boolean hitWall(Wall w) {
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			return true;
		}
		return false;
	}

}
