/*
 * @version: 2017v1
 */

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

    private static final String BIOC_Root = "document";
    private static final String BIOC_child_passage = "passage";
    private static final String BIOC_child_source = "source";
    private static final String BIOC_child_source_ID = "id";
    private static final String BIOC_infon = "infon";
    private static final String BIOC_infon_Attr = "key";
    private static final String BIOC_infon_Attr_Val_type = "type";
    private static final String BIOC_infon_Val_1 = "title";
    private static final String BIOC_infon_Val_2 = "abstract";
    private static final String BIOC_infon_Val_3 = "metadata";
    private static final String BIOC_infon_Val_4 = "MeshHeading";
    private static final String BIOC_infon_Val_5 = "Chemical";
    private static final String BIOC_infon_Val_6 = "Qualifier";   //subset of MeshHeading
    private static final String BIOC_infon_Val_7 = "Descriptor";  //subset of MeshHeading
    private static final String BIOC_infon_Val_8 = "NameOfSubstance"; //subset of Chemical
    private static final String BIOC_infon_Val_9 = "Keyword";
    private static final String BIOC_TXT_tag = "text";
    private static final String BIOC_children_sentence = "sentence";

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

        Element paperE = doc.getRootElement().getChild(BIOC_Root);
        Element source = doc.getRootElement().getChild(BIOC_child_source);

        Element sourceID = paperE.getChild(BIOC_child_source_ID);

        sp.source = source.getText();
        sp.id = sourceID.getText();

        List<Element> passages = paperE.getChildren(BIOC_child_passage);
        passages.forEach(tmp -> {
            String keyAttr = tmp.getChild(BIOC_infon).getAttributeValue(BIOC_infon_Attr);
            if (keyAttr.equals(BIOC_infon_Attr_Val_type)) {

                String infonVal = tmp.getChildText(BIOC_infon);

                if (infonVal.equals(BIOC_infon_Val_1)) {
                    sp.title = tmp.getChildText(BIOC_TXT_tag);
                } else if (infonVal.equals(BIOC_infon_Val_2)) {
                    sp.paperAbstract = tmp.getChildText(BIOC_TXT_tag);
                } else if (infonVal.equals(BIOC_infon_Val_3)) {
                    sp.addMetaData(tmp);
                }
            }
        });

        return sp;
    }

    /**
     * builds a BioC from a SinglePaper and writes it to a OutputStream in BioC XML format
     *
     * @param sp A SinglePaper
     * @param out OutputStream where the content of the SinglePaper will be written into
     */
    static void spToBioCStream(SinglePaper sp, OutputStream out){

        Element rootEle = new Element("collection");
        Document doc = new Document(rootEle);

        Element docEle = new Element("document");
        doc.getRootElement().addContent(docEle);

        //Adding ID to the BioC XML
        if (sp.id != null) {
            docEle.addContent(new Element("id").setText(sp.id));
        }

        //Adding Title to the BioC XML
        if(sp.title != null){
            Element passOne_Ele = new Element("passage");
            Element infon_Title_Ele = new Element("infon");
            infon_Title_Ele.setAttribute(new Attribute("key","type"));
            infon_Title_Ele.setText("title");
            passOne_Ele.addContent(infon_Title_Ele);
            passOne_Ele.addContent(new Element("text").setText(sp.title));
            docEle.addContent(passOne_Ele);
        }

        //Adding Abstract to the BioC XML
        if(sp.paperAbstract != null){
            Element passZwo_Ele = new Element("passage");
            Element infon_Abstr_Ele = new Element("infon");
            infon_Abstr_Ele.setAttribute("key","type");
            infon_Abstr_Ele.setText("abstract");
            passZwo_Ele.addContent(infon_Abstr_Ele);
            passZwo_Ele.addContent(new Element("text").setText(sp.paperAbstract));
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
