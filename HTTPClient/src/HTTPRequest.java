import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HTTPRequest {
	
	// Attributes
	private String host;
	private static final int PORT = 80;
	private String method;
	private String requestURI;
	private HashMap<String,String> requestHeader;
	private String entityBody;
	private String request;
	private String outputFilename;
	
	/**
	 * Default constructor
	 */
	public HTTPRequest() {
		requestHeader = new HashMap<String,String>();
		entityBody = "";
		request = "";
	}
	
	/**
	 * Constructor
	 * @param host: The host address
	 * @param method: The request method (GET or POST)
	 */
	public HTTPRequest(String host, String method) {
		requestHeader = new HashMap<String,String>();
		this.host = host;
		this.method = method;
		entityBody = "";
		request = "";
	}
	
	/**
	 * Method to execute a request, by opening a socket and write the request into the buffer.
	 * The request is sent to the server, which will send back a response.
	 * @param verbose: true if verbose, else will only get the parameters
	 * @return a string representing the server's response
	 * @throws IOException
	 */
	public String execute(Boolean verbose) throws IOException {
		SocketAddress endpoint = new InetSocketAddress(host, PORT);
		String responseString = "";
        try (SocketChannel socket = SocketChannel.open()) {
            socket.connect(endpoint);
            createRequest();
            System.out.println("Sending request...");
            Charset utf8 = StandardCharsets.UTF_8;
            ByteBuffer buf = utf8.encode(request);
            socket.write(buf);
            buf.clear();

            // Get the response from server
            HTTPResponse response = new HTTPResponse(socket);
            if (verbose)
            {
            	responseString = response.getFullResponse(buf);
            }
            else {
            	responseString = response.queryParameters(buf);
            }
        } catch (IOException e) {
        	System.out.println("Exception in execute(Boolean)");
        }
        
        // Output file if any
        if(this.outputFilename != null && this.outputFilename.length() > 0) {
        	String responseBody = responseString;
        	if(verbose) {
        		String[] responseArray = responseString.split("\r\n");
        		responseBody = responseArray[responseArray.length - 1];
        	} 
        	BufferedWriter outputFileWriter = new BufferedWriter(new FileWriter(this.outputFilename));
			outputFileWriter.write(responseBody);
			outputFileWriter.close();
			System.out.println("Response written to output file successfully.");
        }
        
        return responseString;
	}
	
	/**
	 * Method to combine all headers and entity body together in one single String (request)
	 */
	public void createRequest() {
		request = method + " " + requestURI + " HTTP/1.0\r\nHost: " + host + "\r\n";
		if (!requestHeader.isEmpty()) {
			for (String key : requestHeader.keySet()) {
				request += key + ": " + requestHeader.get(key) + "\r\n";
			}
		}
		if (!entityBody.isEmpty()) {
			request += "\r\n" + entityBody + "\r\n\r\n";
		}
		else
			request += "\r\n";
		System.out.println(request);
	}
	
	/**
	 * Method to add a request header into the HashMap requestHeader
	 * @param key: key of the header
	 * @param value: value of the header
	 */
	public void addRequestHeader(String key, String value) {
		requestHeader.put(key, value);
	}
	
	/**
	 * Method to set the entity body
	 * @param body: the entity body of the request
	 */
	public void setEntityBody(String body)
	{
		this.entityBody = body;
		String contentLength = Integer.toString(entityBody.length());
		requestHeader.put("Content-Length", contentLength); // add a new header for the Content-Length
	}
	
	/**
	 * Method to set the host
	 * @param host: host address
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Method to set the request method
	 * @param method: the request method (GET/POST)
	 */
	public void setMethod(String method) {
		this.method = method.toUpperCase();
	}
	
	/**
	 * Method to set the request URI
	 * @param requestURI: the request URI
	 */
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	
	/**
	 * Method to set the output filename
	 * @param outputFilename
	 */
	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}
}
