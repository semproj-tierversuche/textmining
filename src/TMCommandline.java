public class TMCommandline {

    private static final String ConfigDir = "config.properties";
    private static String metamap_host;

    public static void main(String[] args){
        if(args.length == 1 && args[0].equals("-version")) {
            System.out.println("0.1");
            System.exit(0);
        }
        //demo call of metamap
        if(args.length == 3 && args[0].equals("-mm")){
            Config c = new Config(ConfigDir);
            TextMiningPipeline tmp = new TextMiningPipeline(c, " ");//TODO
            tmp.run();
            String out = tmp.getOutput().toString();
            System.out.println(out);
            System.exit(0);
        }

        if(args.length > 1) {
            System.err.println("-version to get the Version number and -mm \"CONFIGDIR\" \"TMING STRING\"");
            System.exit(1);
        }

        System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
    }
}
