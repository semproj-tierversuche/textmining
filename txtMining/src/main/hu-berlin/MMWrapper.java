/*
 * @version: 2018v4
 */

import gov.nih.nlm.nls.metamap.*;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

/**
 *
 * calling mmServer here
 *
 * @author pLukas
 */
public class MMWrapper {

    //Metamap Config:
    private final Config c;

    MMWrapper(Config c){
        this.c = c;
    }

    /**
     * We send and run multiple titles at once to the MMserver. To save overhead. and mm is more efficient this way.
     *
     * @param input a list of single Papers
     */
    void runTitle(List<SinglePaper> input) {
        MetaMapApi mmapi = new MetaMapApiImpl();
        mmapi.setTimeout(c.mm_timeOut);
        if (c.mm_title_opt != null) {
            mmapi.setOptions(c.mm_title_opt);
        }

        StringBuilder strB = new StringBuilder();
        input.forEach(tmpPaper -> {
            if (tmpPaper.paperAbstract != null && !tmpPaper.title.isEmpty()) {
                strB.append(StringEscapeUtils.unescapeXml(tmpPaper.paperAbstract));
                strB.append("\n\n");
            }
        });

        List<Result> resultList = mmapi.processCitationsFromString(strB.toString());

        for (int i = 0, i_p = 0; i < resultList.size(); i++, i_p++) {
            Result res = resultList.get(i);
            try {
                for (Utterance utterance : res.getUtteranceList()) {
                    for (PCM pcm : utterance.getPCMList()) {
                        for (Ev candidate : pcm.getCandidateList()) {
                            if (candidate.getScore() > c.mm_title_passing_score) {
                                while (input.get(i_p).title == null) {
                                    i_p++;              //paper does not have an title, so we get the next one
                                }
                                input.get(i_p).addTitleEv(candidate);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(c.errorStream);
            }
        }
    }

    /**
     * We send and run multiple abstracts at once to the MMserver. To save overhead. and mm is more efficient this way.
     * Do not send to many at once. Otherwise the timeout may set in.
     *
     * @param input a list of single Papers
     */
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

        for (int i = 0, i_p = 0; i < resultList.size(); i++, i_p++) {   //i_p is the abstract Single Paper List
            Result res = resultList.get(i);
            try {
                for (Utterance utterance : res.getUtteranceList()) {
                    while (input.get(i_p).paperAbstract == null) {
                        i_p++;              //paper does not have an abstract, so we get the next one
                    }
                    input.get(i).addpUtterancePos(utterance.getPosition());
                    for (PCM pcm : utterance.getPCMList()) {
                        for (Ev candidate : pcm.getCandidateList()) {
                            if (candidate.getScore() > c.mm_abstract_passing_score) {
                                input.get(i_p).addPaperAbstractEv(candidate);
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
