package gr.ihu.ict.zotero.publications.importer.config;

public class DefaultConfig implements Config{

    @Override
    public String getUrl() {
        return "https://api.zotero.org";
    }
    
}
