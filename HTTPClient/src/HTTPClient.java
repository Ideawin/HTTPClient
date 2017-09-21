import static java.util.Arrays.asList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class HTTPClient {
	
	public void executeRequest() {
		
	}
	
	public static void main(String[] args) throws IOException {
		HTTPRequest test = new HTTPRequest("httpbin.org", "GET");
		test.execute();
	}

}
