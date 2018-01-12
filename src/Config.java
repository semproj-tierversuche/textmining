/*
 * @version: 2018v3
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    //configs
    String txtMining_host;
    int txtMining_port;
    int txtMining_batch_size;

    //metamap configs
    String metamap_host_1;
    int metamap_ports_start_1;
    int metamap_ports_end_1;
    String metamap_host_2;
    int metamap_ports_start_2;
    int metamap_ports_end_2;
    String metamap_host_3;
    int metamap_ports_start_3;
    int metamap_ports_end_3;

    int mm_wait_before_retry;

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
    Config(String dir, boolean externalConfig) {
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
            txtMining_batch_size = Integer.decode(prop.getProperty("txtMining_batch_size"));
            mm_Abstract_opt = prop.getProperty("mm_Abstract_opt");
            mm_MeshHeading_opt = prop.getProperty("mm_MeshHeading_opt");
            file_delimiter = prop.getProperty("file_delimiter");
            output_divider = prop.getProperty("output_divider");
            end_of_Stream = prop.getProperty("end_of_Stream");
            //MM hosting data
            metamap_host_1 = prop.getProperty("metamap_host_1");
            metamap_ports_start_1 = Integer.decode(prop.getProperty("metamap_ports_start_1"));
            metamap_ports_end_1 = Integer.decode(prop.getProperty("metamap_ports_end_1"));
            metamap_host_1 = prop.getProperty("metamap_host_2");
            metamap_ports_start_1 = Integer.decode(prop.getProperty("metamap_ports_start_2"));
            metamap_ports_end_1 = Integer.decode(prop.getProperty("metamap_ports_end_2"));
            metamap_host_1 = prop.getProperty("metamap_host_3");
            metamap_ports_start_1 = Integer.decode(prop.getProperty("metamap_ports_start_3"));
            metamap_ports_end_1 = Integer.decode(prop.getProperty("metamap_ports_end_3"));
            mm_wait_before_retry = Integer.decode(prop.getProperty("mm_wait_before_retry"));

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
