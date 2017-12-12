/*
 * @version: 2017v1
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TMCommandline {

    private static String ConfigDir = "src/config.properties";
    private static Config c;

    /**
     * reads from Standard IN runs TM tools and pushes everything to stdOUT
     */
    private static void runMultiFiles(){
        List<SinglePaper> spList = new ArrayList<>();
        Scanner s_in = new Scanner(System.in);

        StringBuilder tmp_sb = new StringBuilder();
        String tmp_str;

        while(s_in.hasNextLine()) {
            tmp_str = s_in.nextLine();
            if(tmp_str.equals(c.input_divider)){
                //adding current file to the SinglePaper collection
                SinglePaper spaper = BioCConverter.bioCStringToSP(tmp_sb.toString());
                spList.add(spaper);
                tmp_sb.setLength(0); //deleting current String builder
            }else {
                tmp_sb.append(tmp_str);
            }
        }

        TextMiningPipeline tmPipe = new TextMiningPipeline(c, spList);
        tmPipe.run();

        spList.forEach(tmpSP -> {
            BioCConverter.spToBioCStream(tmpSP, System.out);
            System.out.print("\n"+c.output_divider+"\n");
        } );
        System.out.print("\n"+c.end_of_Stream);

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
            System.out.println("0.1");
            System.exit(0);

        }else if(args.length >= 1 && args[0].equals("-tm")){
            String dirToBioCDoc;
            int i_args = 1;
            if(args[1].equals("-c")) {
                ConfigDir = args[2];
                i_args+=2;
            }

            c = new Config(ConfigDir);

            if(args[i_args].equals("-file")) {
                dirToBioCDoc = args[1+i_args];
                runSingleFile(dirToBioCDoc);
            }else {
                runMultiFiles();
            }

        }

        if(args.length > 1) {
            System.err.println("-version to get the Version number and -mm <-c \"CONFIGDIR\"> <-data\"File Dir STRING\">");
            System.exit(1);
        }

        System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
    }
}
