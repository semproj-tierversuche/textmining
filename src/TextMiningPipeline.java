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

	/**
	 * The main method where the input runs through
	 *
	 *
	 */
	@Override
	public void run() {
		//init
		MMWrapper mmw = new MMWrapper(c);

		//Call Zoning method here


		//run through metamap TODO Think about Multithreading
        if (c.mm_title) {
			mmw.runTitle(spList);
        }
        if (c.mm_abstract) {
            mmw.runAbstracts(spList);
        }

		//Candidate filtering
		for (SinglePaper sp : spList) {
			sp.cleanEvLists();
		}

		//TODO: mark the utterance with the most importance

		//TODO: move turn intoBioC here

		//return Object
	}
}
