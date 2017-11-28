/**
 *  Codeschnipsel um Stopwörter zu löschen
 */
package textmining.src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class stopwords{
	public void removeStopwords(String s) throws IOException {
		FileReader fr = new FileReader("assets/stopwords.txt");
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> wordsList = new ArrayList<String>();
		Set<String> stopWordsSet = new HashSet<String>();
		String zeile = null;

		while( (zeile = br.readLine()) != null ) {
			stopWordsSet.add(zeile);
		}
		String[] words = s.split(" ");

		for(String word : words)
		{
			String wordCompare = word.toLowerCase();
			if(!stopWordsSet.contains(wordCompare))
			{
				wordsList.add(word);
			}
		}

		for (String str : wordsList){
			System.out.print(str+" ");
		}
	}
}

