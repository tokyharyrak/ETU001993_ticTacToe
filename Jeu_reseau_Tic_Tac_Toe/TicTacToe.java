import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TicTacToe implements Runnable {

	public String ip = "localhost";
	public int port = 22222;
	public Scanner scanner = new Scanner(System.in);
	public JFrame frame;
	public final int WIDTH = 506;
	public final int HEIGHT = 527;
	public Thread thread;

	public Painter painter;
	public Socket socket;
	public DataOutputStream dos;
	public DataInputStream dis;

	public ServerSocket serverSocket;

	public BufferedImage terrain;
	public BufferedImage redX;
	public BufferedImage blueX;
	public BufferedImage redO;
	public BufferedImage blueO;

	public String[] espace = new String[9];

	public boolean anjaranao = false;
	public boolean circle = true;
	public boolean ekena = false;
	public boolean tsyafakamiteny = false;
	public boolean nandresy = false;
	public boolean resy = false;
	public boolean naitsy = false;

	public int elanelana = 160;
	public int errors = 0;
	public int firstSpot = -1;
	public int secondSpot = -1;

	public Font soratra = new Font("Verdana", Font.BOLD, 32);
	public Font soratrakely = new Font("Verdana", Font.BOLD, 20);
	public Font soratrabe = new Font("Verdana", Font.BOLD, 50);

	public String mampiandry = "Miandry mpilalao iray";
	public String tsyafakamiresaka = "Mbola tsy mifandray";
	public String victoire = "Nandresy enao!";
	public String defaite = "Resy enao!";
	public String sahala = "sahala ny lalao";

	public int[][] wins = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } };

	/**
	 * <pre>
	 * 0, 1, 2 
	 * 3, 4, 5 
	 * 6, 7, 8
	 * </pre>
	 */

	public TicTacToe() {
		System.out.println("Adresse IP azafady: ");
		ip = scanner.nextLine();
		System.out.println("Apidiro ny PORT azafady : ");
		port = scanner.nextInt();
		while (port < 1 || port > 65535) {
			System.out.println("Tsy mety ny port! Mametraha hafa: ");
			port = scanner.nextInt();
		}

		makasary();

		painter = new Painter();
		painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		if (!connect()) 
		serverInitial();

		frame = new JFrame();
		frame.setTitle("Tic-Tac-Toe");
		frame.setContentPane(painter);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		thread = new Thread(this, "TicTacToe");
		thread.start();
	}

	public void run() {
		while (true) {
			tick();
			painter.repaint();

			if (!circle && !ekena) {
				mmpihainoServerSocket();
			}

		}
	}

	public void fenetre(Graphics g) {
		g.drawImage(terrain, 0, 0, null);
		if (tsyafakamiteny) {
			g.setColor(Color.RED);
			g.setFont(soratrakely);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(tsyafakamiresaka);
			g.drawString(tsyafakamiresaka, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
			return;
		}

		if (ekena) {
			for (int i = 0; i < espace.length; i++) {
				if (espace[i] != null) {
					if (espace[i].equals("X")) {
						if (circle) {
							g.drawImage(redX, (i % 3) * elanelana + 10 * (i % 3), (int) (i / 3) * elanelana + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(blueX, (i % 3) * elanelana + 10 * (i % 3), (int) (i / 3) * elanelana + 10 * (int) (i / 3), null);
						}
					} else if (espace[i].equals("O")) {
						if (circle) {
							g.drawImage(blueO, (i % 3) * elanelana + 10 * (i % 3), (int) (i / 3) * elanelana + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(redO, (i % 3) * elanelana + 10 * (i % 3), (int) (i / 3) * elanelana + 10 * (int) (i / 3), null);
						}
					}
				}
			}
			if (nandresy || resy) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(10));
				g.setColor(Color.BLACK);
				g.drawLine(firstSpot % 3 * elanelana + 10 * firstSpot % 3 + elanelana / 2, (int) (firstSpot / 3) * elanelana + 10 * (int) (firstSpot / 3) + elanelana / 2, secondSpot % 3 * elanelana + 10 * secondSpot % 3 + elanelana / 2, (int) (secondSpot / 3) * elanelana + 10 * (int) (secondSpot / 3) + elanelana / 2);

				g.setColor(Color.RED);
				g.setFont(soratrabe);
				if (nandresy) {
					int stringWidth = g2.getFontMetrics().stringWidth(victoire);
					g.drawString(victoire, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
				} else if (resy) {
					int stringWidth = g2.getFontMetrics().stringWidth(defaite);
					g.drawString(defaite, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
				}
			}
			if(naitsy) {
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.BLACK);
				g.setFont(soratrabe);
				int stringWidth = g2.getFontMetrics().stringWidth(sahala);
				g.drawString(sahala, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
			}
		} else {
			g.setColor(Color.RED);
			g.setFont(soratra);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(mampiandry);
			g.drawString(mampiandry, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
		}

	}

	public void tick() {
		if (errors >= 10) tsyafakamiteny = true;

		if (!anjaranao && !tsyafakamiteny) {
			try {
				int space = dis.readInt();
				if (circle) espace[space] = "X";
				else espace[space] = "O";
				checkResy();
				checkSahala();
				anjaranao = true;
			} catch (IOException e) {
				e.printStackTrace();
				errors++;
			}
		}
	}

	public void checkNandresy() {
		for (int i = 0; i < wins.length; i++) {
			if (circle) {
				if (espace[wins[i][0]] == "O" && espace[wins[i][1]] == "O" && espace[wins[i][2]] == "O") {
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					nandresy = true;
				}
			} else {
				if (espace[wins[i][0]] == "X" && espace[wins[i][1]] == "X" && espace[wins[i][2]] == "X") {
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					nandresy = true;
				}
			}
		}
	}

	public void checkResy() {
		for (int i = 0; i < wins.length; i++) {
			if (circle) {
				if (espace[wins[i][0]] == "X" && espace[wins[i][1]] == "X" && espace[wins[i][2]] == "X") {
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					resy = true;
				}
			} else {
				if (espace[wins[i][0]] == "O" && espace[wins[i][1]] == "O" && espace[wins[i][2]] == "O") {
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					resy = true;
				}
			}
		}
	}

	public void checkSahala() {
		for (int i = 0; i < espace.length; i++) {
			if (espace[i] == null) {
				return;
			}
		}
		naitsy = true;
	}

	public void mmpihainoServerSocket() {
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			ekena = true;
			System.out.println("Nanagataka ny hilalao ary nekena");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean connect() {
		try {
			socket = new Socket(ip, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			ekena = true;
		} catch (IOException e) {
			System.out.println("Mbola tsy mety mifandray: " + ip + ":" + port + " | Starting a server");
			return false;
		}
		System.out.println("Server mifandray");
		return true;
	}

	public void serverInitial() {
		try {
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		} catch (Exception e) {
			e.printStackTrace();
		}
		anjaranao = true;
		circle = false;
	}

	public void makasary() {
		try {
			terrain = ImageIO.read(getClass().getResourceAsStream("/terrain.png"));
			redX = ImageIO.read(getClass().getResourceAsStream("/redX.png"));
			redO = ImageIO.read(getClass().getResourceAsStream("/redO.png"));
			blueX = ImageIO.read(getClass().getResourceAsStream("/blueX.png"));
			blueO = ImageIO.read(getClass().getResourceAsStream("/blueO.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		TicTacToe ticTacToe = new TicTacToe();
	}

	public class Painter extends JPanel implements MouseListener {
		public static final long serialVersionUID = 1L;

		public Painter() {
			setFocusable(true);
			requestFocus();
			setBackground(Color.WHITE);
			addMouseListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			fenetre(g);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (ekena) {
				if (anjaranao && !tsyafakamiteny && !nandresy && !resy) {
					int x = e.getX() / elanelana;
					int y = e.getY() / elanelana;
					y *= 3;
					int position = x + y;

					if (espace[position] == null) {
						if (!circle) espace[position] = "X";
						else espace[position] = "O";
						anjaranao = false;
						repaint();
						Toolkit.getDefaultToolkit().sync();

						try {
							dos.writeInt(position);
							dos.flush();
						} catch (IOException e1) {
							errors++;
							e1.printStackTrace();
						}

						System.out.println("Nipetraka ilay sary");
						checkNandresy();
						checkSahala();

					}
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

	}

}
