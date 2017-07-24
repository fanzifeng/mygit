import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

import javax.management.BadAttributeValueExpException;
import javax.sql.RowSetInternal;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import org.omg.CORBA.INTERNAL;

public class Tank {
	private static final int xsudu = 5;
	private static final int ysudu = 5;

	private static final int WIDTH = 30;
	private static final int HEIGHT = 30;

	private boolean live = true;

	private int x, y;
	private boolean bl = false, bu = false, br = false, bd = false;

	private static Random r = new Random();

	enum Direction {
		L, LU, U, RU, R, RD, D, LD, STOP
	};

	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.RD;

	private boolean good;

	public int oldx, oldy;

	int step = r.nextInt(12) + 3;

	TankClient tc;

	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.good = good;
		this.oldx = x;
		this.oldy = y;
	}

	public Tank(int x, int y, boolean good, Direction dir, TankClient tc) {
		this(x, y, good);
		this.tc = tc;
		this.dir = dir;
	}

	public void draw(Graphics g) {
		if (!live) {
			if (!good) {
				tc.badTanks.remove(this);
			}
			return;
		}
		Color c = g.getColor();
		if (good) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLUE);
		}
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);

		switch (ptDir) {
		case L:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT / 2);
			break;
		case LU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y);
			break;
		case LD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT);
			break;
		case U:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y);
			break;
		case R:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT / 2);
			break;
		case RU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.HEIGHT, y);
			break;
		case RD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT);
			break;
		case D:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y + Tank.HEIGHT);
			break;
		}
		move();

	}

	public void move() {
		this.oldx = x;
		this.oldy = y;
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
		case STOP:
			break;
		}
		if (this.dir != Direction.STOP) {
			this.ptDir = this.dir;
		}
		if (x < 0)
			x = 0;
		if (y < 30)
			y = 30;
		if (x + Tank.WIDTH > TankClient.WIDTH)
			x = TankClient.WIDTH - Tank.WIDTH;
		if (y + Tank.HEIGHT > TankClient.HEIGHT)
			y = TankClient.HEIGHT - Tank.HEIGHT;

		if (!good) {
			Direction[] dirs = Direction.values();
			if (step == 0) {
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
				step = r.nextInt(12) + 3;
			}
			step--;

			if (r.nextInt(30) > 25)
				this.fire();
		}

	}

	public void stay() {
		x = oldx;
		y = oldy;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_RIGHT:
			br = true;
			break;
		case KeyEvent.VK_DOWN:
			bd = true;
			break;
		case KeyEvent.VK_LEFT:
			bl = true;
			break;
		case KeyEvent.VK_UP:
			bu = true;
			break;
		}
		locateDirection();
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_CONTROL:
			fire();
			break;
		case KeyEvent.VK_RIGHT:
			br = false;
			break;
		case KeyEvent.VK_DOWN:
			bd = false;
			break;
		case KeyEvent.VK_LEFT:
			bl = false;
			break;
		case KeyEvent.VK_UP:
			bu = false;
			break;
		case KeyEvent.VK_A:
			superFire();
			break;
		}
		locateDirection();
	}

	public void locateDirection() {
		if (bl && !bu && !br && !bd) {
			dir = Direction.L;
		} else if (bl && bu && !br && !bd) {
			dir = Direction.LU;
		} else if (bl && !bu && !br && bd) {
			dir = Direction.LD;
		} else if (!bl && bu && !br && !bd) {
			dir = Direction.U;
		} else if (!bl && !bu && br && !bd) {
			dir = Direction.R;
		} else if (!bl && bu && br && !bd) {
			dir = Direction.RU;
		} else if (!bl && !bu && br && bd) {
			dir = Direction.RD;
		} else if (!bl && !bu && !br && bd) {
			dir = Direction.D;
		} else if (!bl && !bu && !br && !bd) {
			dir = Direction.STOP;
		}

	}

	public Millile fire() {
		if (!live)
			return null;
		int x = this.x + Tank.WIDTH / 2 - Millile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Millile.HEIGHT / 2;
		Millile m = new Millile(x, y, ptDir, tc, good);
		tc.milliles.add(m);
		return m;
	}
	
	public Millile fire(Direction dir) {
		if (!live)
			return null;
		int x = this.x + Tank.WIDTH / 2 - Millile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Millile.HEIGHT / 2;
		Millile m = new Millile(x, y, dir, tc, good);
		tc.milliles.add(m);
		return m;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean isGood() {
		return good;
	}

	public boolean collideWalls(Wall w) {
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.stay();
			return true;
		}
		return false;
	}

	public boolean collideTanks(List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (this != t) {
				if (this.live && t.live && this.getRect().intersects(t.getRect())) {
					this.stay();
					t.stay();
					return true;
				}
			}
		}

		return false;
	}
	
	public void superFire () {
		Direction[] dirs = Direction.values();
		for(int  i = 0; i < 8; i++ ) {
			fire(dirs[i]);
		}
	}

}
