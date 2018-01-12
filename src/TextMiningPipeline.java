/*
 * @version: 2018v3
 */

import java.util.List;

public class TextMiningPipeline implements Runnable{

    private List<SinglePaper> spList;
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
        MMWrapper mmWr = new MMWrapper(c);

		//transform input into the SinglePaper class
		//SinglePaper si = new SinglePaper("","","","","");


		//Call Zoning method here

		//delete Stopwords

        /*
         *
         * running through metamap here
         *
         */
        //getting a open Metamap server
        MMHost mmh;
        do {
            mmh = TMCommandline.openMMhosts.poll();
            if (mmh == null) {
                try {
                    Thread.sleep(c.mm_wait_before_retry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } while (mmh == null);

		//mmw.runMeshHeadings(spList);
        mmWr.runAbstracts(spList, mmh.getHostUrl(), mmh.getPort());

        //returning the open connection
        TMCommandline.openMMhosts.add(mmh);
        /*
         * END of Metamap
         */

		//run through semrep

		//turn intoBioc

		//return Object

        //TODO move this, as multiple threads overwrite each other
        spList.forEach(tmpSP -> {
            BioCConverter.spToBioCStream(tmpSP, System.out);
            System.out.print("\n" + c.output_divider + "\n");
        });
	}
}
