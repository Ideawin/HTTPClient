import java.io.IOException;

public class MultithreadedHTTPClient {

	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main (String[] args)  {
		
		int numberOfThreads = 4;
		if(args.length > 0) {
			try {
				numberOfThreads = Integer.parseInt(args[0]);
			} catch(Exception e) {
				System.err.println("Argument is not an integer.");
				System.exit(1);
			}
		}
		
		String[] read = {"get", "-v", "http://localhost:8080/test.txt"};
		String[] write = {"post", "-v", "-d", "\"allloasdafdddddddddddddddddddddddddddddddasda asd asd aasdas asd asd ad adasd asd as dad a doooo\"", "http://localhost:8080/test.txt"};
		
		HTTPClient.initialize();
		for(int i=0; i<numberOfThreads; i++) {
			new HTTPClient(write).start();
			new HTTPClient(read).start();
		}
	}
}
