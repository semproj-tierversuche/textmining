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
    String file_delimiter;
    String output_divider;
    String end_of_Stream;

    /**
     * Gets the configs from the Config file
     * the dir and filename where to find the Config
     *
     * @param dir the directory of the config file
     * @param externalConfig if the config is loaded externally
     */
     Config(String dir, boolean externalConfig){
        Properties prop = new Properties();
        InputStream input = null;
        try {

            if(externalConfig){
                input = new FileInputStream(dir);
            }else {
                input = this.getClass().getResourceAsStream(dir);
            }

            // load a properties file
            prop.load(input);

            // getting properties
            txtMining_host = prop.getProperty("txtMining_host");
            txtMining_port = Integer.decode(prop.getProperty("txtMining_port"));
            metamap_host = prop.getProperty("metamap_host");
            mm_Abstract_opt = prop.getProperty("mm_Abstract_opt");
            mm_MeshHeading_opt = prop.getProperty("mm_MeshHeading_opt");
            file_delimiter = prop.getProperty("file_delimiter");
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
