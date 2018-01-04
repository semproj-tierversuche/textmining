/*
 * @version: 2017v1
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TMCommandline {

    private static String ConfigDir = "/config.properties";
    private static Config c;
    private static final String version="0.2";

    /**
     * reads from Standard IN runs TM tools and pushes everything to stdOUT
     */
    private static void runMultiFiles(){
        List<SinglePaper> spList = new ArrayList<>();
        Scanner s_in = new Scanner(System.in).useDelimiter(c.file_delimiter);

        InputStream tmp_is;
        String tmp_str;

        while(!s_in.hasNext(c.end_of_Stream)) {     //TODO: while true and end progam with input

            if(s_in.hasNext()){
                tmp_str = s_in.next();
                try {
                    tmp_is = new ByteArrayInputStream(tmp_str.getBytes(StandardCharsets.UTF_8.name())); //converting into Stream because saxbuilder does not take strings
                    SinglePaper spaper = BioCConverter.bioCStreamToSP(tmp_is);//adding current file to the SinglePaper collection
                    spList.add(spaper);
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }
        }

        TextMiningPipeline tmPipe = new TextMiningPipeline(c, spList);
        tmPipe.run();

        spList.forEach(tmpSP -> {
            BioCConverter.spToBioCStream(tmpSP, System.out);
            System.out.print("\n"+c.output_divider+"\n");
        } );
        System.out.print("\n"+c.end_of_Stream+"\n");

        System.exit(0);
    }

    /**
     * takes a BioC XML file, runs TM on it and pushes it to stdOUT
     * @param dirToBioC dir to file
     */
    private static void runSingleFile(String dirToBioC){
        List<SinglePaper> spList = new ArrayList<>();
        SinglePaper sp = BioCConverter.bioCFileToSP(dirToBioC);
        spList.add(sp);
        TextMiningPipeline tmPipe = new TextMiningPipeline(c, spList);
        tmPipe.run();

        spList.forEach(tmp->BioCConverter.spToBioCStream(tmp, System.out));


        System.exit(0);
    }

    /**
     * Options when running -tm: -c <CONFIGDIR> (read in a different config file)  -file <FILEDIR> (reads in one BioC XML and processes it)
     *
     * @param args -version, -tm (for running tm)
     */
    public static void main(String[] args){

        //main program
        if(args.length == 1 && args[0].equals("-version")) {
            System.out.println(version);
            System.exit(0);

        }else if(args.length >= 1 && args[0].equals("-tm")){
            boolean externalConfig = false;
            String dirToBioCDoc;
            int i_args = 1;
            if(args.length >= 2 && args[1].equals("-c")) {
                ConfigDir = args[2];
                i_args+=2;
                externalConfig = true;
            }

            c = new Config(ConfigDir,externalConfig);

            if(args.length >= (1+i_args) && args[i_args].equals("-file")) {
                dirToBioCDoc = args[1+i_args];
                runSingleFile(dirToBioCDoc);
            }else {
                runMultiFiles();
            }

        }

        if(args.length > 1) {
            System.err.println("-version to get the Version number and -tm < -c \"CONFIGDIR\"> < -file\"File Dir STRING\">");
            System.exit(1);
        }

        System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
    }
}
