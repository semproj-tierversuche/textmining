/*
 * @version: 2018v4
 */


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author pLukas
 */
public class TMCommandline {

    private static String ConfigDir = "/config.properties";
    private static Config c;
    private static final String version="0.4";

    /**
     * reads from Standard IN runs TM tools and pushes everything to stdOUT
     */
    private static void runMultiFiles(){
        int filesProcessed=0;
        long startTime = System.currentTimeMillis();
        List<SinglePaper> spList = new ArrayList<>();
        Scanner s_in = new Scanner(System.in);

        InputStream tmp_is;
        StringBuilder tmp_strBl = new StringBuilder();
        String tmp_str;

        while(!s_in.hasNext(c.end_of_Stream)) {

            tmp_str = s_in.nextLine();
            if(tmp_str.endsWith(c.end_of_Stream)){

                tmp_is = new ByteArrayInputStream(tmp_strBl.toString().getBytes(StandardCharsets.US_ASCII)); //converting into Stream because saxbuilder does not take strings, must be ASCII for Metamap
                SinglePaper spaper = BioCConverter.bioCStreamToSP(tmp_is);      //adding current file to the SinglePaper collection
                if(spaper != null){
                    spList.add(spaper);
                    filesProcessed++;
                }
                tmp_strBl.setLength(0); //setting back string builder, for new file
            }else{
                tmp_strBl.append(tmp_str);
            }
        }

        TextMiningPipeline tmPipe = new TextMiningPipeline(c, spList, System.out);
        tmPipe.run();

        if( ! c.bioC_Xml_only){
            System.out.print("\n"+c.end_of_Stream+"\n");
            long time_needed = System.currentTimeMillis() - startTime;
            //System.out.println("Time needed in ms: "+ time_needed+" --- files processed: "+filesProcessed);
        }

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
        TextMiningPipeline tmPipe = new TextMiningPipeline(c, spList, System.out);
        tmPipe.run();

        VersuchszweckChecker vzc = new VersuchszweckChecker(c.vzc_dic_dir, c.vzc_stop_dir);
        spList.forEach(tmp -> BioCConverter.spToBioCStream(c, tmp, System.out, vzc));

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
            //System.err.println("-version to get the Version number and -tm < -c \"CONFIGDIR\"> < -file\"File Dir STRING\">");
            System.exit(1);
        }

        //System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
    }
}
