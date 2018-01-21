/*
 * @version: 2018v2
 */

import gov.nih.nlm.nls.metamap.Position;
import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class BioCConverter {

    private static final String BIOC_Root = "collection";
    private static final String BIOC_DOC_Root = "document";
    private static final String BIOC_passage = "passage";
    private static final String BIOC_source = "source";
    private static final String BIOC_id = "id";
    private static final String BIOC_infon = "infon";
    private static final String BIOC_key = "key";
    private static final String BIOC_type = "type";
    private static final String BIOC_title = "title";
    private static final String BIOC_abstract = "abstract";
    private static final String BIOC_metadata = "metadata";
    private static final String BIOC_TXT_tag = "text";
    private static final String BIOC_annotaion = "annotation";
    private static final String BIOC_location = "location";
    private static final String BIOC_offset = "offset";
    private static final String BIOC_length = "length";
    private static final String BIOC_sentence = "sentence";

    /**
     *
     * @param xmlDir dir to File which should be parsed
     * @return null if failed otherwise a SinglePaper
     */
    static SinglePaper bioCFileToSP(String xmlDir){
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document doc = saxBuilder.build(xmlDir);
            return bioCtoSinglePaper(doc);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param bioCIN read in file as inputStream
     * @return null if failed otherwise a SingePaper
     */
    static SinglePaper bioCStreamToSP(InputStream bioCIN){
        DocType dT = new DocType("BioC.dtd","BioC.dtd","BioC.dtd");
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document doc = saxBuilder.build(bioCIN).setDocType(dT);
            return bioCtoSinglePaper(doc);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.err.println("Should have not reached this statement!\nString that couldn't be converted:"+bioCIN.toString());
        return null;
    }

    /**
     *
     * @param doc a Document built with the saxBuilder
     */
    private static SinglePaper bioCtoSinglePaper(Document doc){

        SinglePaper sp = new SinglePaper();

        Element paperE = doc.getRootElement().getChild(BIOC_DOC_Root);
        Element source = doc.getRootElement().getChild(BIOC_source);

        Element sourceID = paperE.getChild(BIOC_id);

        sp.source = source.getText();
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

        return sp;
    }

    /**
     * Adds annotations to a given parent Element
     *
     * @param sp     Singlepaper to take the annotations from
     * @param parent parent Element where the Annotations will be added to
     */
    private static void generateAnnoXML(SinglePaper sp, Element parent) {
        sp.pAbstractEv.forEach(ev -> {
            try {
                Element singleAnno = new Element(BIOC_annotaion);
                //Annotation INFON:
                Element AnnoInfon = new Element(BIOC_infon);
                AnnoInfon.setAttribute(BIOC_key, BIOC_type);
                AnnoInfon.setText(ev.getPreferredName());
                singleAnno.addContent(AnnoInfon);

                //Annotation location:
                Element AnnoLocation = new Element(BIOC_location);
                Position evPos = ev.getPositionalInfo().get(0);
                AnnoLocation.setAttribute(BIOC_offset, "'" + evPos.getX() + "'");
                AnnoLocation.setAttribute(BIOC_length, "'" + evPos.getY() + "'");
                singleAnno.addContent(AnnoLocation);

                //Annotation Concept:
                Element AnnoConcept = new Element(BIOC_infon);
                AnnoConcept.setAttribute(BIOC_key, ev.getConceptName());
                AnnoConcept.setText(ev.getConceptId());
                singleAnno.addContent(AnnoConcept);

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
    private static void generateUtterAbstr(SinglePaper sp, Element parent, int baseOffset) {
        StringBuilder tmpAbstr = new StringBuilder(sp.paperAbstract);
        List<Position> positions = sp.getpUtterancesPos();

        positions.forEach(pos -> {
            int offsetTotalINT = baseOffset + pos.getX();   //pos.getX() ist der Offset im Abstract
            String offsetTotal = Integer.toString(offsetTotalINT);
            //setting sentence element and the offset value
            Element sentence = new Element(BIOC_sentence);
            sentence.addContent(new Element(BIOC_offset).setText(offsetTotal));

            //adding the text
            int endUttPos = pos.getX() + pos.getY();
            String singleUtt = tmpAbstr.substring(pos.getX(), endUttPos);
            sentence.addContent(new Element(BIOC_TXT_tag).setText(singleUtt));

            //adding everything to the parent
            parent.addContent(sentence);
        });
    }

    /**
     * builds a BioC from a SinglePaper and writes it to a OutputStream in BioC XML format
     *
     * @param sp A SinglePaper
     * @param out OutputStream where the content of the SinglePaper will be written into
     */
    static void spToBioCStream(Config c, SinglePaper sp, OutputStream out) {

        Element rootEle = new Element(BIOC_Root);
        Document doc = new Document(rootEle);

        Element docEle = new Element(BIOC_DOC_Root);
        doc.getRootElement().addContent(docEle);

        //Adding ID to the BioC XML
        if (sp.id != null) {
            docEle.addContent(new Element(BIOC_id).setText(sp.id));
        }

        //Adding Title to the BioC XML
        if(sp.title != null){
            Element passOne_Ele = new Element(BIOC_passage);
            Element infon_Title_Ele = new Element(BIOC_infon);
            infon_Title_Ele.setAttribute(new Attribute(BIOC_key, BIOC_type));
            infon_Title_Ele.setText(BIOC_title);
            passOne_Ele.addContent(infon_Title_Ele);
            passOne_Ele.addContent(new Element(BIOC_TXT_tag).setText(sp.title));
            docEle.addContent(passOne_Ele);
        }

        //Adding Abstract to the BioC XML
        if(sp.paperAbstract != null){
            Element passZwo_Ele = new Element(BIOC_passage);
            Element infon_Abstr_Ele = new Element(BIOC_infon);
            infon_Abstr_Ele.setAttribute(BIOC_key, BIOC_type);
            infon_Abstr_Ele.setText(BIOC_abstract);
            passZwo_Ele.addContent(infon_Abstr_Ele);
            passZwo_Ele.addContent(new Element(BIOC_offset).setText(sp.getAbstractOffset()));


            if (c.mm_abstract_utterance) {
                generateUtterAbstr(sp, passZwo_Ele, sp.getAbstractOffsetINT());
            } else {
                passZwo_Ele.addContent(new Element(BIOC_TXT_tag).setText(sp.paperAbstract));
            }

            if (c.mm_abstract_annotations) {
                generateAnnoXML(sp, passZwo_Ele);
            }

            docEle.addContent(passZwo_Ele);
        }

        //Adding Metadata to the BioC XML
        if (sp.metaData != null) {
            docEle.addContent(sp.metaData);
        }


        // new XMLOutputter().output(doc, System.out);
        XMLOutputter xmlOutput = new XMLOutputter();

        // display nice nice
        xmlOutput.setFormat(Format.getPrettyFormat());

        try {
            xmlOutput.output(doc, out);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
