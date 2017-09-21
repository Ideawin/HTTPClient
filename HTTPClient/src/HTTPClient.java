import static java.util.Arrays.asList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class HTTPClient {
	
	public static void main(String[] args) throws IOException {
		HTTPRequest test = new HTTPRequest("httpbin.org", "GET");
		String response = test.execute(true);
		System.out.println("Response is:\n" + response);
	}

}
