
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
 * A chat server that delivers public and private messages
 */
public class MultiThreadServer extends JFrame {
	
	private JTextField userText;
	public JTextArea chatWindow;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	public String username;
	private boolean connected;
	
	// Socket is the connection between computers -> server and client
	
	// The server socket
	private ServerSocket serverSocket = null;
	// The client socket
	private Socket clientSocket = null;
	
	// This chat server can accept up to maxClientsCount clients' connections.
	private int maxClientsCount = 10;
	private clientThread[] threads = new clientThread[maxClientsCount];
	
	private static int port = 6789;
	
	
	
	
	public static void main(String args[]) {
		MultiThreadServer m = new MultiThreadServer();
	}
	
	// constructor
	public MultiThreadServer() {
		// Title
		super("Server Instant Messaging");
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				// Send message once enter key is pressed
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		
		// Add to screen
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(500,500);
		setVisible(true);
		
		
	    /*
	     * Open a server socket on the port number (default 6789)
	     */
		try {
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e){
			e.getStackTrace();
		}
		
		/*
		 * Create a client socket for each connection
		 * and pass it through a new client thread.
		 */
		while(true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for(i = 0; i < maxClientsCount; i++) {
					if(threads[i] == null) {
						(threads[i] = new clientThread(clientSocket, threads)).start();
						break;
					}
				}
				if(i == maxClientsCount) {
					ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
					os.writeObject("Server too busy. Try again later.");
					os.close();
					clientSocket.close();
				}
			}
			catch (IOException e) {
				e.getStackTrace();
			}
		}
	}
	
	// Send a message to client
	private void sendMessage(String message) {
		try {
			
			// Create an object and send it to the output stream
			os.writeObject("SERVER - " + message);
			os.flush();
			
			// Now that the message is sent, you want it to display on the screen
			showMessage("\nSERVER - " + message);
		}
		catch(IOException ioexception) {
			chatWindow.append("\n ERROR: Message cannot be sent.");
		}
	}
	
	// Updates chat window
	private void showMessage(final String text) {
		// Only update a part of the GUI -> a thread
		SwingUtilities.invokeLater(
			// Create a new thread
			new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}
		);
	}
}