package xyz.morphia.testmodel;


import xyz.morphia.annotations.Embedded;

@Embedded
public class Translation {
    private String title;
    private String body;

    public Translation() {
    }

    public Translation(final String title, final String body) {
        this.title = title;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
