package by.varb.teho.dto;

/**
 * DTO с кратким и полным именами.
 */
public abstract class AbstractNamedDTO {
    private String shortName;
    private String fullName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public AbstractNamedDTO(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }
}
