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
            	
            } else {
            	System.out.println("Error in httpc command.");
            }
    	}
        
        // For testing purposes only
        System.out.println("Verbose:"  + opts.has("v"));
        System.out.println("Header:" + opts.has("h"));
        System.out.println("Headers:" + opts.valuesOf("h").size() + " " + opts.valuesOf("h"));
        System.out.println("In-Line Data:" + opts.has("d"));
        System.out.println("File:" + opts.has("f"));
        parser.printHelpOn(System.out);
		
		HTTPRequest test = new HTTPRequest("httpbin.org", "GET");
		String response = test.execute(true);
		System.out.println(response);
	}

//	public static boolean validate(String httpcCommand, boolean hasHeaders, List<String> headers, boolean hasInLineData, boolean hasFile, String url) {
//		
//		
//		// Validate the httpc command
//		if(httpcCommand == null) {
//			return false;
//		}
//		HashMap<String,Boolean> httpcCommands = new HashMap<String,Boolean>();
// 		httpcCommands.put("get", true);
// 		httpcCommands.put("post", true);
// 		httpcCommands.put("put", false);
//        if(!httpcCommands.containsKey(httpcCommand) || !httpcCommands.get(httpcCommand)) {
//        	System.out.println("Invalid httpc command, type httpc -help for full list of commands.");
//        	return false;
//        }
//        
//        // If it is a get request, no in-line data or file should be provided
//        if(httpcCommand == )
//	}
}
