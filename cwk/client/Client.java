import java.io.*;
import java.net.*;

public class Client 
{	
	String command = null;
	String fName = null;

	public void Client(){

		try{
			int localhost_port = 9111;
			Socket socket = new Socket( "localhost", localhost_port );

			//create output
			socketOutput = new PrintWriter(socket.getOutputStream(), true);
			//create input
			socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		}
		catch(IOExeption IOE){
			System.out.println("IOExpection: %s", IOE);
		}
		catch(UnknownHostException UHE){
			System.out.println("UnknownHostException: %s", UHE);
		}

		//creates request
		String request = String.format("%s", command);
		if(command == "put"){
			request += String.format(" %s", fName);
		}
		
		//send request to server
		socketOutput.println(request);

	}

	public static void main( String[] args )
	{
		if(args[0] == "list"){
			command = args[0]
		}
		else if(args[0] == "put"){
			command = args[0]
			fName = args[1];
		}
		else{
			System.out.println("Invalid arguments.");
		}

		Client client = new Client();
		client.Client();
	}
}