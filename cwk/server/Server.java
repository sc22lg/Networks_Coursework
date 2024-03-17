import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class Server 
{	
	
	public String currentDirectory = System.getProperty("user.dir");
	public String path = currentDirectory + File.separator + "serverFiles" + File.separator;

	//creates a new file and returns its location
	public boolean createNewFile(String filename){

		try {
			File file = new File(path + filename);
			if (file.createNewFile()) {
				System.out.println("File created: " + file.getName());
			} 
			else {
				System.out.println("File already exists.");
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

	public void runServer(){

		int localhost_port = 9201;
		ServerSocket serverSock = null;
		ExecutorService service = null;

		//create the log file
		createNewFile("log.txt");

		try {
			serverSock = new ServerSocket(localhost_port);
		}
		catch(IOException IOE){
			System.out.println( IOE );
			System.exit(-1);
		}

		service = Executors.newFixedThreadPool(20);

		Socket clientSocket = null;

		boolean running = true;
        while( running ){

			try{
				//wait for connection
				clientSocket = serverSock.accept();
			}
			catch (IOException IOE) {
				System.out.println( IOE );
			}

			try{
				//get clientSocket info
				InetAddress inet = clientSocket.getInetAddress();
                Date date = new Date();

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

					//send list of files to client
					for(int i = 0; i < fileList.size(); i++){
						out.println(fileList.get(i));
					}

				}
				else if(splitRequest[0].equals("put")){

					String requestedFName = splitRequest[1];

					//creates the file and then reads contents from input.
					if(createNewFile(requestedFName)){

						FileWriter fWriter = new FileWriter(path + requestedFName);

						inputRequest = in.readLine();
						while((inputRequest != null)){
							fWriter.write(inputRequest);
							fWriter.write("\n");
							inputRequest = in.readLine();
						}
						fWriter.close();
						out.println(String.format("Uploaded file '%s'", requestedFName));
					}
					else{
						out.println(String.format("Error: Cannot upload file '%s'; already exists on server", requestedFName));
					}

				}
				else{
					System.out.println("Invalid command");
				}
			
			}
			catch (IOException IOE) {
				System.out.println( IOE );
			}
		}
	}

	public static void main( String[] args )
	{
		Server server = new Server();
		server.runServer();
	}
}