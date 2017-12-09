public class SinglePaper {
    String id;              //here pubmed ID
    String meshHeadings;
    String chemicalList;
    String paperAbstract;
    String keywords;

    SinglePaper(String id, String meshHeadings, String chemicalList, String paperAbstract, String keywords){
        this.id = id;
        this.meshHeadings = meshHeadings;
        this.chemicalList = chemicalList;
        this.paperAbstract = paperAbstract;
        this.keywords = keywords;
    }
}
