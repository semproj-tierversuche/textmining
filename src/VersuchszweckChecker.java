/*
 * @version: 2018v4
 */

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Versucht aus Pubmed-Abstracts den Satz auszuw�hlen, der den Versuchszweck am besten beschreibt.
 * @author Daniel Grund
 */
public class VersuchszweckChecker {
	//selbst Pattern#compile() aufzurufen erspart, dass wiederholt implizit von String#matches() machen zu lassen
	private final Set<Pattern> dictionary;
	private final Set<Pattern> stopWords;
	
	/**
	 * Versuchszweck eines pubmed-Artikels erkennen durch Auswahl des dafuer wichtigsten Satzes
	 * 
	 * Man zerlege einen pubmed-Abstract in einzelne Saetze jeweils vom Typ String.
	 * Fuer jeden Satz, in ihrer Reihenfolge im pubmed-Abstract, rufe man diese Methode auf.
	 * Der erste Satz, fuer den diese Methode true zurueckgibt, beschreibt vermutlich den Versuchszweck am besten.
	 * Fuer die nachfolgenden Saetze des gleichen Abstracts muss diese Methode dann nicht mehr aufgerufen werden.
	 * 
	 * Die Versuchszweck-Saetze mehrere Dokumente k�nnen dann im eigenen Code per beliebigen Verfahren paarweise auf Aehnlichkeit verglichen werden.
	 * 
	 * Diese Methode ist thread-safe.
	 *  
	 * @param sentence Satz aus einem pubmed-Abstract
	 * @return true falls der Uebergebene Satz vermutlich einen Versuchszweck beschreibt
	 */
	boolean isVersuchszweck(final String sentence)  {
		assert(dictionary.size() > 0);
		for(Pattern stopWord : stopWords) {
			if(stopWord.matcher(sentence).lookingAt()) {
				return false;
			}
		}
		for(Pattern dictionaryEntry : dictionary) {
			if(dictionaryEntry.matcher(sentence).lookingAt()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Ruft #isVersuchszweck(String) in einer Schleife fuer jedes Element des Uebergebenen Arrays auf und liefert das erste s mit isVersuchszweck(s)=true
	 * 
	 * Diese Methode ist thread-safe.
	 * 
	 * @param sentences gesamter pubmed-Abstract bereits in einzelne Saetze zerlegt
	 * @return gefundenen Versuchszweck oder null, wenn Versuchszweck nicht erkannt
	 */
	String getFirstVersuchszweck(final String[] sentences)  {
		for(String s : sentences) {
			if(isVersuchszweck(s)) {
				return s;
			}
		}
		return null;
	}
	
	/**
	* Initialisiere das Dictionary.
	*
	* Als Uebergabeparameter eignet sich evtl. auch File f = new File(ClassLoader.getResource("dictionary.txt"))
	*
	* @param dictionaryFile Pfad zum dictionary mit Here-We-Phrasen
	* @param stopwordFile Pfad zur Liste mit Stopworten, null wenn kein Stopwoerter genutzt werden sollen
	*/
	VersuchszweckChecker(String dictionaryFile, String stopwordFile){

        Set<Pattern> dictionary = new HashSet<>();
        Set<Pattern> stopWords = new HashSet<>();

		if(stopwordFile != null) {
			InputStream stopF = this.getClass().getResourceAsStream(stopwordFile);
			Scanner s_stop = new Scanner(stopF);
			while(s_stop.hasNextLine()) {
				Pattern p = Pattern.compile(".*" + s_stop.nextLine(), Pattern.CASE_INSENSITIVE);
				stopWords.add(p);
			}
		}
        this.stopWords = Collections.unmodifiableSet(stopWords);

		InputStream dicF = this.getClass().getResourceAsStream(dictionaryFile);
		Scanner s_dic = new Scanner(dicF);
		while (s_dic.hasNextLine()){
			Pattern p = Pattern.compile(".*"+ s_dic.nextLine(), Pattern.CASE_INSENSITIVE);
			dictionary.add(p);
		}
		this.dictionary = Collections.unmodifiableSet(dictionary);
	}
}