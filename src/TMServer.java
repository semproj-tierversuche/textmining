/*
 *  Main class which runs the Server and starts new threads for every received batch
 */

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TMServer {

    //Config Vars
    private static String HOSTNAME;
    private static int PORT;
    //HTML Vars
    private static final int BACKLOG = 1;
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    //HTML status vars
    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    /**
     * Gets the configs from the Config file
     * @param configDir the dir and filename where to find the Config
     */
    private static void getProperties(String configDir){
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream(configDir);

            // load a properties file
            prop.load(input);

            // getting properties
            PORT = Integer.decode(prop.getProperty("txtMining_port"));
            HOSTNAME = prop.getProperty("txtMining_host");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String decodeUrlComponent(final String urlComponent) {
        try {
            return URLDecoder.decode(urlComponent, CHARSET.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex);
        }
    }

    private static Map<String, List<String>> getRequestParameters(final URI requestUri) {
        final Map<String, List<String>> requestParameters = new LinkedHashMap<>();
        final String requestQuery = requestUri.getRawQuery();
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                final String requestParameterName = decodeUrlComponent(requestParameter[0]);
                requestParameters.putIfAbsent(requestParameterName, new ArrayList<>());
                final String requestParameterValue = requestParameter.length > 1 ? decodeUrlComponent(requestParameter[1]) : null;
                requestParameters.get(requestParameterName).add(requestParameterValue);
            }
        }
        return requestParameters;
    }

    private static void startTMServer() throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);

        server.createContext("/func1", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                final String requestMethod = he.getRequestMethod().toUpperCase();
                switch (requestMethod) {
                    case METHOD_GET:
                        final Map<String, List<String>> requestParameters = getRequestParameters(he.getRequestURI());
                        // do something with the request parameters
                        final String responseBody = "['hello world!']";
                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                        he.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                        he.getResponseBody().write(rawResponseBody);
                        break;
                    case METHOD_OPTIONS:
                       // headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        //he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
                        break;
                    default:
                        //headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        //he.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                        break;
                }
            } finally {
                he.close();
            }
        });
        server.start();
        /*
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            System.out.println("[INFO]: Server has started on 127.0.0.1:"+ PORT +".\nWaiting for a connection...");
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+ PORT +".");
            System.exit(1);
        }

        Socket client = serverSocket.accept();
        System.out.println("Connected to: "+client.getInetAddress().toString());

        InputStream in = client.getInputStream();

        /*
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        } */

       /* PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        clientSocket.getInputStream()));
        String inputLine, outputLine;
        KnockKnockProtocol kkp = new KnockKnockProtocol();

        outputLine = kkp.processInput(null);
        out.println(outputLine);

        while ((inputLine = in.readLine()) != null) {
            outputLine = kkp.processInput(inputLine);
            out.println(outputLine);
            if (outputLine.equals("Bye."))
                break;
        }
       // out.close();
        in.close();
        //clientSocket.close();
        serverSocket.close();*/

    }

    /**
     * Global main class will start the TM server
     * @param args
     */
    public static void main(String[] args){

        getProperties("../config.properties");


    }
}
