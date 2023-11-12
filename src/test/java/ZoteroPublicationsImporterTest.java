import java.util.List;

import org.junit.Test;

import gr.ihu.ict.zotero.publications.importer.config.Config;
import gr.ihu.ict.zotero.publications.importer.config.DefaultConfig;
import gr.ihu.ict.zotero.publications.importer.model.PublicationItem;
import gr.ihu.ict.zotero.publications.importer.service.PublicationItemService;
import gr.ihu.ict.zotero.publications.importer.service.PublicationItemServiceImpl;

public class ZoteroPublicationsImporterTest {
    @Test
    public void test() {
        Config config = new DefaultConfig();
        PublicationItemService publicationItemService = new PublicationItemServiceImpl(config);
        List<PublicationItem> findAllPublicationItemsByUserId = publicationItemService.findAllPublicationItemsByUserId("12296221").toJavaList();
        System.out.println(findAllPublicationItemsByUserId);
    }
}
