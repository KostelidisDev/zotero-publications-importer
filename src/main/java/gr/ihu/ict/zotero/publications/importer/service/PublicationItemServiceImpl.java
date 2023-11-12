package gr.ihu.ict.zotero.publications.importer.service;

import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import gr.ihu.ict.zotero.publications.importer.config.Config;
import gr.ihu.ict.zotero.publications.importer.model.PublicationItem;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

public class PublicationItemServiceImpl implements PublicationItemService {

    private final Config config;

    public PublicationItemServiceImpl(final Config config) {
        this.config = config;
    }

    @Override
    public List<PublicationItem> findAllPublicationItemsByUserId(String userId) {
        return Try.success(config)
                .map(_config -> _config.getUrl())
                .map(_baseUrl -> String.format("%s/users/%s/publications/items", _baseUrl, userId))
                .flatMap(url -> Try.of(() -> new URL(url)))
                .flatMap(url -> Try.of(() -> IOUtils.toString(url, Charset.defaultCharset())))
                .map(JSONArray::new)
                .map(JSONArray::toList)
                .map(List::ofAll)
                .flatMap(publications -> Try.sequence(
                        publications
                                .flatMap(publication -> Try.of(() -> (Map<String, Object>) publication))
                                .map(publication -> safeResolveMapValue("data", publication,
                                        new HashMap<String, String>()))
                                .flatMap(publicationData -> Try.of(() -> (Map<String, Object>) publicationData))
                                .map(publicationData -> {
                                    return Try.of(() -> {
                                        final String title = (String) safeResolveMapValue("title", publicationData,
                                                "Untitled");

                                        ArrayList<HashMap<String, Object>> safeResolveMapValue = (ArrayList<HashMap<String, Object>>) safeResolveMapValue(
                                                "creators", publicationData, new ArrayList<HashMap<String, Object>>());

                                        final java.util.List<String> creators = List.ofAll(safeResolveMapValue)
                                                .map(value -> {
                                                    Map<String, Object> creatorData = (Map<String, Object>) value;
                                                    final String firstName = (String) safeResolveMapValue("firstName",
                                                            creatorData, "Unknown First Name");
                                                    final String lastName = (String) safeResolveMapValue("lastName",
                                                            creatorData, "Unknown Last Name");
                                                    final String creatorType = (String) safeResolveMapValue(
                                                            "creatorType", creatorData, "Unknown Creator Type");
                                                    return String.format("%s %s", firstName, lastName);
                                                })
                                                .toJavaList();

                                        final String abstractNote = (String) safeResolveMapValue("abstractNote",
                                                publicationData, "");
                                        final String date = (String) safeResolveMapValue("date", publicationData, "");
                                        final Date parsedDate = parseZoteroDate(date);
                                        final String doi = (String) safeResolveMapValue("DOI", publicationData, "");
                                        final String url = (String) safeResolveMapValue("url", publicationData, "");

                                        return new PublicationItem(
                                                title,
                                                creators,
                                                abstractNote,
                                                parsedDate,
                                                doi,
                                                url);
                                    });
                                }))
                        .map(Seq<PublicationItem>::toList))
                .get();
    }

    private Date parseZoteroDate(String date) {
        List<SimpleDateFormat> supportedFormats = List.of(
                new SimpleDateFormat("MM d, yyyy"),
                new SimpleDateFormat("MM, yyyy"),
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("yyyy-MM")
                );

        List<Date> possibleValidDates = supportedFormats
                .map(format -> Try.of(() -> format.parse(date)))
                .filter(parseTry -> parseTry.isSuccess())
                .map(Try::get);

        if(possibleValidDates.isEmpty()) {
            return new Date();
        }

        return possibleValidDates.get(0);
    }

    private <T> T safeResolveMapValue(final String key, final Map<String, T> map, final T defaultValue) {
        return Try.of(() -> map.get(key))
                .map(value -> {
                    if (Objects.isNull(value)) {
                        return defaultValue;
                    }

                    return value;
                })
                .getOrElse(defaultValue);
    }

}
