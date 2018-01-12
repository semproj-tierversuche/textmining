/*
 * @version: 2018v3
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TMCommandline {

    private static String ConfigDir = "/config.properties";
    private static Config c;
    private static final String version = "0.3";
    static Queue<MMHost> openMMhosts = new ConcurrentLinkedQueue<>();    // a list containing open Metamap host connections


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
            if(tmp_str.equals(c.file_delimiter)){

                // adding a new paper to the process stream
                tmp_is = new ByteArrayInputStream(tmp_strBl.toString().getBytes(StandardCharsets.US_ASCII)); //converting into Stream because saxbuilder does not take strings, must be ASCII for Metamap
                SinglePaper spaper = BioCConverter.bioCStreamToSP(tmp_is);      //adding current file to the SinglePaper collection
                if(spaper != null){
                    spList.add(spaper);
                    filesProcessed++;
                }
                tmp_strBl.setLength(0); //setting back string builder, for new file

                //kicking off a new thread
                if ((filesProcessed % c.txtMining_batch_size) == 0) {
                    TextMiningPipeline tmp_thread = new TextMiningPipeline(c, spList);
                    new Thread(tmp_thread).start();
                }
            }else{
                tmp_strBl.append(tmp_str);
            }
        }

        TextMiningPipeline tmp_thread = new TextMiningPipeline(c, spList);
        tmp_thread.run();


        System.out.print("\n"+c.end_of_Stream+"\n");

        long time_needed = System.currentTimeMillis() - startTime;
        System.out.println("Time needed in ms: "+ time_needed+" --- files processed: "+filesProcessed);
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
     * generates a list of metamap hosts. Config must be loaded beforehand
     */
    private static void initMMhosts() {
        if (c.metamap_host_1 != null && !c.metamap_host_1.isEmpty() && c.metamap_ports_start_1 != 0 && c.metamap_ports_end_1 != 0) {
            for (int i_port = c.metamap_ports_start_1; i_port <= c.metamap_ports_end_1; i_port++) {
                MMHost mmh = new MMHost(c.metamap_host_1, i_port);
                openMMhosts.add(mmh);
            }
        }

        if (c.metamap_host_2 != null && !c.metamap_host_2.isEmpty() && c.metamap_ports_start_2 != 0 && c.metamap_ports_end_2 != 0) {
            for (int i_port = c.metamap_ports_start_2; i_port <= c.metamap_ports_end_2; i_port++) {
                MMHost mmh = new MMHost(c.metamap_host_2, i_port);
                openMMhosts.add(mmh);
            }
        }

        if (c.metamap_host_3 != null && !c.metamap_host_3.isEmpty() && c.metamap_ports_start_3 != 0 && c.metamap_ports_end_3 != 0) {
            for (int i_port = c.metamap_ports_start_3; i_port <= c.metamap_ports_end_3; i_port++) {
                MMHost mmh = new MMHost(c.metamap_host_3, i_port);
                openMMhosts.add(mmh);
            }
        }
    }

    /**
     * initialises the program:
     * 1. loading Configuration
     * 2. generating list of Metamap Hosts
     *
     * @param externalConfig true if the config file is loaded externally
     */
    private static void init(boolean externalConfig) {
        c = new Config(ConfigDir, externalConfig);
        initMMhosts();
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

            init(externalConfig);

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
