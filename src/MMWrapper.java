/*
 * @version: 2017v1
 */

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Result;

import java.util.List;

public class MMWrapper {

    //Metamap Config:
    private Config c;

    MMWrapper(Config c){
        this.c = c;
    }

    void runMeshHeadings(List<SinglePaper> input){

        MetaMapApi mmapi = new MetaMapApiImpl();
        mmapi.setOptions(c.mm_MeshHeading_opt);

        input.forEach( tmpPaper -> {
            if (tmpPaper.meshHeadings != null && !tmpPaper.meshHeadings.isEmpty()) {
                List<Result> resultList = mmapi.processCitationsFromString(tmpPaper.meshHeadings);
                Result res = resultList.get(0);
                tmpPaper.meshHeadings = res.getMachineOutput();
            }
        });
    }

    void runAbstracts(List<SinglePaper> input){
        MetaMapApi mmapi = new MetaMapApiImpl();
        mmapi.setOptions(c.mm_Abstract_opt);

        input.forEach(tmpPaper -> {
            if(tmpPaper.paperAbstract != null && !tmpPaper.paperAbstract.isEmpty()){
                List<Result> resultList = mmapi.processCitationsFromString(tmpPaper.paperAbstract);
                Result res = resultList.get(0);
                tmpPaper.paperAbstract = res.getMachineOutput();
            }
        });
    }

    /**
     * first test to see if components work together
     * @param input text to metamap
     * @return mined string
     */
     String fwdToMMtest(String input){

        MetaMapApi mmapi = new MetaMapApiImpl();
        mmapi.setOptions("-b"); //getting all TODO figure out which options exactly we will need
        List<Result> resultList = mmapi.processCitationsFromString(input);
        Result res = resultList.get(0);
        return res.getMachineOutput();
    }

}
