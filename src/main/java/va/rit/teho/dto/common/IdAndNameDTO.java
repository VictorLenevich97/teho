package va.rit.teho.dto.common;

public class IdAndNameDTO {
    private Long id;
    private String name;

    public IdAndNameDTO() {
    }

    public IdAndNameDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
