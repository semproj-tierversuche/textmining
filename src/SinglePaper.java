/*
 * @version: 2017v1
 */


import org.jdom2.Element;

public class SinglePaper {
    String id;              //here pubmed ID
    String source;
    String title;
    String meshHeadings;
    String chemicalList;

    String keywords;
    Element metaData;

    String paperAbstract;

    SinglePaper(){
        id = null;
        source = null;
        title = null;
        meshHeadings = null;
        chemicalList = null;
        paperAbstract = null;
        keywords = null;
        metaData = null;
    }

    SinglePaper(String id, String title, String meshHeadings, String chemicalList, String paperAbstract, String keywords){
        this.id = id;
        this.title = title;
        this.meshHeadings = meshHeadings;
        this.chemicalList = chemicalList;
        this.paperAbstract = paperAbstract;
        this.keywords = keywords;
    }

    void addMetaData(Element metaData) {
        this.metaData = metaData.clone();
    }

}
