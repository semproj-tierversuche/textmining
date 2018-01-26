/*
 * @version: 2018v2
 */


import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Position;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class SinglePaper {
    String id;              //here pubmed ID
    String source;
    String title;
    List<Ev> pTitleEv;

    private String AbstractOffset;
    String paperAbstract;
    List<Ev> pAbstractEv;
    private List<Position> pUtterancesPos;

    Element metaData;

    SinglePaper(){
        id = null;
        source = null;
        title = null;
        AbstractOffset = null;
        paperAbstract = null;
        metaData = null;
        pAbstractEv = new ArrayList<>();
        pUtterancesPos = new ArrayList<>();
        pTitleEv = new ArrayList<>();
    }

    void addTitleEv(Ev ev) {
        pTitleEv.add(ev);
    }

    void addMetaData(Element metaData) {
        this.metaData = metaData.clone();
    }

    void addPaperAbstractEv(Ev ev) {
        pAbstractEv.add(ev);
    }

    List<Position> getpUtterancesPos() {
        return pUtterancesPos;
    }

    void addpUtterancePos(Position position) {
        pUtterancesPos.add(position);
    }

    boolean utterancePosIsEmpty() {
        return pUtterancesPos.isEmpty();
    }

    int getAbstractOffsetINT() {
        return Integer.decode(AbstractOffset);
    }

    String getAbstractOffset() {
        return AbstractOffset;
    }

    void setAbstractOffset(String abOff) {
        AbstractOffset = abOff;
    }

}
