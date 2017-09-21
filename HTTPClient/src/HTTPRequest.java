import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HTTPRequest {
	
	private String host;
	private static final int PORT = 80;
	private String method;
	private String requestURI;
	HashMap<String,String> requestHeader;
	private String requestMessage;
	
	private static final String RN = "\r\n";
	
	public HTTPRequest() {}
	
	public HTTPRequest(String host, String method) {
		this.host = host;
		this.method = method;
	}
	
	public void execute() throws IOException {
		SocketAddress endpoint = new InetSocketAddress(host, PORT);
        try (SocketChannel socket = SocketChannel.open()) {
            socket.connect(endpoint);
            System.out.println("Sending request...");
            Charset utf8 = StandardCharsets.UTF_8;
            //line = "GET /status/418 HTTP/1.0\r\nHost: httpbin.org\r\n\r\n";
            String line = "GET /get?course=networking&assignment=1 HTTP/1.0\r\nHost: httpbin.org\r\n\r\n";
            ByteBuffer buf = utf8.encode(line);
            socket.write(buf);
            buf.clear();

            // Receive all what we have sent
            HTTPResponse response = new HTTPResponse();
            response.verbose(socket,buf);
        } catch (IOException e) {
        }
	}
	
}
