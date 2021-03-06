/*
 * @version: 2018v4
 */

import java.io.OutputStream;
import java.util.List;

/**
 * @author pLukas
 */
public class TextMiningPipeline implements Runnable{

	private final List<SinglePaper> spList;
	private final Config c;
	private final OutputStream outStream;

	TextMiningPipeline(Config c, List<SinglePaper> input, OutputStream out){
		this.c = c;
		this.spList = input;
		this.outStream = out;
	}

	/**
	 * The main method where the input runs through
	 */
	@Override
	public void run() {
		//init
		MMWrapper mmw = new MMWrapper(c);

		//Call Zoning method here


		//run through metamap
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


		//turning intoBioC here:
		//WARNING: Versuchszeckchecker is called in BioCConverter due to efficiency
		VersuchszweckChecker vzc = new VersuchszweckChecker(c.vzc_dic_dir, c.vzc_stop_dir);
        BioCConverter.spToBioCStream(c, spList, outStream, vzc);
	}
}
