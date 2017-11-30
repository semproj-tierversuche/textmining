/**
 *  Todo: Sobald Metamap per Kommandozeile aufrufbar, den Input da reinschieben und den Output per XSLT nach BioC konvertieren.
 */
//package textmining.src;

import java.io.*;
import java.util.Scanner;


//prozeduraler Stub
//Umsetzen in objektorientierten Code sobald XSLT und Metamap-Commandline vorliegen
public class TextMiningPipeline {
	private static Scanner input = new Scanner(System.in);

	private static void showUsage() {
		System.err.println("-version	.. print version number to stdout");
		System.exit(1);
	}
	
	public static void main(String[] args) {
		if(args.length > 1) {
			showUsage();
		}
		if(args.length == 1 && args[0].equals("-version")) {
			System.out.println("0.1");
			System.exit(0);
		}
		System.err.println("Metamap command line wrapper missing, piping stdin to stdout:");
		while(input.hasNextLine()) {
			System.out.println(input.nextLine());
		}
	}
}
