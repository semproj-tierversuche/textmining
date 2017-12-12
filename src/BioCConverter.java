/*
 * @version: 2017v1
 */

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.OutputStream;
import java.util.List;


public class BioCConverter {


    /**
     *
     * @param xmlDir dir to File which should be parsed
     * @return null if failed otherwise a SinglePaper
     */
    static SinglePaper bioCFileToSP(String xmlDir){
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document doc = saxBuilder.build(new File(xmlDir));
            return bioCtoSinglePaper(doc);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param bioC read in file as String
     * @return null if failed otherwise a SingePaper
     */
    static SinglePaper bioCStringToSP(String bioC){
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document doc = saxBuilder.build(bioC);
            return bioCtoSinglePaper(doc);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param doc a Document built with the saxBuilder
     */
    private static SinglePaper bioCtoSinglePaper(Document doc){

        SinglePaper sp = new SinglePaper();

        Element paperE = doc.getRootElement().getChild("document");
        List<Element> children = paperE.getChildren("passage");
        children.forEach(tmp -> {
            String keyAttr = tmp.getChild("infon").getAttributeValue("key");
            if(keyAttr.equals("type")){
                String infonVal = tmp.getChildText("infon");
                if(infonVal.equals("title")){
                    sp.title = tmp.getChildText("text");
                }else if(infonVal.equals("abstract")){
                    sp.paperAbstract = tmp.getChildText("text");
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
        doc.setRootElement(rootEle);

        Element docEle = new Element("document");
        doc.getRootElement().addContent(docEle);

        //Adding Title to the BioC XML
        Element passOne_Ele = new Element("passage");
        Element infon_Title_Ele = new Element("infon");
        infon_Title_Ele.setAttribute(new Attribute("key","type"));
        infon_Title_Ele.setText("title");
        passOne_Ele.addContent(infon_Title_Ele);
        passOne_Ele.addContent(new Element("text").setText(sp.title));

        //Adding Abstract to the BioC XML
        Element passZwo_Ele = new Element("passage");
        Element infon_Abstr_Ele = new Element("infon");
        infon_Abstr_Ele.setAttribute("key","type");
        infon_Abstr_Ele.setText("abstract");
        passZwo_Ele.addContent(infon_Abstr_Ele);
        passZwo_Ele.addContent(new Element("text").setText(sp.title));

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
