/*
 * @version: 2018v2
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
                String tmpAbstract = StringEscapeUtils.unescapeXml(tmpPaper.paperAbstract); //gets rid of the XML escape sequences
                List<Result> resultList = mmapi.processCitationsFromString(tmpAbstract);
                Result res = resultList.get(0);
                try {
                    for (Utterance utterance : res.getUtteranceList()) {
                        for (PCM pcm : utterance.getPCMList()) {
                            for (Ev candidate : pcm.getCandidateList()) {
                                if (candidate.getScore() > c.mm_title_passing_score) {
                                    tmpPaper.addTitleEv(candidate);
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

    void runAbstracts(List<SinglePaper> input){
        MetaMapApi mmapi = new MetaMapApiImpl();
        if (c.mm_Abstract_opt != null) {
            mmapi.setOptions(c.mm_Abstract_opt);
        }

        input.forEach(tmpPaper -> {
            if(tmpPaper.paperAbstract != null && !tmpPaper.paperAbstract.isEmpty()){
                String tmpAbstract = StringEscapeUtils.unescapeXml(tmpPaper.paperAbstract); //gets rid of the XML escape sequences
                List<Result> resultList = mmapi.processCitationsFromString(tmpAbstract);
                Result res = resultList.get(0);
                try {
                    for (Utterance utterance : res.getUtteranceList()) {
                        tmpPaper.addpUtterancePos(utterance.getPosition());
                        for (PCM pcm : utterance.getPCMList()) {
                            for (Ev candidate : pcm.getCandidateList()) {
                                if (candidate.getScore() > c.mm_abstract_passing_score) {
                                    tmpPaper.addPaperAbstractEv(candidate);
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
