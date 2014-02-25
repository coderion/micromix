package micromix.services.restgateway.spring;

public class InvoiceQuery {

    private long id;

    private String title;

    public InvoiceQuery(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public InvoiceQuery() {
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
