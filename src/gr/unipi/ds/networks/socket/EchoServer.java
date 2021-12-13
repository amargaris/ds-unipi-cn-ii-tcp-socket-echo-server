package gr.unipi.ds.networks.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * source originated:
 * 
 * https://www.baeldung.com/a-guide-to-java-sockets
 * 
 * @author amarg
 *
 */
public class EchoServer {
	
	//~~~~~~~ MAIN METHOD
	
	public static void main(String[] args) throws Exception{
		EchoServer echo1 = new EchoServer();
		echo1.init(9999);
//		echo1.startSingleThread(); 		//this activate single threaded mode
		echo1.startMultiThreaded();		//this activates multi threaded mode (recommended)
	}
	
	//~~~~~~~ CLASS BODY
	
	private ServerSocket serverSocket;	//then only resource of the server class

	public void init(int port) throws Exception{
		serverSocket = new ServerSocket(port);			 //the server will be listening to this port for connections	
		System.out.println("server start @ port "+port);
	}
	public void startSingleThread() throws Exception{
		Socket clientSocket = serverSocket.accept();	 //the most important call: accept will return client connections
		startSession(clientSocket);						 //execute the server application protocol for the first customer to connect
	}
	public void startMultiThreaded() throws Exception{
		while(true) {									 //we do this in a while(true) loop for multiple client sessions
			Socket clientSocket = serverSocket.accept(); //the most important call: accept will return client connections
			new Thread(()->{							 //wrap the function in a new thread, in this way we implement the multi-thread
				try {
					startSession(clientSocket);			 //execute the server application protocol here
				}catch(Exception e) {
					e.printStackTrace();
				}			
			}).start();									 //start the thread
		}
	}

	public void startSession(Socket clientSocket) throws Exception{
		String clientName = toClientString(clientSocket);											 //simple utility to transform the socket into a print name
		System.out.println(clientName+" connected");
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);					 //wrap the socket:output into a print writer object
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//wrap the socket:input into a buffered reader object

		String inputLine;
		while ((inputLine = in.readLine()) != null) {				//read what the client is sending (string)
			out.println(inputLine);									//write it back to the client (seriously that's it!)
			System.out.println(clientName+" send: "+inputLine);		
			if ("end".equals(inputLine)) {							//implement the soft shutdown operation with the keyword "end"
				out.close();										//close output stream
				in.close();											//close input stream
				clientSocket.close();								//close socket resource
				System.out.println(clientName+" exited gracefully");
				break;												//break the thread loop (for this client only!)
			}
			
		}
	}
	/**
	 * simple utility to convert socket into a name using {ip}:{port} syntax
	 * 
	 * @param clientSocket
	 * @return
	 */
	public static String toClientString(Socket clientSocket) {
		return "client "+clientSocket.getInetAddress().toString()+":"+clientSocket.getPort();
	}
}