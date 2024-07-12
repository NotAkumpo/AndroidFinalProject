package angchoachuyevangelista.finals.finalproject;

import io.realm.RealmObject;

public class Review extends RealmObject {

    private String assessment;
    private String adderUuid;
    private String adderUsername;
    private Double overallRating;
    private String professorName;
    private String professorClass;
    private String path;

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getAdderUuid() {
        return adderUuid;
    }

    public void setAdderUuid(String adderUuid) {
        this.adderUuid = adderUuid;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getProfessorClass() {
        return professorClass;
    }

    public void setProfessorClass(String professorClass) {
        this.professorClass = professorClass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAdderUsername() {
        return adderUsername;
    }

    public void setAdderUsername(String adderUsername) {
        this.adderUsername = adderUsername;
    }

    @Override
    public String toString() {
        return "Review{" +
                "assessment='" + assessment + '\'' +
                ", adderUuid='" + adderUuid + '\'' +
                ", adderName='" + adderUsername + '\'' +
                ", overallRating=" + overallRating +
                ", professorName='" + professorName + '\'' +
                ", professorClass='" + professorClass + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
