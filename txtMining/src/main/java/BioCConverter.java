/*
 * @version: 2018v4
 */

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Position;
import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Converts BioC to the internal SinglePaper representation and back
 *
 * @author pLukas
 */
public class BioCConverter {

    private static final String BIOC_Root = "collection";
    private static final String BIOC_DOC_Root = "document";
    private static final String BIOC_passage = "passage";
    private static final String BIOC_id = "id";
    private static final String BIOC_infon = "infon";
    private static final String BIOC_key = "key";
    private static final String BIOC_type = "type";
    private static final String BIOC_title = "title";
    private static final String BIOC_abstract = "abstract";
    private static final String BIOC_metadata = "metadata";
    private static final String BIOC_TXT_tag = "text";
    private static final String BIOC_annotation = "annotation";
    private static final String BIOC_location = "location";
    private static final String BIOC_offset = "offset";
    private static final String BIOC_length = "length";
    private static final String BIOC_sentence = "sentence";
    private static final String BIOC_score = "score";
    private static final String BIOC_preferredName = "preferred_name";
    private static final String BIOC_Concept_ID = "concept_id";
    private static final String BIOC_purpose = "purpose";

    /**
     *
     * @param xmlDir dir to File which should be parsed
     * @return null if failed otherwise a SinglePaper
     */
    static List<SinglePaper> bioCFileToSP(String xmlDir) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document doc = saxBuilder.build(xmlDir);
            return bioCtoSinglePapers(doc);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param bioCIN read in file as inputStream
     * @return null if failed otherwise a SingePaper
     */
    static List<SinglePaper> bioCStreamToSP(Config c, InputStream bioCIN) {

        DocType dT = new DocType("BioC.dtd", "BioC.dtd");
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document doc = saxBuilder.build(bioCIN).setDocType(dT);
            return bioCtoSinglePapers(doc);
        }catch (Exception e){
            e.printStackTrace();
        }
        c.errorStream.println("Should have not reached this statement!\nString that couldn't be converted:" + bioCIN.toString());
        return null;
    }

    /**
     *
     * @param doc a Document built with the saxBuilder
     */
    private static List<SinglePaper> bioCtoSinglePapers(Document doc) {

        List<SinglePaper> spList = new ArrayList<>();

        List<Element> papers = doc.getRootElement().getChildren(BIOC_DOC_Root);

        for (Element paperE : papers) {
            SinglePaper sp = new SinglePaper();
            Element sourceID = paperE.getChild(BIOC_id);

            sp.id = sourceID.getText();

            List<Element> passages = paperE.getChildren(BIOC_passage);
            passages.forEach(tmp -> {
                String keyAttr = tmp.getChild(BIOC_infon).getAttributeValue(BIOC_key);
                if (keyAttr.equals(BIOC_type)) {

                    String infonVal = tmp.getChildText(BIOC_infon);

                    if (infonVal.equals(BIOC_title)) {
                        sp.title = tmp.getChildText(BIOC_TXT_tag);
                    } else if (infonVal.equals(BIOC_abstract)) {
                        sp.setAbstractOffset(tmp.getChildText(BIOC_offset));
                        sp.paperAbstract = tmp.getChildText(BIOC_TXT_tag);
                    } else if (infonVal.equals(BIOC_metadata)) {
                        sp.addMetaData(tmp);
                    }
                }
            });
            spList.add(sp);
        }

        return spList;
    }

    /**
     * Adds annotations to a given parent Element
     *
     * @param evList     Ev to get the annotations from
     * @param parent parent Element where the Annotations will be added to
     */
    private static void generateAnnoXML(List<Ev> evList, Element parent) {
        evList.forEach(ev -> {
            try {
                Element singleAnno = new Element(BIOC_annotation);

                //Annotation INFON Score:
                Element scoreInfon = new Element(BIOC_infon);
                scoreInfon.setAttribute(BIOC_key, BIOC_score);
                scoreInfon.setText(String.valueOf(ev.getScore()));
                singleAnno.addContent(scoreInfon);

                //Annotation INFON:
                Element AnnoInfon = new Element(BIOC_infon);
                AnnoInfon.setAttribute(BIOC_key, BIOC_type);
                AnnoInfon.setText(ev.getConceptName());
                singleAnno.addContent(AnnoInfon);

                //Annotation location:
                Element AnnoLocation = new Element(BIOC_location);
                Position evPos = ev.getPositionalInfo().get(0);
                AnnoLocation.setAttribute(BIOC_offset, String.valueOf(evPos.getX()));
                AnnoLocation.setAttribute(BIOC_length, String.valueOf(evPos.getY()));
                singleAnno.addContent(AnnoLocation);

                //Annotation Concept:
                Element AnnoConceptName = new Element(BIOC_infon);
                AnnoConceptName.setAttribute(BIOC_key, BIOC_preferredName);
                AnnoConceptName.setText(ev.getPreferredName());
                singleAnno.addContent(AnnoConceptName);

                //Annotation Concept ID:
                Element AnnoConceptID = new Element(BIOC_infon);
                AnnoConceptID.setAttribute(BIOC_key, BIOC_Concept_ID);
                AnnoConceptID.setText(ev.getConceptId());
                singleAnno.addContent(AnnoConceptID);

                //adding Annotation to parent
                parent.addContent(singleAnno);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Divides a Abstract into utterances --- information from MM
     *
     * @param sp         Singlepaper to take the Utterances from
     * @param parent     parent Element where the Abstract will be added to
     * @param baseOffset the offset from the Title
     */
    private static void generateUtterAbstr(SinglePaper sp, Element parent, int baseOffset, VersuchszweckChecker vzc) {
        StringBuilder tmpAbstr = new StringBuilder(sp.paperAbstract);
        List<Position> positions = sp.getpUtterancesPos();

        final boolean[] gotUsage = new boolean[1];  //boolean is used in lambda, so it must be final
        gotUsage[0] = false;    // the array provides an effectively final

        positions.forEach(pos -> {
            int offsetTotalINT = baseOffset + pos.getX();   //pos.getX() ist der Offset im Abstract
            String offsetTotal = Integer.toString(offsetTotalINT);
            //setting sentence element and the offset value
            Element sentence = new Element(BIOC_sentence);
            sentence.addContent(new Element(BIOC_offset).setText(offsetTotal));

            //adding the text
            int endUttPos = pos.getX() + pos.getY();
            String singleUtt = tmpAbstr.substring(pos.getX(), endUttPos);

            //checking if the text describes the experimental purpose
            if( !gotUsage[0] && vzc.isVersuchszweck(singleUtt)){
                sentence.addContent(new Element(BIOC_purpose).setText("true"));
                gotUsage[0] = true;
            }
            sentence.addContent(new Element(BIOC_TXT_tag).setText(singleUtt));

            //adding everything to the parent
            parent.addContent(sentence);
        });
    }

    /**
     * builds a BioC from a SinglePaper and writes it to a OutputStream in BioC XML format
     *
     * @param spList All the single Papers
     * @param out OutputStream where the content of the SinglePaper will be written into
     */
    static void spToBioCStream(Config c, List<SinglePaper> spList, OutputStream out, VersuchszweckChecker vzc) {

        Element rootEle = new Element(BIOC_Root);
        Document doc = new Document(rootEle);

        for (SinglePaper sp : spList) {
            Element docEle = new Element(BIOC_DOC_Root);
            doc.getRootElement().addContent(docEle);

            //Adding ID to the BioC XML
            if (sp.id != null) {
                docEle.addContent(new Element(BIOC_id).setText(sp.id));
            }

            //Adding Title to the BioC XML
            if (sp.title != null) {
                Element passOne_Ele = new Element(BIOC_passage);
                Element infon_Title_Ele = new Element(BIOC_infon);
                infon_Title_Ele.setAttribute(new Attribute(BIOC_key, BIOC_type));
                infon_Title_Ele.setText(BIOC_title);
                passOne_Ele.addContent(infon_Title_Ele);
                passOne_Ele.addContent(new Element(BIOC_TXT_tag).setText(sp.title));

                if (c.mm_title) {
                    generateAnnoXML(sp.pTitleEv, passOne_Ele);
                }

                docEle.addContent(passOne_Ele);
            }

            //Adding Abstract to the BioC XML
            if (sp.paperAbstract != null) {
                Element passZwo_Ele = new Element(BIOC_passage);
                Element infon_Abstr_Ele = new Element(BIOC_infon);
                infon_Abstr_Ele.setAttribute(BIOC_key, BIOC_type);
                infon_Abstr_Ele.setText(BIOC_abstract);
                passZwo_Ele.addContent(infon_Abstr_Ele);
                passZwo_Ele.addContent(new Element(BIOC_offset).setText(sp.getAbstractOffset()));


                if (c.mm_abstract_utterance && !sp.utterancePosIsEmpty()) {
                    generateUtterAbstr(sp, passZwo_Ele, sp.getAbstractOffsetINT(), vzc);
                } else {
                    passZwo_Ele.addContent(new Element(BIOC_TXT_tag).setText(sp.paperAbstract));
                }

                if (c.mm_abstract_annotations) {
                    generateAnnoXML(sp.pAbstractEv, passZwo_Ele);
                }

                docEle.addContent(passZwo_Ele);
            }

            //Adding Metadata to the BioC XML
            if (sp.metaData != null) {
                docEle.addContent(sp.metaData);
            }
        }

        // new XMLOutputter().output(doc, System.out);
        XMLOutputter xmlOutput = new XMLOutputter();

        // display nice nice
        xmlOutput.setFormat(Format.getPrettyFormat());

        try {
            xmlOutput.output(doc, out);
        } catch (IOException e) {
            e.printStackTrace(c.errorStream);
        }

    }
}
