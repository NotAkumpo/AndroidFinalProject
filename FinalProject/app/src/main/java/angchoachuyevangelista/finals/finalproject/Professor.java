package angchoachuyevangelista.finals.finalproject;

import io.realm.RealmObject;

public class Professor extends RealmObject {

    private String firstName;
    private String lastName;
    private String classTeaching;
    private Double overallRating;
    private String adderUuid;
    private String path;
    private Integer totalReviews = 0;
    //^^This will be used to auto update the profs rating

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getClassTeaching() {
        return classTeaching;
    }

    public void setClassTeaching(String classTeaching) {
        this.classTeaching = classTeaching;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public String getAdderUuid() {
        return adderUuid;
    }

    public void setAdderUuid(String adderUuid) {
        this.adderUuid = adderUuid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    //Use this method to update the average rating of the prof everytime a review is added
    public void updateRating(Double r){
        Double counter = overallRating * totalReviews;
        totalReviews += 1;
        overallRating = (counter + r) / totalReviews;
    }

    //Use this method to update the average rating of the prof everytime a review is removed
    public void removeRating(Double r){
        Double counter = overallRating * totalReviews;
        totalReviews -= 1;
        overallRating = (counter - r) / totalReviews;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", classTeaching='" + classTeaching + '\'' +
                ", overallRating=" + overallRating +
                ", adderUuid='" + adderUuid + '\'' +
                '}';
    }

}
