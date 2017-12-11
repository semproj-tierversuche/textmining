/*
 * @version: 2017v1
 */

import java.util.List;

public class TextMiningPipeline implements Runnable{

	private List<SinglePaper> input;	//TODO decide on proper input type
	private List<SinglePaper> siIn;
	private Config c;
	private Object output;	//TODO decide on proper output type

	TextMiningPipeline(Config c, List<SinglePaper> input){ //TODO decide on proper input type
		this.c = c;
		this.input = input;
	}

	Object getOutput(){
		return output;
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
		mmw.runMeshHeadings(input);

		//run through semrep

		//turn intoBioc

		//return Object
	}
}
