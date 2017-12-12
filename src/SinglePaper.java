/*
 * @version: 2017v1
 */

public class SinglePaper {
    String id;              //here pubmed ID
    String title;
    String meshHeadings;
    String chemicalList;
    String paperAbstract;
    String keywords;

    SinglePaper(){
        id = null;
        title = null;
        meshHeadings = null;
        chemicalList = null;
        paperAbstract = null;
        keywords = null;
    }

    SinglePaper(String id, String title, String meshHeadings, String chemicalList, String paperAbstract, String keywords){
        this.id = id;
        this.title = title;
        this.meshHeadings = meshHeadings;
        this.chemicalList = chemicalList;
        this.paperAbstract = paperAbstract;
        this.keywords = keywords;
    }

}
