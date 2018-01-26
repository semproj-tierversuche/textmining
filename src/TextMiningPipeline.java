/*
 * @version: 2018v2
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
        if (c.mm_title) {
			mmw.runTitle(spList);
        }
        if (c.mm_abstract) {
            mmw.runAbstracts(spList);
        }

		//run through semrep

		//turn intoBioc

		//return Object
	}
}
