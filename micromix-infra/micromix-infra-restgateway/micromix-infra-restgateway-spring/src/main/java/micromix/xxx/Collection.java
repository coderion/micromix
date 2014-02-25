package micromix.xxx;

import java.util.List;

public class Collection<T> {

    private List<T> elements;

    public Collection(List<T> elements) {
        this.elements = elements;
    }

    public Collection() {
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

}