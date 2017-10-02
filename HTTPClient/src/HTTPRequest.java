import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
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
	private String url;
	
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
	 * @param numberOfRedirect: number of maximum times to redirect
	 * @return a string representing the server's response
	 * @throws IOException
	 */
	public String execute(Boolean verbose, int numberOfRedirect) throws IOException {
		// Open new socket
		SocketAddress endpoint = new InetSocketAddress(host, PORT);
		String responseString = "";
        try (SocketChannel socket = SocketChannel.open()) {
        	// Connect to socket
            socket.connect(endpoint);
            
            // Create a request, adding all request headers and entity body if applicable
            createRequest();
            
            // Write request to the socket using a buffer
            System.out.println("Sending request...");
            Charset utf8 = StandardCharsets.UTF_8;
            ByteBuffer buf = utf8.encode(request);
            socket.write(buf);
            
            // Clear the buffer
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
            
            // Check status line
            if(!response.getURL().isEmpty() && numberOfRedirect > 0) {
            	// Recursive case
            	this.setUrl(response.getURL());
            	this.parseUrl();
            	// Execute again
        		return execute(verbose, numberOfRedirect - 1);
            } else {
            	// Base case
            	// Output file if any
                if(this.outputFilename != null && this.outputFilename.length() > 0) {
                	String responseBody = responseString;
                	// If verbose is true
                	if(verbose) {
                		String[] responseArray = responseString.split("\r\n");
                		// Get the response body so it can be output to a file
                		responseBody = responseArray[responseArray.length - 1];
                	} 
                	BufferedWriter outputFileWriter = new BufferedWriter(new FileWriter(this.outputFilename));
        			outputFileWriter.write(responseBody);
        			outputFileWriter.close();
        			System.out.println("Response written to output file successfully.");
                }
                return responseString;
            }
        } catch (IOException e) {
        	System.out.println("Exception in execute(Boolean)");
        }
        return "";
	}
	
	/**
	 * Method to combine all headers and entity body together in one single String (request)
	 */
	public void createRequest() {
		// first line is the request line
		request = method + " " + requestURI + " HTTP/1.0\r\n";
		// next few lines are the header lines (including Host)
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
	
	/**
	 * Method to set the url
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Method to parse the url
	 */
	public void parseUrl() {
		if(this.url.charAt(0) == '/') {
			setRequestURI(url); 
		} else {
			URL urlObj;
			 try {
				 urlObj = new URL(url);
				 setHost(urlObj.getHost());
				 addRequestHeader("Host", urlObj.getHost());
				 setRequestURI(urlObj.getPath()); 
			 } catch (MalformedURLException e) {
				 System.err.println(e.getMessage());
			 }
		}
	}
}
