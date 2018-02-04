/*
 * @version: 2018v4
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author pLukas
 */
public class TextMiningPipeline implements Runnable{

	private List<SinglePaper> spList;
	private Config c;
	private OutputStream outStream;

	TextMiningPipeline(Config c, List<SinglePaper> input, OutputStream out){
		this.c = c;
		this.spList = input;
		this.outStream = out;
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
		String outdivider = "\n"+c.output_divider+"\n";
		VersuchszweckChecker vzc = new VersuchszweckChecker(c.vzc_dic_dir, c.vzc_stop_dir);
		spList.forEach(tmpSP -> {
			BioCConverter.spToBioCStream(c, tmpSP, outStream, vzc);
			if( ! c.bioC_Xml_only ){
				try{
					outStream.write(outdivider.getBytes());
				}catch (IOException ioE){
					ioE.printStackTrace(c.errorStream);
				}
			}
		} );
	}
}
