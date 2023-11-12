package gr.ihu.ict.zotero.publications.importer.model;

import java.util.Date;
import java.util.List;

public class PublicationItem {
    private final String title;
    private final List<String> creators;
    private final String abstractNote;
    private final Date date;
    private final String doi;
    private final String url;

    public PublicationItem(String title, List<String> creators, String abstractNote, Date date, String doi,
            String url) {
        this.title = title;
        this.creators = creators;
        this.abstractNote = abstractNote;
        this.date = date;
        this.doi = doi;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCreators() {
        return creators;
    }

    public String getAbstractNote() {
        return abstractNote;
    }

    public Date getDate() {
        return date;
    }

    public String getDoi() {
        return doi;
    }

    public String getUrl() {
        return url;
    }

}
