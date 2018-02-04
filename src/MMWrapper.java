/*
 * @version: 2018v4
 */

import gov.nih.nlm.nls.metamap.*;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

/**
 * @author pLukas
 */
public class MMWrapper {

    //Metamap Config:
    private final Config c;

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
                    e.printStackTrace(c.errorStream);
                }

            }
        });

    }

    void runAbstracts(List<SinglePaper> input) {
        MetaMapApi mmapi = new MetaMapApiImpl();
        mmapi.setTimeout(c.mm_timeOut);
        if (c.mm_Abstract_opt != null) {
            mmapi.setOptions(c.mm_Abstract_opt);
        }

        StringBuilder stBuilder = new StringBuilder();
        input.forEach(tmpPaper -> {
            if (tmpPaper.paperAbstract != null && !tmpPaper.paperAbstract.isEmpty()) {
                stBuilder.append(StringEscapeUtils.unescapeXml(tmpPaper.paperAbstract));
                stBuilder.append("\n\n");
            }
        });

        List<Result> resultList = mmapi.processCitationsFromString(stBuilder.toString());

        for(int i=0;i < resultList.size(); i++){
            Result res = resultList.get(i);
            try {
                for (Utterance utterance : res.getUtteranceList()) {
                    input.get(i).addpUtterancePos(utterance.getPosition());
                    for (PCM pcm : utterance.getPCMList()) {
                        for (Ev candidate : pcm.getCandidateList()) {
                            if (candidate.getScore() > c.mm_abstract_passing_score) {
                                input.get(i).addPaperAbstractEv(candidate);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(c.errorStream);
            }
        }

    }

}
