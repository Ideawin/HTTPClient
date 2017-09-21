import static java.util.Arrays.asList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class HTTPClient {
	
	public static void main(String[] args) throws IOException {
		
		// Verify that the args
		if(args == null || args.length < 2) {
			System.out.println("Invalid command");
		}
		
		// Validate httpc command
        String command = args[0];
        if(!command.equalsIgnoreCase("httpc")) {
        	System.out.println("Invalid command, the command should start with httpc.");
        }
        
        // Validate the HTTP method used
        // Possible HTTP methods
        String httpcCommand = args[1].toLowerCase();
		
		// Create an option parser to validate the command entry by the user
		OptionParser parser = new OptionParser();
        parser.accepts("v", "Prints the detail of the response such as protocol, status, and headers.");
        parser.accepts("h", "key:value Associates headers to HTTP Request with the format")
        	.withRequiredArg();
        parser.accepts("d", "string Associates an inline data to the body HTTP POST request");
        parser.accepts("f", "file Associates the content of a file to the body HTTP POST");
        
        // Parse the given arguments
        OptionSet opts = parser.parse(args);
        
        // Obtain the values of each options
        boolean hasVerbose = opts.has("v");
        boolean hasHeaders = opts.has("h");
        List<String> headers = null;
        if(hasHeaders) {
        	headers = (ArrayList<String>) opts.valuesOf("h");
        }
        boolean hasInLineData = opts.has("d");
        boolean hasFile = opts.has("f");
        
        String url = args[args.length-1];
        
        // Validate the entry
        if(httpcCommand.equals("help")) {
    		parser.printHelpOn(System.out);
    	} else {
    		if(validate(httpcCommand, hasHeaders, headers, hasInLineData, hasFile, url)) {
    			// TODO: parse the URL and other parameters into the HTTPRequest object
    			HTTPRequest request = new HTTPRequest("httpbin.org", "GET");
    			String response = test.execute(true);
    			System.out.println("Response is:\n" + response);
            } else {
            	System.out.println("Error in httpc command.");
            }
    	}
	}

	public static boolean validate(String httpcCommand, boolean hasHeaders, List<String> headers, boolean hasInLineData, boolean hasFile, String url) {
		
		// Validate the httpc command
		if(httpcCommand == null) {
			return false;
		}
		HashMap<String,Boolean> httpcCommands = new HashMap<String,Boolean>();
 		httpcCommands.put("get", true);
 		httpcCommands.put("post", true);
 		httpcCommands.put("put", false);
        if(!httpcCommands.containsKey(httpcCommand) || !httpcCommands.get(httpcCommand)) {
        	System.out.println("Invalid httpc command, type httpc -help for full list of commands.");
        	return false;
        }
        
        // Validate get and post
        if(httpcCommand.equals("get") && (hasInLineData || hasFile)) {
        	System.out.println("GET method should not contain in-line data or file");
        	return false;
        } else if (httpcCommand.equals("post") && hasInLineData && hasFile) {
        	System.out.println("POST only allows one of the following: in-line data OR file, but not both");
        }
        
        return true;
	}
}
