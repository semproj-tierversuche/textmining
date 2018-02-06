/*
 * @version: 2018v4
 */


import java.io.*;
import java.util.Properties;

public class Config {

    //configs
    final String txtMining_host;
    final int txtMining_port;
    final int mm_timeOut;
    final String metamap_host;
    final String mm_Abstract_opt;
    final String mm_title_opt;
    final String end_of_Stream;
    final PrintWriter errorStream;

    final String vzc_dic_dir;
    final String vzc_stop_dir;

    //mm
    final int mm_abstract_passing_score;
    final int mm_title_passing_score;
    final boolean mm_title;
    final boolean mm_abstract;
    final boolean mm_abstract_annotations;
    final boolean mm_abstract_utterance;
    final boolean bioC_Xml_only;

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
        PrintWriter tmpPwriter = null;
        try {

            if(externalConfig){
                input = new FileInputStream(dir);
            }else {
                input = this.getClass().getResourceAsStream(dir);
            }

            // load a properties file
            prop.load(input);

            String errorStreamDir = prop.getProperty("err_Log_dir","err_txtMining.log");
            File errorFile = new File(errorStreamDir);
            errorFile.createNewFile();
            tmpPwriter = new PrintWriter(errorFile);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

         this.errorStream = tmpPwriter;

         vzc_dic_dir = "/assets/dictionary.txt";
         vzc_stop_dir = "/assets/stopwords.txt";

         // getting properties
         this.txtMining_host = prop.getProperty("txtMining_host");
         txtMining_port = Integer.decode(prop.getProperty("txtMining_port"));
         mm_timeOut = Integer.decode(prop.getProperty("mm_timeOut"));
         metamap_host = prop.getProperty("metamap_host");
         mm_Abstract_opt = prop.getProperty("mm_Abstract_opt", null);
         mm_title_opt = prop.getProperty("mm_title_opt", null);
         end_of_Stream = prop.getProperty("end_of_Stream");

         mm_abstract_passing_score = Integer.decode(prop.getProperty("mm_abstract_passing_score"));
         mm_title_passing_score = Integer.decode(prop.getProperty("mm_title_passing_score"));
         mm_abstract_annotations = Boolean.parseBoolean(prop.getProperty("mm_abstract_annotations"));
         mm_title = Boolean.parseBoolean(prop.getProperty("mm_title"));
         mm_abstract = Boolean.parseBoolean(prop.getProperty("mm_abstract"));
         mm_abstract_utterance = Boolean.parseBoolean(prop.getProperty("mm_abstract_utterance"));
         bioC_Xml_only = Boolean.parseBoolean(prop.getProperty("bioC_Xml_only"));
    }
}