import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;


/**
 * Versucht aus Pubmed-Abstracts den Satz auszuw�hlen, der den Versuchszweck am besten beschreibt.
 * @author Daniel Grund
 */
public class VersuchszweckChecker {
	//selbst Pattern#compile() aufzurufen erspart, dass wiederholt implizit von String#matches() machen zu lassen
	private final Set<Pattern> dictionary;
	private final Set<Pattern> stopWords;
	
	/**
	 * Versuchszweck eines pubmed-Artikels erkennen durch Auswahl des daf�r wichtigsten Satzes
	 * 
	 * Man zerlege einen pubmed-Abstract in einzelne S�tze jeweils vom Typ String.
	 * F�r jeden Satz, in ihrer Reihenfolge im pubmed-Abstract, rufe man diese Methode auf.
	 * Der erste Satz, f�r den diese Methode true zur�ckgibt, beschreibt vermutlich den Versuchszweck am besten.
	 * F�r die nachfolgenden S�tze des gleichen Abstracts muss diese Methode dann nicht mehr aufgerufen werden.
	 * 
	 * Die Versuchszweck-S�tze mehrere Dokumente k�nnen dann im eigenen Code per beliebigen Verfahren paarweise auf �hnlichkeit verglichen werden.
	 * 
	 * Diese Methode ist thread-safe.
	 *  
	 * @param sentence Satz aus einem pubmed-Abstract
	 * @see getFirstVersuchszweck(String[])
	 * @return true falls der �bergebene Satz vermutlich einen Versuchszweck beschreibt
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
	 * Ruft #isVersuchszweck(String) in einer Schleife f�r jedes Element des �bergebenen Arrays auf und liefert das erste s mit isVersuchszweck(s)=true
	 * 
	 * Diese Methode ist thread-safe.
	 * 
	 * @param sentences gesamter pubmed-Abstract bereits in einzelne S�tze zerlegt
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
	 * Als �bergabeparameter eignet sich evtl. auch File f = new File(ClassLoader.getResource("dictionary.txt"))
	 * 
	 * @param dictionaryFile Pfad zum dictionary mit Here-We-Phrasen
	 * @param stopwordFile Pfad zur Liste mit Stopworten, null wenn kein Stopw�rter genutzt werden sollen
	 * @throws FileNotFoundException stopwordFile nicht gefunden
	 * @throws IOException dictionaryFile nicht lesbar
	 */
	 VersuchszweckChecker(final File dictionaryFile, final File stopwordFile) throws FileNotFoundException, IOException {
		Set<Pattern> dictionary = new HashSet<Pattern>();
		Set<Pattern> stopWords = new HashSet<Pattern>();
		
		if(stopwordFile != null) {
			//kann Scanner benutzen, weil alle Zeilen frei von Leerzeichen
			try (Scanner scanner = new Scanner(stopwordFile)) {
				while(scanner.hasNext()) {
					Pattern p = Pattern.compile(".*" + scanner.next(), Pattern.CASE_INSENSITIVE);
					stopWords.add(p);
				}
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