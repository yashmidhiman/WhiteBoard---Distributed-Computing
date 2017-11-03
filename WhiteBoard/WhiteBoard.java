
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

class WhiteBoard {

	static int currentX, currentY, oldX, oldY, eraserOn;
	static PadDrawing drawPad = null;
	static int flag = 0;
	static String coloring = "";
	// Multicast Socket, Group Address and Port Number
	static MulticastSocket mcSocket = null;
	static InetAddress group = null;
	static int thePort;
	static String hostname;
	//buffered image for loading image icons
	static BufferedImage img;

	// Packet Receiver Thread
	public static void readThread(MulticastSocket mcSocket, InetAddress group, int thePort) {

		// new Datagram Packet
		byte[] buff = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buff, buff.length, group, thePort);

		// Packet Receive
		try {
			mcSocket.receive(dp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Extract Information
		String pkt = "";
		pkt = new String(buff, 0, dp.getLength());
		String arg[] = pkt.split(" ");
		oldX = Integer.parseInt(arg[0]);
		oldY = Integer.parseInt(arg[1]);
		currentX = Integer.parseInt(arg[2]);
		currentY = Integer.parseInt(arg[3]);
		coloring = new String(arg[4]);
		eraserOn = Integer.parseInt(arg[5]);
		// reset image
		if (eraserOn == 2) {
			drawPad.rclear();
		} else {
			// Draw the pattern "invoking drawPad.drawing"
			drawPad.rdrawing(oldX, oldY, currentX, currentY, coloring, eraserOn);
		}
		// Information Received and can be drawn
		// System.out.println("\tx1-"+oldX+"\ty1-"+oldY +
		// "\tx2-"+currentX+"\ty2-"+currentY);
	}
	
	//load image function 
	private static void loadImage(String str) {
		try {
			img = ImageIO.read(WhiteBoard.class.getResource(str));
		} catch (IOException e) { e.printStackTrace(); }
	}

	public static void main(String[] args) throws Exception {

		// Creates a frame with a title of "White Board"
		JFrame frame = new JFrame("White Board");
		// Creates a new container
		Container content = frame.getContentPane();
		// sets the layout
		content.setLayout(new BorderLayout());
		// creates a new PadDrawing, which is pretty much the paint program
		drawPad = new PadDrawing(oldX, oldY, currentX, currentY, coloring, eraserOn);
		// flag on/off in case of transfer is needed or not
		drawPad.setFlagg(flag);
		// sets the PadDrawing in the center
		content.add(drawPad, BorderLayout.CENTER);

		/**************************** Left Panel *********************************/
		// creates a JPanel
		JPanel panel = new JPanel();
		// size of the panel
		panel.setPreferredSize(new Dimension(45, 45));
		// sets the panel to the left
		content.add(panel, BorderLayout.WEST);

		// Colored image icons
		// the eraser image icon
		loadImage("images/eraser.png");
		Icon iconE = new ImageIcon(img);
		// the blue image icon
		loadImage("images/blue.png");
		Icon iconB = new ImageIcon(img);
		// hot pink image icon
		loadImage("images/magenta.png");
		Icon iconM = new ImageIcon(img);
		// red image icon
		loadImage("images/red.png");
		Icon iconR = new ImageIcon(img);
		// black image icon
		loadImage("images/black.png");
		Icon iconBl = new ImageIcon(img);
		// green image icon
		loadImage("images/green.png");
		Icon iconG = new ImageIcon(img);

		// Clear button to clear the screen.
		JButton clearButton = new JButton("Reset");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPad.clear();
			}
		});
		//set border color
        clearButton.setBorder(new LineBorder(Color.GRAY));

		// creates the eraser button and sets the eraser icon for erasing
		JButton eraserButton = new JButton(iconE);
		eraserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPad.erase();
			}

		});

		// creates the red button and sets the red icon.
		JButton redButton = new JButton(iconR);
		redButton.setBackground(Color.red);
		redButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPad.red();
			}

		});

		// similarly black button
		JButton blackButton = new JButton(iconBl);
		blackButton.setBackground(Color.black);
		blackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPad.black();
			}
		});

		// hot pink button
		JButton magentaButton = new JButton(iconM);
		magentaButton.setBackground(Color.magenta);
		magentaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPad.magenta();
			}
		});

		// blue button
		JButton blueButton = new JButton(iconB);
		blueButton.setBackground(Color.blue);
		blueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPad.blue();
			}
		});

		// green button
		JButton greenButton = new JButton(iconG);
		greenButton.setBackground(Color.green);
		greenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPad.green();
			}
		});

		// sets the sizes of the buttons
		clearButton.setPreferredSize(new Dimension(45, 20));
		eraserButton.setPreferredSize(new Dimension(20, 20));
		blackButton.setPreferredSize(new Dimension(20, 20));
		magentaButton.setPreferredSize(new Dimension(20, 20));
		redButton.setPreferredSize(new Dimension(20, 20));
		blueButton.setPreferredSize(new Dimension(20, 20));
		greenButton.setPreferredSize(new Dimension(20, 20));

		// add the buttons to the panel
		panel.add(greenButton);
		panel.add(blueButton);
		panel.add(magentaButton);
		panel.add(blackButton);
		panel.add(redButton);
		panel.add(eraserButton);
		panel.add(clearButton);
		/******************************* Left Panel done ***************************/

		/****************************** Top Panel ********************************/
		// creates another JPanel for connection
		JPanel panel2 = new JPanel();
		// set panel size
		panel2.setPreferredSize(new Dimension(45, 30));
		// sets the panel location at the top
		content.add(panel2, BorderLayout.PAGE_START);
		// create new text fields for multicast address and port number
		JLabel mcastlabel = new JLabel();
		mcastlabel.setText("<multicast-addr>");
		JTextField host = new JTextField();
		JLabel portlabel = new JLabel();
		portlabel.setText("<port-no>");
		
		JTextField port = new JTextField();
		// create connection button
		JButton ready = new JButton("connect");
		// set the size of the text field and button
		host.setPreferredSize(new Dimension(80, 20));
		port.setPreferredSize(new Dimension(50, 20));
		ready.setPreferredSize(new Dimension(90, 20));
		// set default multicast address port number and tool tip text of button
		host.setText("230.1.1.1");
		port.setText("5001");
		ready.setToolTipText("click to Initiate");
		// add components to the panel text fields and button
		panel2.add(mcastlabel);
		panel2.add(host);
		panel2.add(portlabel);
		panel2.add(port);
		panel2.add(ready);

		// sets the size of the frame
		frame.setSize(700, 500);
		// makes it so you can close
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// makes it visible
		frame.setVisible(true);
		/******************************* Top Panel done ***************************/

		// adds and action listener to the button
		ready.addActionListener(new ActionListener() {
			// invoke on mouse click
			public void actionPerformed(ActionEvent e) {

				// leave from any old connection. use other Multicast group
				if (mcSocket != null) {
					try {
						mcSocket.leaveGroup(group);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				// get values from the text boxes
				hostname = new String(host.getText());
				thePort = Integer.parseInt(port.getText());

				System.out.println("\nPlease wait for Connection...");
				// creating a new connection
				try {
					// determine the IP address of the host, given by "hostname"
					group = InetAddress.getByName(hostname);

					// create the multicast socket with the specified port number "thePort"
					mcSocket = new MulticastSocket(thePort);

					System.out.println("  <System Name>/<IP Address>:\t" + InetAddress.getLocalHost());
					System.out.println("  <Multicast address>:\t\t" + group.getHostAddress() + "\t<port>:  " + thePort);

					// Set the default time-to-live for multicast packets sent out on this
					// MulticastSocket i.e. to control scope
					//mcSocket.setTimeToLive(3);
					// join the multicast group
					mcSocket.joinGroup(group);

					// Since the data is sent by another class "PadDrawing" few things are needed to
					// pass and set for proper transmission
					// set flag 1 i.e. connection is established and data can be transferred
					flag = 1;
					drawPad.setFlagg(flag);
					// set the group InetAddress
					drawPad.setGroup(group);
					// set the port number
					drawPad.setPort(thePort);
					// set the multicast Socket
					drawPad.setMCSocket(mcSocket);
					drawPad.black();
					// call the receiving Thread for receiving data elements
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								// always accept the packets
								while (true) {
									// packet receive thread
									readThread(mcSocket, group, thePort);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					// start Thread
					try {
						t.start();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

	}
}

/************************* For PadDrawing *******************************/
class PadDrawing extends JComponent {

	// A SerialVersionUID identifies the unique original class version for writing
	// streams and from which it can read
	private static final long serialVersionUID = 1L;

	// Multicast Socket, Group Address, Port Number and flag
	public MulticastSocket mcSocket;
	public InetAddress group;
	public int thePort;
	public int flag = 0;

	// copying into current class instance variable
	public void setFlagg(int flag) {
		this.flag = flag;
	}

	public void setGroup(InetAddress group) {
		this.group = group;
	}

	public void setPort(int thePort) {
		this.thePort = thePort;
	}

	public void setMCSocket(MulticastSocket mc) {
		mcSocket = mc;
	}

	// x,y coordinate used for drawing
	int currentX, currentY, oldX = 0, oldY = 0, eraser = 0;
	String coloring;
	// eraser On or Off mode. 0 means Off and 1 means On
	int eraserOn = 0;

	// Abstract class Image, represents graphical image
	Image image;
	
	//buffered image for loading "eraser image"
	static BufferedImage img;

	// Graphics 2D class for sophisticated control. color management, 2d shapes
	Graphics2D graphics2D;

	//loadImage function
	private static void loadImage(String str) {
		try {
			img = ImageIO.read(WhiteBoard.class.getResource(str));
		} catch (IOException e) { e.printStackTrace(); }
	}

	// for remote drawing lines and erasing
	public final void rdrawing(int oldX, int oldY, int currentX, int currentY, String coloring, int eraser) {
		if (graphics2D != null)
			switch (coloring) {
			case "green":
				graphics2D.setPaint(Color.green);
				break;
			case "blue":
				graphics2D.setPaint(Color.blue);
				break;
			case "black":
				graphics2D.setPaint(Color.black);
				break;
			case "red":
				graphics2D.setPaint(Color.red);
				break;
			case "magenta":
				graphics2D.setPaint(Color.magenta);
				break;
			case "white":
				graphics2D.setPaint(Color.white);
				break;
			default:
				break;
			}
		if (eraser == 1) {
			// draw white rectangle
			graphics2D.fillRect(oldX, oldY, 30, 30);
		} else {
			// draw line x1,y1,x2,y2
			graphics2D.drawLine(oldX, oldY, currentX, currentY);
		}
		// repaint the component
		repaint();
	}

	// Now for the constructors
	public PadDrawing(int oX, int oY, int cX, int cY, String ccolor, int eraser) throws Exception {

		// copying the coordinates into current class instance variable
		this.currentX = cX;
		this.currentY = cY;
		this.oldX = oX;
		this.oldY = oY;
		// using single buffer
		setDoubleBuffered(false);

		// adds mouse action listener
		addMouseListener(new MouseAdapter() {
			// on mouse click event
			public void mousePressed(MouseEvent e) {
				// if the mouse is pressed it sets the oldX & oldY coordinates
				oldX = e.getX();
				oldY = e.getY();
				// if eraser mode is on, draw white filled rectangle
				if (eraserOn == 1) {
					graphics2D.fillRect(oldX, oldY, 30, 30);
				}
				repaint();
				// if there is connection send the packet
				if (flag == 1) {
					try {
						// string containing the packet
						String pkt = oldX + " " + oldY + " " + oldX + " " + oldY + " " + coloring + " "
								+ eraserOn;
						// new datagram packet
						DatagramPacket dp = new DatagramPacket(pkt.getBytes(), pkt.length(), group, thePort);
						// packet send
						mcSocket.send(dp);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		// adds mouse action listener
		addMouseMotionListener(new MouseMotionAdapter() {
			// on mouse drag event
			public void mouseDragged(MouseEvent e) {
				// if the mouse is dragged it sets currentX & currentY coordinates
				currentX = e.getX();
				currentY = e.getY();
				// draw function call
				if (graphics2D != null)
					if (eraserOn == 1) {
						// draw white rectangle
						graphics2D.fillRect(oldX, oldY, 30, 30);
					} else {
						// draw line x1,y1,x2,y2
						graphics2D.drawLine(oldX, oldY, currentX, currentY);
					}
				// repaint the component
				repaint();
				// if there is connection send the packet
				if (flag == 1) {
					try {
						// string containing the packet
						String pkt = oldX + " " + oldY + " " + currentX + " " + currentY + " " + coloring + " "
								+ eraserOn;
						// new datagram packet
						DatagramPacket dp = new DatagramPacket(pkt.getBytes(), pkt.length(), group, thePort);
						// packet send
						mcSocket.send(dp);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				// and sets the oldX & oldY coordinates
				oldX = currentX;
				oldY = currentY;
			}
		});
	}

	// paint component
	public void paintComponent(Graphics g) {
		// initializing the paint board
		if (image == null) {
			// creates a drawable image
			image = createImage(getSize().width - 20, getSize().height - 20);
			graphics2D = (Graphics2D) image.getGraphics();
			// rendering quality
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// clears the paint board
			clear();
		}
		// finally image
		g.drawImage(image, 0, 0, null);
	}

	// eraser
	public void erase() {
		// choose white color
		graphics2D.setPaint(Color.white);
		// color
		coloring = "white";
		// eraser mode on
		eraserOn = 1;
		if (flag == 1) {
			try {
				// string containing the packet
				String pkt = getSize().width + " " + getSize().height + " " + getSize().width + " " + getSize().height + " " + coloring + " " + 1;
				// new datagram packet
				DatagramPacket dp = new DatagramPacket(pkt.getBytes(), pkt.length(), group, thePort);
				// packet send
				mcSocket.send(dp);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		//to load Image in "img"
		loadImage("/images/eraser.png");
		// set custom cursor
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(img).getImage(),
				new Point(0, 0), "e"));
		repaint();
	}

	// clear
	public void rclear() {
		// turn off eraser mode
		eraserOn = 0;
		// set color to white
		graphics2D.setPaint(Color.white);
		// fill white rectangle
		graphics2D.fillRect(0, 0, getSize().width - 20, getSize().height - 20);
		// set default color
		graphics2D.setPaint(Color.black);
		repaint();
	}

	// clear
	public void clear() {
		// turn off eraser mode
		eraserOn = 0;
		// set color to white
		graphics2D.setPaint(Color.white);
		// color
		coloring = "white";
		// fill white rectangle
		graphics2D.fillRect(0, 0, getSize().width - 20, getSize().height - 20);
		// set default color
		graphics2D.setPaint(Color.black);
		//color
		coloring = "black";
		// set default cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if (flag == 1) {
			try {
				// string containing the packet
				String pkt = oldX + " " + oldY + " " + currentX + " " + currentY + " " + coloring + " " + 2;
				// new datagram packet
				DatagramPacket dp = new DatagramPacket(pkt.getBytes(), pkt.length(), group, thePort);
				// packet send
				mcSocket.send(dp);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		repaint();
	}

	public void red() {
		// turn off eraser mode
		eraserOn = 0;
		// set color to red
		graphics2D.setPaint(Color.red);
		// color
		coloring = "red";
		// set default cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		repaint();
	}

	public void black() {
		// turn off eraser mode
		eraserOn = 0;
		// set color to black
		graphics2D.setPaint(Color.black);
		// color
		coloring = "black";
		// set default cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		repaint();
	}

	public void magenta() {
		// turn off eraser mode
		eraserOn = 0;
		// set color to magenta
		graphics2D.setPaint(Color.magenta);
		// color
		coloring = "magenta";
		// set default cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		repaint();
	}

	public void blue() {
		// turn off eraser mode
		eraserOn = 0;
		// set color to blue
		graphics2D.setPaint(Color.blue);
		// color
		coloring = "blue";
		// set default cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		repaint();
	}

	public void green() {
		// turn off eraser mode
		eraserOn = 0;
		// set color to green
		graphics2D.setPaint(Color.green);
		// color
		coloring = "green";
		// set default cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		repaint();
	}

}
