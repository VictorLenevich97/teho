package va.rit.teho.dto;

import va.rit.teho.entity.TehoSession;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SessionDTO {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-dd");
    private final String id;
    private final String name;
    private final String created;

    public SessionDTO(String id, String name, String created) {
        this.id = id;
        this.name = name;
        this.created = created;
    }

    public static SessionDTO from(TehoSession tehoSession) {
        return new SessionDTO(tehoSession.getId().toString(),
                              tehoSession.getName(),
                              tehoSession.getCreationTimestamp()
                                         .atZone(ZoneId.systemDefault())
                                         .format(DATE_TIME_FORMATTER));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreated() {
        return created;
    }
}
