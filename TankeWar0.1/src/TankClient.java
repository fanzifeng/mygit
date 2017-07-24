import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame {

	public static final int HEIGHT = 800;
	public static final int WIDTH = 600;

	Tank myTank = new Tank(50, 50, true, Tank.Direction.STOP, this);
	Wall w1 = new Wall(50, 350, 20, 350, this);
	Wall w2 = new Wall(200, 550, 520, 20, this);

	List<Explode> explodes = new ArrayList<Explode>();
	List<Millile> milliles = new ArrayList<Millile>();
	List<Tank> badTanks = new ArrayList<Tank>();
	Millile m = null;

	Image offScerrnImage = null;

	@Override
	public void update(Graphics g) {
		if (offScerrnImage == null) {
			offScerrnImage = this.createImage(WIDTH, HEIGHT);
		}
		Graphics goffScerrn = offScerrnImage.getGraphics();
		Color c = goffScerrn.getColor();
		goffScerrn.setColor(Color.WHITE);
		goffScerrn.fillRect(0, 0, WIDTH, HEIGHT);
		paint(goffScerrn);
		g.drawImage(offScerrnImage, 0, 0, null);
	}

	public void paint(Graphics g) {
		for (int i = 0; i < milliles.size(); i++) {
			Millile m = milliles.get(i);
			m.hitTans(badTanks);
			m.hitTank(myTank);
			m.hitWall(w1);
			m.hitWall(w2);
			if (!m.isLive()) {
				milliles.remove(m);
			} else {
				m.draw(g);
			}

		}

		for (int i = 0; i < explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}

		for (int i = 0; i < badTanks.size(); i++) {
			Tank t = badTanks.get(i);
			t.collideWalls(w1);
			t.collideWalls(w2); 
			t.draw(g);
			t.collideTanks(badTanks);
		}

		myTank.draw(g);
		w1.draw(g);
		w2.draw(g);

	}

	public void lauchFrame() {
		for (int i = 0; i < 10; i++) {
			badTanks.add(new Tank(100, 40 * (i + 1), false, Tank.Direction.R, this));
		}
		this.setLocation(600, 200);
		this.setSize(WIDTH, HEIGHT);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setResizable(false); // 禁止用户调整窗口大小
		this.setBackground(Color.WHITE);
		this.setTitle("TankWar");
		this.addKeyListener(new KeyMonitor());
		this.setVisible(true);// 显示窗口
		new Thread(new paintThread()).start();

	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lauchFrame();
	}

	private class paintThread implements Runnable {

		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class KeyMonitor extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}

	}
}
