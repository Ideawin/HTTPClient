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
	private HashMap<String,String> entityHeader;
	private String entityBody;
	private String request = "GET /get?course=networking&assignment=1 HTTP/1.0\r\nHost: httpbin.org\r\n\r\n";
	//request = "GET /status/418 HTTP/1.0\r\nHost: httpbin.org\r\n\r\n";
	
	private static final String RN = "\r\n";
	
	// Default constructor
	public HTTPRequest() {}
	
	// Constructor
	public HTTPRequest(String host, String method) {
		this.host = host;
		this.method = method;
	}
	
	// Method to execute a request, by opening a socket and write the request into the buffer.
	// The request is sent to the server, which will send back a response.
	public String execute(Boolean verbose) throws IOException {
		SocketAddress endpoint = new InetSocketAddress(host, PORT);
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
            	return response.getFullResponse(buf);
            }
            else
            	return response.queryParameters(buf);
            
        } catch (IOException e) {
        	System.out.println("Exception in execute(Boolean)");
        }
        return "";
	}
	
	// Method to combine all headers and entity body together in one single String (request)
	public void createRequest() {
	}
	
	// Method to add a request header into the HashMap requestHeader
	public void addRequestHeader(String key, String value) {
		requestHeader.put(key, value);
	}
	
	// Method to add an entity header into the HashMap entityHeader
	public void addEntityHeader(String key, String value) {
		entityHeader.put(key, value);
	}
	
	// Method to set the entity body
	public void setEntityBody(String body)
	{
		this.entityBody = body;
	}
}
