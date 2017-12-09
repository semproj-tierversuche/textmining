import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TMCommandline {

    private static String metamap_host;

    /**
     * Gets the configs from the config file
     * @param configDir the dir and filename where to find the config
     */
    private static void getProperties(String configDir){
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream(configDir);

            // load a properties file
            prop.load(input);

            // getting properties
            metamap_host = prop.getProperty("metamap_host");

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

    public static void main(String[] args){
        if(args.length == 1 && args[0].equals("-version")) {
            System.out.println("0.1");
            System.exit(0);
        }
        //demo call of metamap
        if(args.length == 2 && args[0].equals("-mm")){
            String out = MMWrapper.fwdToMMtest(metamap_host, args[1]);
            System.out.println(out);
            System.exit(0);
        }

        if(args.length > 1) {
            System.err.println("-version to get the Version number and -mm \"TMING STRING\"");
            System.exit(1);
        }

        System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
    }
}
