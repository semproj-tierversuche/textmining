/*
 *
 */

public class TextMiningPipeline implements Runnable{

	private Object input;	//pointer to input
	private String metamap_host;

	public TextMiningPipeline(Object input, String metamap_host){
		this.input = input;
		this.metamap_host = metamap_host;
	}


	/**
	 * The main method where the input runs through
	 *
	 *
	 */
	@Override
	public void run() {
		//Call Zoning method here

		//delete Stopwords

		//run through metamap TODO Think about Multithreading

		//run through semrep

		//turn intoBioc

		//return Object
	}
}
