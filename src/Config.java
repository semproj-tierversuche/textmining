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
    String mm_title_opt;
    String file_delimiter;
    String output_divider;
    String end_of_Stream;

    //mm
    int mm_passing_score;
    boolean mm_title;
    boolean mm_abstract;
    boolean mm_abstract_annotations;
    boolean mm_abstract_utterance;

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
            mm_Abstract_opt = prop.getProperty("mm_Abstract_opt", null);
            mm_title_opt = prop.getProperty("mm_title_opt", null);
            file_delimiter = prop.getProperty("file_delimiter");
            output_divider = prop.getProperty("output_divider");
            end_of_Stream = prop.getProperty("end_of_Stream");

            mm_passing_score = Integer.decode(prop.getProperty("mm_passing_score"));
            mm_abstract_annotations = Boolean.parseBoolean(prop.getProperty("mm_abstract_annotations"));
            mm_title = Boolean.parseBoolean(prop.getProperty("mm_title"));
            mm_abstract = Boolean.parseBoolean(prop.getProperty("mm_abstract"));
            mm_abstract_utterance = Boolean.parseBoolean(prop.getProperty("mm_abstract_utterance"));

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
