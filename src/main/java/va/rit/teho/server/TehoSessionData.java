package va.rit.teho.server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import va.rit.teho.service.SessionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.UUID;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

@Component
@Scope(value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
public class TehoSessionData implements Serializable {

    private static final String SESSION_ID_FIELD = "session-id";

    @Resource
    private HttpSession httpSession;

    public void saveSessionId(UUID sessionUUID) {
        httpSession.setAttribute(SESSION_ID_FIELD, sessionUUID);
    }

    public UUID getSessionId() {
        return (UUID) httpSession.getAttribute(SESSION_ID_FIELD);
    }
}
