/*
 * @version: 2017v1
 */


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.List;


public class BioCReader {


    /**
     *
     * @param xmlDir
     */
    static SinglePaper bioCtoSinglePaper(String xmlDir){

        SinglePaper sp = new SinglePaper();

        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document doc = saxBuilder.build(new File(xmlDir));
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
        }catch (Exception e){
            e.fillInStackTrace();
        }

        return sp;
    }
}
