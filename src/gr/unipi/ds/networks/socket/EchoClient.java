package gr.unipi.ds.networks.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * source originated:
 * 
 * https://www.baeldung.com/a-guide-to-java-sockets
 * 
 * @author amarg
 *
 */
public class EchoClient {
	//~~~~~ MAIN METHOD
	public static void main(String[] args) throws Exception{
		EchoClient echo = new EchoClient();
		echo.startConnection("localhost", 9999);
	}
	//~~~~ CLASS BODY
	
	//define these resource as members, in this way we can open them in one method and close them in another
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
 
    public void startConnection(String ip, int port) throws Exception{
        //setup network
    	clientSocket = new Socket(ip, port);										  //communicate with os - setup client socket
        out = new PrintWriter(clientSocket.getOutputStream(), true);				  //socket convert outputstream to print stream to write text
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//socket convert input stream to read text
        System.out.println("connected to server @"+ip+":"+port);
        
        //operation
		Scanner scan = new Scanner(System.in);			//open the scanner resource from system.in
		while(true) {
			System.out.println("type text to send:");
			String read = scan.nextLine();				//read text terminated by new line
			String response = sendMessage(read);		//take the text and forward it to the TCP socket output stream
			System.out.println("server: "+response);	//print the response (THIS IS ALL THE APPLICATION DOES)
			
			if(read.equals("end")) {					//end is a special case, leads to gracefully closing the network operations
				System.out.println("terminating connection");
				stopConnection();						//close network
				scan.close();							//close the scanner resource
				return;									//end program
			}
		}
    }
    /**
     * nothing magic, write the string in the writer read from the bufferedreader
     * @param msg
     * @return
     * @throws Exception
     */
    public String sendMessage(String msg) throws Exception{
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }
    /**
     * simply close the network resource (input,output streams and socket itself)
     * 
     * @throws Exception
     */
    public void stopConnection() throws Exception{
        in.close();
        out.close();
        clientSocket.close();
    }
}