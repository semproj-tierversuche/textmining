import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    //configs
    String txtMining_host;
    int txtMining_port;
    String metamap_host;
    String mm_Abstract_opt;
    String mm_MeshHeading_opt;
    String input_divider;
    String output_divider;
    String end_of_Stream;

    /**
     * Gets the configs from the Config file
     * the dir and filename where to find the Config
     *
     * @param dir the directory of the config file
     */
    Config(String dir){
        Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream(dir);

            // load a properties file
            prop.load(input);

            // getting properties
            txtMining_host = prop.getProperty("txtMining_host");
            txtMining_port = Integer.decode(prop.getProperty("txtMining_port"));
            metamap_host = prop.getProperty("metamap_host");
            mm_Abstract_opt = prop.getProperty("mm_Abstract_opt");
            mm_MeshHeading_opt = prop.getProperty("mm_MeshHeading_opt");
            input_divider = prop.getProperty("input_divider");
            output_divider = prop.getProperty("output_divider");
            end_of_Stream = prop.getProperty("end_of_Stream");

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



}
