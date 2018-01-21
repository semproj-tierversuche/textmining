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
    }

    void addMetaData(Element metaData) {
        this.metaData = metaData.clone();
    }

    void addPaperAbstractEv(Ev ev) {
        pAbstractEv.add(ev);
    }

    void addUttPos(Position aPos) {
        pUtterancesPos.add(aPos);
    }

    List<Position> getpUtterancesPos() {
        return pUtterancesPos;
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
