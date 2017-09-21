import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HTTPResponse {

	public HTTPResponse() {}
	
	public HTTPResponse(SocketChannel socket, ByteBuffer buf) {}
	
	public String queryParameters() {
		return "";
	}
	
	public void verbose(SocketChannel socket, ByteBuffer buf)  throws IOException {
    	Charset utf8 = StandardCharsets.UTF_8;
    	String response = "";
    	while ((socket.read(buf) != -1)) {
		    buf.flip();
		    response += utf8.decode(buf);
		    buf.clear();
		}
    	System.out.println(response);
	}
	
	
}
