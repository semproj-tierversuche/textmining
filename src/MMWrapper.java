/*
 * @version: 2017v1
 */

import gov.nih.nlm.nls.metamap.*;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

public class MMWrapper {

    //Metamap Config:
    private Config c;

    MMWrapper(Config c){
        this.c = c;
    }


    void runTitle(List<SinglePaper> input) {
        MetaMapApi mmapi = new MetaMapApiImpl();

        if (c.mm_title_opt != null) {
            mmapi.setOptions(c.mm_title_opt);
        }
        input.forEach(tmpPaper -> {
            if (tmpPaper.title != null && !tmpPaper.title.isEmpty()) {
                String tmpTitle = StringEscapeUtils.unescapeXml(tmpPaper.title);
                List<Result> resultList = mmapi.processCitationsFromString(tmpTitle);
                Result res = resultList.get(0);

                try {
                    for (AcronymsAbbrevs aa : res.getAcronymsAbbrevs()) {
                        aa.getAcronym();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void runAbstracts(List<SinglePaper> input){
        MetaMapApi mmapi = new MetaMapApiImpl();
        if (c.mm_Abstract_opt != null) {
            // mmapi.setOptions(c.mm_Abstract_opt);        //TODO figure out which options can be used with which library
        }

        input.forEach(tmpPaper -> {
            if(tmpPaper.paperAbstract != null && !tmpPaper.paperAbstract.isEmpty()){
                String tmpAbstract = StringEscapeUtils.unescapeXml(tmpPaper.paperAbstract); //gets rid of the XML escape sequences
                List<Result> resultList = mmapi.processCitationsFromString(tmpAbstract);
                Result res = resultList.get(0);

                try {
                    for (Utterance utterance : res.getUtteranceList()) {

                        String debugExp;
                        if (c.mm_abstract_utterance) {
                            tmpPaper.addUttPos(utterance.getPosition());
                        }


                        for (PCM pcm : utterance.getPCMList()) {
                            for (Mapping map : pcm.getMappingList()) {
                                for (Ev mapEv : map.getEvList()) {
                                    if (mapEv.getScore() > c.mm_passing_score) {
                                        tmpPaper.addPaperAbstractEv(mapEv);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
