/*
 * @version: 2018v4
 */
import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Position;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * internal representation
 * @author pLukas
 */
public class SinglePaper {
    String id;              //here pubmed ID
    String title;
    List<Ev> pTitleEv;

    private String AbstractOffset;
    String paperAbstract;
    List<Ev> pAbstractEv;
    private List<Position> pUtterancesPos;

    Element metaData;

    SinglePaper(){
        id = null;
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

    /**
     * helper method for cleanEvLists
     *
     * @param evList List<Ev> to clean up
     */
    private void cleanList(List<Ev> evList) {

        for (int i_lOne = 0, i_lTwo = 1; i_lTwo < evList.size(); ) {
            Ev evOne = evList.get(i_lOne);
            Ev evTwo = evList.get(i_lTwo);
            try {
                Position posOne = evOne.getPositionalInfo().get(0);
                Position posTwo = evTwo.getPositionalInfo().get(0);

                if (posOne.getX() == posTwo.getX() && posOne.getY() == posTwo.getY()) {   // checking if Ev cover the same word
                    if (evOne.getScore() > evTwo.getScore()) {    //removing the one which is smaller
                        evList.remove(evTwo);
                    } else if (evOne.getScore() < evTwo.getScore()) {
                        evList.remove(evOne);
                    } else {     // score is equal -- lookahead ,- check if the next element matches the current two
                        if (1 + i_lTwo < evList.size()) {
                            Ev thirdElement = evList.get(1 + i_lTwo);
                            Position posThree = thirdElement.getPositionalInfo().get(0);
                            if (posThree.getX() == posTwo.getX() && posThree.getY() == posTwo.getY()) {
                                if (thirdElement.getScore() > evTwo.getScore()) {
                                    evList.remove(i_lOne);
                                    evList.remove(i_lTwo);
                                } else {
                                    i_lOne++;
                                    i_lTwo++;
                                }
                            } else {
                                i_lOne++;
                                i_lTwo++;
                            }
                        } else {
                            i_lOne++;
                            i_lTwo++;
                        }

                    }
                } else {
                    i_lOne++;
                    i_lTwo++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * if multiple EVs point to the same Word the one with the lower score will be cleaned out
     */
    void cleanEvLists() {
        if (pTitleEv != null) {
            cleanList(pTitleEv);
        }
        if (pAbstractEv != null) {
            cleanList(pAbstractEv);
        }
    }
}
