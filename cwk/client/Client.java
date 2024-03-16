import java.io.*;
import java.net.*;

public class Client 
{	
	static String command = null;
	static String fName = null;

	public void Client(){

		PrintWriter socketOutput = null;
		BufferedReader socketInput = null;

		try{
			int localhost_port = 9111;
			Socket socket = new Socket( "localhost", localhost_port );

			//create output
			socketOutput = new PrintWriter(socket.getOutputStream(), true);
			//create input
			socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		}
		catch(IOException IOE){
			System.out.println(String.format("IOExpection: %s", IOE));
		}
		/*catch(UnknownHostException UHE){
			System.out.println(String.format("UnknownHostException: %s", UHE));
		}*/

		//creates request
		String request = String.format("%s", command);
		if(command == "put"){
			request += String.format(" %s", fName);
		}
		
		//send request to server
		socketOutput.println(request);


		//wait for server response
		try
        {
			String serverResponse = null;
			while((serverResponse=socketInput.readLine()) != null){
				// Echo server string.
				System.out.println( "Server: " + serverResponse );
			}
			//put close statements below
        }
        catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.exit(1);
        }

	}

	public static void main( String[] args )
	{

		if(args[0].equals("list")){
			command = args[0];
		}
		else if(args[0].equals("put")){
			command = args[0];
			fName = args[1];
		}
		else{
			System.out.println("Invalid arguments.");
		}

		Client client = new Client();
		client.Client();
	}
}