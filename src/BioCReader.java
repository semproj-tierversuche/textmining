/*
 * @version: 2017v1
 */


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BioCReader {

    private static List<String> getTitleAbstract(Document doc, XPath xpath, String type) {
        List<String> list = new ArrayList<>();
        try {
            XPathExpression expr = xpath.compile("/collection/document/passage/infon[@key='type']");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for(int i=0; i< nodes.getLength();i++){
                list.add( nodes.item(i).getNextSibling().getNextSibling().getTextContent() );
            }
            int d =0;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     *
     * @param xmlDir
     */
    static SinglePaper bioCtoSinglePaper(String xmlDir){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlDir);

            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();

            List<String> names = getTitleAbstract(doc, xpath, "title");
            System.out.println("Employees with 'age>30' are:" + Arrays.toString(names.toArray()));


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return new SinglePaper("","","","","");
    }
}
