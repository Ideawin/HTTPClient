import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HTTPResponse {
	
	// Attributes
	private String response = "";
	private SocketChannel socket;

	/**
	 * Default constructor
	 */
	public HTTPResponse() {}
	
	/**
	 * Constructor with a socket passed as a parameter
	 * @param socket
	 */
	public HTTPResponse(SocketChannel socket) {
		this.socket = socket;
	}
	
	/**
	 * Method to query parameters of the request
	 * @param buf: the buffer containing the response
	 * @return a string representing the server's response
	 * @throws IOException
	 */
	public String queryParameters(ByteBuffer buf) throws IOException {
		response = this.getFullResponse(buf);
		return response; // must only take the bottom half
	}
	
	/**
	 * Method to get the full server's response
	 * @param buf: the buffer containing the response
	 * @return a string representing the server's response
	 * @throws IOException
	 */
	public String getFullResponse(ByteBuffer buf) throws IOException {
    	Charset utf8 = StandardCharsets.UTF_8;
    	while ((socket.read(buf) != -1)) {
		    buf.flip();
		    response += utf8.decode(buf);
		    buf.clear();
		}
    	return response;
	}
	
	
}
