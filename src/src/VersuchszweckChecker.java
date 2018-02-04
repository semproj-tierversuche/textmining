import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;


/**
 * Versucht aus Pubmed-Abstracts den Satz auszuwählen, der den Versuchszweck am besten beschreibt.
 * @author Daniel Grund
 */
public class VersuchszweckChecker {
	//selbst Pattern#compile() aufzurufen erspart, das wiederholt implizit von String#matches() machen zu lassen
	private final Set<Pattern> dictionary;
	private final Set<Pattern> stopWords;
	
	/**
	 * Versuchszweck eines pubmed-Artikels erkennen durch Auswahl des dafür wichtigsten Satzes
	 * 
	 * Man zerlege einen pubmed-Abstract in einzelne Sätze jeweils vom Typ String.
	 * Für jeden Satz, in ihrer Reihenfolge im pubmed-Abstract, rufe man diese Methode auf.
	 * Der erste Satz, für den diese Methode true zurückgibt, beschreibt vermutlich den Versuchszweck am besten.
	 * Für die nachfolgenden Sätze des gleichen Abstracts muss diese Methode dann nicht mehr aufgerufen werden.
	 * 
	 * Die Versuchszweck-Sätze mehrere Dokumente können dann im eigenen Code per beliebigen Verfahren paarweise auf Ähnlichkeit verglichen werden.
	 * 
	 * Diese Methode ist thread-safe.
	 *  
	 * @param sentence Satz aus einem pubmed-Abstract
	 * @see getFirstVersuchszweck(String[])
	 * @return true falls der übergebene Satz vermutlich einen Versuchszweck beschreibt
	 */
	public boolean isVersuchszweck(final String sentence)  {
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
	 * Ruft #isVersuchszweck(String) in einer Schleife für jedes Element des übergebenen Arrays auf und liefert das erste s mit isVersuchszweck(s)=true
	 * 
	 * Diese Methode ist thread-safe.
	 * 
	 * @param sentences gesamter pubmed-Abstract bereits in einzelne Sätze zerlegt
	 * @return gefundenen Versuchszweck oder null, wenn Versuchszweck nicht erkannt
	 */
	public String getFirstVersuchszweck(final String[] sentences)  {
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
	 * Als Übergabeparameter eignet sich evtl. auch File f = new File(ClassLoader.getResource("dictionary.txt"))
	 * 
	 * @param dictionaryFile Pfad zum dictionary mit Here-We-Phrasen
	 * @param stopwordFile Pfad zur Liste mit Stopworten, null wenn kein Stopwörter genutzt werden sollen
	 * @throws IOException Input-Datei nicht lesbar
	 */
	public VersuchszweckChecker(final File dictionaryFile, final File stopwordFile) throws IOException {
		Set<Pattern> dictionary = new HashSet<Pattern>();
		Set<Pattern> stopWords = new HashSet<Pattern>();
		
		if(stopwordFile != null) {
			for(String stopwordEntry : Files.readAllLines(stopwordFile.toPath())) {
				Pattern p = Pattern.compile(".*" + stopwordEntry, Pattern.CASE_INSENSITIVE);
				stopWords.add(p);
			}
		}
		this.stopWords = Collections.unmodifiableSet(stopWords);
		
		//kann Scanner nicht nutzen, weil Zeilen auch mal nen Leerzeichen enthalten
		for(String dictionaryEntry : Files.readAllLines(dictionaryFile.toPath())) {
			Pattern p = Pattern.compile(".*" + dictionaryEntry, Pattern.CASE_INSENSITIVE);
			dictionary.add(p);
		}
		
		this.dictionary = Collections.unmodifiableSet(dictionary);
	}
}