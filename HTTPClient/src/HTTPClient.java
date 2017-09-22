import static java.util.Arrays.asList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class HTTPClient {
	
	// Variable required for the execution of the command at the main level
	private static boolean hasVerbose;
	
	// List of supported commands
	private static HashMap<String, Boolean> supportedCommands = null;
	private static OptionParser parser = null;
	
	// App name
	private static final String APP_NAME = "httpc";
	
	// Possible errors
	private static final String ERR_INVALID_COMMAND = "Invalid command, type httpc -help for more info";
	private static final String ERR_INVALID_HEADER_COLON = "Invalid header, each header element should contain one and only one ':' character";
	private static final String ERR_GET_WITH_BODY = "GET method should not contain in-line data or file";
	private static final String ERR_POST_DATA_AND_FILE = "POST only allows one of the following: in-line data OR file, but not both";
	
	/**
	 * Main method, process the command entered by the user
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		// Initiate any necessary variables
		initialize();
		
		// Validate the passed arguments and execute
		HTTPRequest request = new HTTPRequest();
		if(processCommand(args, request)) {
			String response = request.execute(hasVerbose);
			System.out.println("Response is:\n" + response);
		}		
	}

	/**
	 * Initializes any required variables for the validation of the input
	 */
	public static void initialize() {
		// Possible commands
		supportedCommands = new HashMap<>();
		supportedCommands.put("help", true);
		supportedCommands.put("get", true);
		supportedCommands.put("post", true);
		supportedCommands.put("put", false);
		
		// Parser rules and definition
		parser = new OptionParser();
        parser.accepts("v", "Prints the detail of the response such as protocol, status, and headers.");
        parser.accepts("h", "key:value Associates headers to HTTP Request with the format")
        	.withRequiredArg();
        parser.accepts("d", "string Associates an inline data to the body HTTP POST request")
        	.withRequiredArg();
        parser.accepts("f", "file Associates the content of a file to the body HTTP POST")
        	.withRequiredArg();
	}
	
	/**
	 * Process the command based on the arguments. Validates the parameters and initializes the 
	 * HTTPRequest object.
	 * 
	 * @param args - arguments from the user
	 * @param request - HTTP request on which the arguments will be applied
	 * @return true if the command is ready to be executed, false otherwise
	 */
	public static boolean processCommand(String[] args, HTTPRequest request) {
		// Verify that we have sufficient args
		if(args == null || args.length < 2) {
			System.err.println(ERR_INVALID_COMMAND);
			return false;
		}
		
		// httpc command
        String command = args[0];
        if(!command.equalsIgnoreCase(APP_NAME)) {
        	System.err.println(ERR_INVALID_COMMAND);
        	return false;
        }
        
        // Command used (help, or http method)
        String httpcCommand = args[1].toLowerCase(); 
        if(supportedCommands.containsKey(httpcCommand)) {
        	if(httpcCommand.equalsIgnoreCase("help")) {
        		try {
					parser.printHelpOn(System.out);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
        		return false;
        	} else {
        		request.setMethod(httpcCommand);
        	}
        } else {
        	System.err.println(ERR_INVALID_COMMAND);
        	return false;
        }
        
        // Parse the given arguments
        OptionSet opts = parser.parse(args);
        
        // Verbose
        hasVerbose = opts.has("v");
        
        // Headers
        if(opts.has("h")) {
        	List<String> headers = (ArrayList<String>) opts.valuesOf("h");
        	for(String header : headers) {
        		String tempHeader = header;
        		if(tempHeader.length() - tempHeader.replace(".", "").length() == 1) { 
        			String[] headerKeyValue = header.split(":");
					request.addRequestHeader(headerKeyValue[0], headerKeyValue[1]);
        		} else {
        			System.err.println(ERR_INVALID_HEADER_COLON);
        			return false;
        		}
        	}
        }
        
        // In-line data
        boolean hasInLineData = opts.has("d");
        if(hasInLineData) {
        	if(httpcCommand.equalsIgnoreCase("get")) {
        		System.err.println(ERR_GET_WITH_BODY);
        		return false;
        	} else {
        		String inLineData = (String) opts.valueOf("d");
        		request.setEntityBody(inLineData);
        	}
        }
        
        // File
        if(opts.has("f")) {
        	if(httpcCommand.equalsIgnoreCase("get")) {
        		System.err.println(ERR_GET_WITH_BODY);
        		return false;
        	} else if(hasInLineData) {
        		System.err.println(ERR_POST_DATA_AND_FILE);
        		return false;
        	} else {
        		String filePath = (String) opts.valueOf("f");
        		try {
					String fileContent = readFile(filePath, StandardCharsets.UTF_8);
					request.setEntityBody(fileContent);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					return false;
				}
        	}
        }
        
        // URL (host and URI)
        String url = args[args.length-1];
        URL urlObj;
		try {
			urlObj = new URL(url);
			request.setHost(urlObj.getHost());
	        request.setRequestURI(urlObj.getPath()); 
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Given the path and the encoding of a file, this method reads the entire content of the file and decodes it into a string
	 * @param path
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encodedFileContent = Files.readAllBytes(Paths.get(path));
		return new String (encodedFileContent, encoding);
	}
}
