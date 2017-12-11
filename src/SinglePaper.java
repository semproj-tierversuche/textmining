/*
 * @version: 2017v1
 */

public class SinglePaper {
    String id;              //here pubmed ID
    String title;
    String meshHeadings;
    String mm_meshHeadings;
    String chemicalList;
    String paperAbstract;
    String keywords;

    SinglePaper(){
        id = null;
        title = null;
        meshHeadings = null;
        mm_meshHeadings = null;
        chemicalList = null;
        paperAbstract = null;
        keywords = null;
    }

    SinglePaper(String id, String meshHeadings, String chemicalList, String paperAbstract, String keywords){
        this.id = id;
        this.meshHeadings = meshHeadings;
        this.chemicalList = chemicalList;
        this.paperAbstract = paperAbstract;
        this.keywords = keywords;
        
    }
}
