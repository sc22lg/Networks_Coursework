import java.io.*;
import java.net.*;

public class Client 
{	
	static String command = null;
	static String fName = null;

	public static boolean doesFileExist(String filename) {
        File file = new File(filename);
        return file.exists();
    }

	public void runClient(){

		PrintWriter socketOutput = null;
		BufferedReader socketInput = null;

		try{
			int localhost_port = 9201;
			Socket socket = new Socket( "localhost", localhost_port );

			//create output
			socketOutput = new PrintWriter(socket.getOutputStream(), true);
			//create input
			socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		}
		catch(IOException IOE){
			System.out.println(String.format("IOExpection: %s", IOE));
		}

		//creates request
		String request = String.format("%s", command);
		if(command.equals("put")){
			//test if file exists
			if(doesFileExist(fName)){
				request += String.format(" %s", fName);
			}
			else{
				System.out.println(String.format("Error: Cannot open local file '%s' for reading.", fName));
				return;
			}
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

			}
			catch(IOException IOE){
				System.out.println(String.format("Error: Cannot open local file '%s' for reading", fName));
			}

		}

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
			return;
		}

		Client client = new Client();
		client.runClient();
	}
}