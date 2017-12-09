/*
 * @version: 2017v1
 */

public class TMCommandline {

    private static String ConfigDir = "src/config.properties";

    private static void runSingleFile(Config c, String dirToBioC){

        BioCReader.bioCtoSinglePaper(dirToBioC);

        TextMiningPipeline tmp = new TextMiningPipeline(c, " ");//TODO
        tmp.run();
        String out = tmp.getOutput().toString();
        System.out.println(out);
        System.exit(0);
    }

    public static void main(String[] args){
        if(args.length == 1 && args[0].equals("-version")) {
            System.out.println("0.1");
            System.exit(0);
        }

        //demo call of metamap
        if(args.length >= 2 && args[0].equals("-mm")){
            String dirToBioCDoc;
            int pos_txtForMM=1;

            if(args[1].equals("-c")) {
                ConfigDir = args[2];
                pos_txtForMM += 2;
            }
            if(args[1].equals("-data")) {
                dirToBioCDoc = args[2];
                Config c = new Config(ConfigDir);
                runSingleFile(c, dirToBioCDoc);
            }

        }

        if(args.length > 1) {
            System.err.println("-version to get the Version number and -mm \"CONFIGDIR\" \"TMING STRING\"");
            System.exit(1);
        }

        System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
    }
}
