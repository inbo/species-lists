package au.org.ala.listsapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RESTSpeciesListQuery {

    @Schema(description = "Unique identifier of the species list", hidden = true)
    private String id;
    @Schema(description = "Data resource UID associated with the list")
    private String dataResourceUid;
    @Schema(description = "Title of the species list", example = "status")
    private String title;
    @Schema(description = "Description of the species list")
    private String description;
    @Schema(description = "Type of list (see `/v2/constraints` for valid values)")
    private String listType;
    @Schema(description = "Licence applied to the list (see `/v2/constraints` for valid values)")
    private String licence;
    @Schema(description = "DOI associated with the list")
    private String doi;
    @Schema(description = "Category assigned to the list")
    private String category;
    @Schema(description = "Region covered by the list")
    private String region;
    @Schema(description = "Owner of the species list")
    private String owner;
    @Schema(description = "Whether the list is versioned", hidden = true)
    String isVersioned;
    @Schema(description = "Whether the list is authoritative", example = "false")
    String isAuthoritative;
    @Schema(description = "Whether the list is private", example = "false")
    String isPrivate;
    @Schema(description = "Whether the list is invasive", example = "false")
    String isInvasive;
    @Schema(description = "Whether the list is threatened", example = "true")
    String isThreatened;
    @Schema(description = "Whether the list appears on ALA species pages", example = "false")
    String isBIE;
    @Schema(description = "Whether the list is an SDS list", example = "false")
    String isSDS;
    @Schema(description = "Whether the list is a biosecurity list", example = "false")
    String isBiosecurity;

    public boolean isEmpty() {
        if ((id != null && !id.isEmpty())
                || (dataResourceUid != null && !dataResourceUid.isEmpty())
                || (title != null && !title.isEmpty())
                || (description != null && !description.isEmpty())
                || (listType != null && !listType.isEmpty())
                || (licence != null && !licence.isEmpty())
                || (doi != null && !doi.isEmpty())
                || (category != null && !category.isEmpty())
                || (region != null && !region.isEmpty())
                || (owner != null && !owner.isEmpty())
                || (isVersioned != null && !isVersioned.isEmpty())
                || (isAuthoritative != null && !isAuthoritative.isEmpty())
                || (isPrivate != null && !isPrivate.isEmpty())
                || (isInvasive != null && !isInvasive.isEmpty())
                || (isThreatened != null && !isThreatened.isEmpty())
                || (isBIE != null && !isBIE.isEmpty())
                || (isSDS != null && !isSDS.isEmpty())
                || (isBiosecurity != null && !isBiosecurity.isEmpty())) {
            return false;
        }
        return true;
    }

    public SpeciesList convertTo() {
        SpeciesList s = new SpeciesList();
        s.setIsPrivate(parseBoolean(removeQueryExpr(this.isPrivate)));
        s.setIsAuthoritative(parseBoolean(removeQueryExpr(this.isAuthoritative)));
        s.setIsInvasive(parseBoolean(removeQueryExpr(this.isInvasive)));
        s.setIsThreatened(parseBoolean(removeQueryExpr(this.isThreatened)));
        s.setIsBIE(parseBoolean(removeQueryExpr(this.isBIE)));
        s.setIsSDS(parseBoolean(removeQueryExpr(this.isSDS)));
        s.setIsBiosecurity(parseBoolean(removeQueryExpr(this.isBiosecurity)));
        s.setOwner(this.owner);
        s.setCategory(this.category);
        s.setRegion(this.region);
        s.setLicence(this.licence);
        s.setDoi(this.doi);
        s.setDescription(this.description);
        s.setTitle(this.title);
        s.setDataResourceUid(this.dataResourceUid);
        s.setListType(this.listType);
        s.setId(this.id);
        return s;
    }

    public static Boolean parseBoolean(String s) {
        if (s == null) {
            return null;
        }
        return Boolean.parseBoolean(s);
    }

    public static String removeQueryExpr(String s) {
        if (s != null && s.startsWith("eq:")) {
            return s.substring(3);
        }
        return s;
    }

    public RESTSpeciesListQuery copy() {
        RESTSpeciesListQuery c = new RESTSpeciesListQuery();
        c.id = this.id;
        c.dataResourceUid = this.dataResourceUid;
        c.title = this.title;
        c.description = this.description;
        c.listType = this.listType;
        c.licence = this.licence;
        c.doi = this.doi;
        c.category = this.category;
        c.region = this.region;
        c.owner = this.owner;
        c.isVersioned = this.isVersioned;
        c.isAuthoritative = this.isAuthoritative;
        c.isPrivate = this.isPrivate;
        c.isInvasive = this.isInvasive;
        c.isThreatened = this.isThreatened;
        c.isBIE = this.isBIE;
        c.isSDS = this.isSDS;
        c.isBiosecurity = this.isBiosecurity;
        return c;
    }
}
