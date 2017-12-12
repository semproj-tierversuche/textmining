/*
 * @version: 2017v1
 */

import java.util.List;

public class TextMiningPipeline implements Runnable{

	private List<SinglePaper> spList;	//TODO decide on proper input type
	private Config c;

	TextMiningPipeline(Config c, List<SinglePaper> input){
		this.c = c;
		this.spList = input;
	}

	List<SinglePaper> getSpList(){
		return spList;
	}

	/**
	 * The main method where the input runs through
	 *
	 *
	 */
	@Override
	public void run() {
		//init
		MMWrapper mmw = new MMWrapper(c);

		//transform input into the SinglePaper class
		//SinglePaper si = new SinglePaper("","","","","");


		//Call Zoning method here

		//delete Stopwords

		//run through metamap TODO Think about Multithreading
		mmw.runMeshHeadings(spList);
		mmw.runAbstracts(spList);
		//run through semrep

		//turn intoBioc

		//return Object
	}
}
