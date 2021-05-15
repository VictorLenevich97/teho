package va.rit.teho.server.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import va.rit.teho.entity.session.TehoSession;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.UUID;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

@Component
@Scope(value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
public class TehoSessionData implements Serializable {

    private static final String SESSION_FIELD = "teho-session";

    @Resource
    private transient HttpSession httpSession;

    public void saveSession(TehoSession tehoSession) {
        httpSession.setAttribute(SESSION_FIELD, tehoSession);
    }

    public UUID getSessionId() {
        return ((TehoSession) httpSession.getAttribute(SESSION_FIELD)).getId();
    }

    public TehoSession getSession() {
        return (TehoSession) httpSession.getAttribute(SESSION_FIELD);
    }
}
