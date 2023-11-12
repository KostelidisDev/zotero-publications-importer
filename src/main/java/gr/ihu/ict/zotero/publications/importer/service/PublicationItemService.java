package gr.ihu.ict.zotero.publications.importer.service;

import gr.ihu.ict.zotero.publications.importer.model.PublicationItem;
import io.vavr.collection.List;

public interface PublicationItemService {
    List<PublicationItem> findAllPublicationItemsByUserId(String userId);
}
