package micromix.services.restgateway.spring;

public class Invoice {

    private long id;

    private String title;

    public Invoice(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Invoice() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}