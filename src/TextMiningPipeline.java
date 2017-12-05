/**
 *  Todo: Sobald Metamap per Kommandozeile aufrufbar, den Input da reinschieben und den Output per XSLT nach BioC konvertieren.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;


//prozeduraler Stub
//Umsetzen in objektorientierten Code sobald XSLT und Metamap-Commandline vorliegen
public class TextMiningPipeline {
	private static Scanner input = new Scanner(System.in);
	private static String metamap_host;

	private static void showUsage() {
		System.err.println("-version	.. print version number to stdout\n-mm \"text\"    run metamap on text");
		System.exit(1);
	}

	private static void getProperties(){
		Properties prop = new Properties();
		InputStream input = null;
		try {

			input = new FileInputStream("../config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			metamap_host = prop.getProperty("metamap_host");

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

	public static void main(String[] args) {
		int i=0;
		while(i == 0){
			i=0;
		}

		if(args.length == 1 && args[0].equals("-version")) {
			System.out.println("0.1");
			System.exit(0);
		}
		//demo call of metamap
		if(args.length == 2 && args[0].equals("-mm")){
			String out = MMWrapper.fwdToMMtest(metamap_host, args[1]);
			System.out.println(out);
			System.exit(0);
		}

		if(args.length > 1) {
			showUsage();
		}

		System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
		while(input.hasNextLine()) {
			System.out.println(input.nextLine());
		}
	}
}
