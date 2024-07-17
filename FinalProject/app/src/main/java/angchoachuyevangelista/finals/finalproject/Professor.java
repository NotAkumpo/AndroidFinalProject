package angchoachuyevangelista.finals.finalproject;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Professor extends RealmObject {

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    //Use this method to update the average rating of the prof everytime a review is added
    public void updateRating(Double r){
        Double counter;
        if(overallRating != null){
            counter = overallRating * totalReviews;
        } else {
            counter = 0.0;
        }
        totalReviews += 1;
        overallRating = (counter + r) / totalReviews;
        overallRating = Math.round(overallRating * 100.0) / 100.0;
    }

    //Use this method to update the average rating of the prof everytime a review is removed
    public void removeRating(Double r){
        Double counter;
        if(overallRating != null){
            counter = overallRating * totalReviews;
        } else {
            counter = 0.0;
        }
        totalReviews -= 1;
        overallRating = (counter - r) / totalReviews;
        overallRating = Math.round(overallRating * 100.0) / 100.0;
        if(totalReviews == 0){
            overallRating = null;
        }
    }

    public void editRating(Double or, Double nr){
        Double counter = overallRating * totalReviews - or + nr;
        overallRating = counter / totalReviews;
        overallRating = Math.round(overallRating * 100.0) / 100.0;
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
