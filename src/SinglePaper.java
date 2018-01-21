/*
 * @version: 2017v1
 */


import gov.nih.nlm.nls.metamap.Ev;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class SinglePaper {
    String id;              //here pubmed ID
    String source;
    String title;

    String paperAbstract;
    List<Ev> pAbstractEv;

    Element metaData;

    SinglePaper(){
        id = null;
        source = null;
        title = null;
        paperAbstract = null;
        metaData = null;
        pAbstractEv = new ArrayList<>();
    }

    void addMetaData(Element metaData) {
        this.metaData = metaData.clone();
    }

    void addPaperAbstractEv(Ev ev) {
        pAbstractEv.add(ev);
    }
}
