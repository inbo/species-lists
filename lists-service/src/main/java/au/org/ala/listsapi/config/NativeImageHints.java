package au.org.ala.listsapi.config;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;

import au.org.ala.listsapi.model.AbbrListVersion1;
import au.org.ala.listsapi.model.Classification;
import au.org.ala.listsapi.model.ConstraintListItem;
import au.org.ala.listsapi.model.ErrorResponse;
import au.org.ala.listsapi.model.Facet;
import au.org.ala.listsapi.model.FacetCount;
import au.org.ala.listsapi.model.Image;
import au.org.ala.listsapi.model.IngestJob;
import au.org.ala.listsapi.model.IngestProgressItem;
import au.org.ala.listsapi.model.InputSpeciesList;
import au.org.ala.listsapi.model.KeyValue;
import au.org.ala.listsapi.model.KvpValueVersion1;
import au.org.ala.listsapi.model.MigrateProgressItem;
import au.org.ala.listsapi.model.QueryListItemVersion1;
import au.org.ala.listsapi.model.RESTSpeciesListQuery;
import au.org.ala.listsapi.model.Release;
import au.org.ala.listsapi.model.SpeciesItemVersion1;
import au.org.ala.listsapi.model.SpeciesList;
import au.org.ala.listsapi.model.SpeciesListItem;
import au.org.ala.listsapi.model.SpeciesListItemVersion1;
import au.org.ala.listsapi.model.SpeciesListIndex;
import au.org.ala.listsapi.model.SpeciesListPage;
import au.org.ala.listsapi.model.SpeciesListPageVersion1;
import au.org.ala.listsapi.model.SpeciesListVersion1;

/**
 * Registers all API DTOs for GraalVM native-image reflection.
 *
 * <p>Jackson needs reflection on getters/setters/fields to serialize and
 * deserialize these classes at runtime. In a JVM build this works
 * automatically; in a native image the metadata must be registered ahead of
 * time. Without these hints Jackson silently serializes to {@code {}}.
 */
@Configuration
@RegisterReflectionForBinding({
    AbbrListVersion1.class,
    Classification.class,
    ConstraintListItem.class,
    ErrorResponse.class,
    Facet.class,
    FacetCount.class,
    Image.class,
    IngestJob.class,
    IngestProgressItem.class,
    InputSpeciesList.class,
    KeyValue.class,
    KvpValueVersion1.class,
    MigrateProgressItem.class,
    QueryListItemVersion1.class,
    RESTSpeciesListQuery.class,
    Release.class,
    SpeciesItemVersion1.class,
    SpeciesList.class,
    SpeciesListItem.class,
    SpeciesListItemVersion1.class,
    SpeciesListIndex.class,
    SpeciesListPage.class,
    SpeciesListPageVersion1.class,
    SpeciesListVersion1.class,
    PageImpl.class,
})
public class NativeImageHints {}
