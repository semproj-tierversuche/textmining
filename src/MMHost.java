/*
 * @version: 2018v3
 */

public class MMHost {
    private String hostURL;
    private int port;

    MMHost(String hostURL, int port) {
        this.hostURL = hostURL;
        this.port = port;
    }

    String getHostUrl() {
        return hostURL;
    }

    int getPort() {
        return port;
    }

}
