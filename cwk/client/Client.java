import java.io.*;
import java.net.*;

public class Client 
{	
	static String command = null;
	static String fName = null;
	static String endofProtocol = "<>?!~~~///ENDOFPROTOCOL///~~~!?<>:)"; //string at the end of every client-server message

	public static boolean doesFileExist(String filename) {
        File file = new File(filename);
        return file.exists();
    }

	public void closeClient(Socket socket){
		try{
			socket.close();
			System.exit(0);
		}
		catch(IOException IOE){
			System.exit(0);
		}
	}

	public void runClient(){

		PrintWriter socketOutput = null;
		BufferedReader socketInput = null;

		//creates request
		String request = String.format("%s", command);
		if(command.equals("put")){
			//test if file exists
			if(doesFileExist(fName)){
				request += String.format(" %s", fName);
			}
			else{
				System.out.println(String.format("Error: Cannot open local file '%s' for reading.", fName));
				System.exit(0);
			}
		}

		//connect to server
		Socket socket = null;
		try{
			int localhost_port = 9201;
			socket = new Socket( "localhost", localhost_port );

			//create output
			socketOutput = new PrintWriter(socket.getOutputStream(), true);
			//create input
			socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		}
		catch(IOException IOE){
			System.out.println(String.format("IOExpection: %s", IOE));
			closeClient(socket);
		}


		//send request to server
		socketOutput.println(request);

		//writes the file to the socket output
		if(command.equals("put")){
			
			try(BufferedReader reader = new BufferedReader(new FileReader(fName))){

				String line;
				while ((line = reader.readLine()) != null) {
					// Write each line read from input file to the PrintWriter 'out'
					socketOutput.println(line);
            	}
				socketOutput.println(endofProtocol);

			}
			catch(IOException IOE){
				System.out.println(String.format("Error: Cannot open local file '%s' for reading", fName));
				closeClient(socket);
			}

		}

		//wait for server responses
		try
		{
			String serverResponse = null;
			if(command.equals("list")){//expects number of files from server first
				serverResponse=socketInput.readLine();
				System.out.println("Listing " + serverResponse + " file(s)");
			}
			//gets rest of file names
			while((serverResponse=socketInput.readLine()) != null){
				// Check if end of mesage
				if(serverResponse.equals(endofProtocol)){closeClient(socket); break;}
				// Echo server string.
				System.out.println( serverResponse );
			}

			//put close statements below
		}
		catch (IOException e) {
			System.err.println("I/O exception during execution\n");
			closeClient(socket);
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
			return;
		}

		Client client = new Client();
		client.runClient();
	}
}