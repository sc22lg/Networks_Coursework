import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.*;

public class Server 
{	
	
	public String currentDirectory = System.getProperty("user.dir");
	public String path = currentDirectory + File.separator + "serverFiles" + File.separator;
	static String endofProtocol = "<>?!~~~///ENDOFPROTOCOL///~~~!?<>:)"; //string at the end of every client-server message

	public void updateLog(InetAddress inet, String request){
		
		String gap = " | ";

		//get date and time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy | HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dateTimeString = now.format(formatter);

		//get client IP
		String clientIPAddress = inet.getHostAddress();

		//open the file writer, giving to true to indicate the file already exists
		try{FileWriter fWriter = new FileWriter("log.txt", true);

			fWriter.append(dateTimeString + gap + clientIPAddress + gap + request + "\n");
			fWriter.close();
		}
		catch(IOException e){

		}
	}

	//creates a new file and returns its location
	public boolean createNewFile(String filename){

		try {
			File file = new File(path + filename);
			if (file.createNewFile()) {
				//System.out.println("File created: " + file.getName());
			} 
			else {
				//System.out.println("File already exists.");
				return false;
			}

		} 
		catch (IOException e) {
			//do nothing ofc it cant find the file it doesnt exist yet
			System.out.println("An error occurred.");
            e.printStackTrace();
			return false;
		}
		return true;
	}

	private static List<String> listFilesInDirectory(String directoryPath) {
        List<String> fileList = new ArrayList<>();
        File directory = new File(directoryPath);

        // Check if the provided path is a directory
        if (directory.isDirectory()) {
            // Get list of files in the directory
            File[] files = directory.listFiles();

            // Add file names to the list
            if (files != null) {
                for (File file : files) {
                    fileList.add(file.getName());
                }
            }
        } else {
            System.out.println("Provided path is not a directory.");
        }

        return fileList;
    }

	public void handleClient(Socket clientSocket){

		try{
			//get clientSocket info
			InetAddress inet = clientSocket.getInetAddress();

			//open reader/writer for client request
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			//read in request
			String inputRequest = in.readLine();
			//separate request by whitespace
			String[] splitRequest = inputRequest.split("\\s+");

			if(splitRequest[0].equals("list")){
				
				//get list of files
				String directoryPath = "serverFiles";
				List<String> fileList = listFilesInDirectory(directoryPath);

				//send number of files to client
				out.println(String.format("%d", fileList.size()));
				//send list of files to client
				for(int i = 0; i < fileList.size(); i++){
					out.println(fileList.get(i));
				}
				out.println(endofProtocol);
				updateLog(inet, splitRequest[0]);
			}
			else if(splitRequest[0].equals("put")){

				String requestedFName = splitRequest[1];

				//creates the file and then reads contents from input.
				if(createNewFile(requestedFName)){

					updateLog(inet, splitRequest[0]);
					//send confirmation to client
					out.println(String.format("Uploaded file '%s'", requestedFName));

					FileWriter fWriter = new FileWriter(path + requestedFName);

					inputRequest = in.readLine();
					while(!inputRequest.equals(endofProtocol)){
						
						fWriter.write(inputRequest); //write line to file
						inputRequest = in.readLine(); //get next line
						if(!inputRequest.equals(endofProtocol)){ //if the line isnt the end then write a newline
							fWriter.write("\n");
						}
					}
					out.println(endofProtocol);
					fWriter.close();

				}
				else{
					updateLog(inet, splitRequest[0]);
					out.println(String.format("Error: Cannot upload file '%s'; already exists on server", requestedFName));
					out.println(endofProtocol);
				}

			}
			else{
				System.out.println("Invalid command");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main( String[] args )
	{
		Server server = new Server();

		//initial setup
		int localhost_port = 9201;
		ServerSocket serverSock = null;

		//create the log file
        File logFile = new File("log.txt");
        try{
            logFile.createNewFile();
        }
        catch(IOException IOE){System.exit(-1);}


		try {
			serverSock = new ServerSocket(localhost_port);
		}
		catch(IOException IOE){
			System.out.println( IOE );
			System.exit(-1);
		}

		//create threads
		ExecutorService service = Executors.newFixedThreadPool(20);

		boolean running = true;
        while( running ){

			try{
				//wait for connection
				Socket clientSocket = serverSock.accept();

				service.execute(() -> {
					server.handleClient(clientSocket);
				});
			}
			catch (IOException IOE) {
				System.out.println( IOE );
			}


		}
	}
}