import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Result;

import java.util.List;

public class MMWrapper {

    /**
     * first test to see if components work together
     * @param host  host:port
     * @param input text to metamap
     * @return mined string
     */
     static String fwdToMMtest(String host, String input){

        MetaMapApi mmapi = new MetaMapApiImpl(host);
        mmapi.setOptions("-b"); //getting all TODO figure out which options exactly we will need
        List<Result> resultList = mmapi.processCitationsFromString(input);
        Result res = resultList.get(0);
        return res.getMachineOutput();
    }
}
