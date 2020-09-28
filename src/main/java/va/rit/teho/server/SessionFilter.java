package va.rit.teho.server;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import va.rit.teho.service.SessionService;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class SessionFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    @Resource
    private TehoSessionData tehoSession;

    public SessionFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String sessionId = httpServletRequest.getHeader("Session-Id");

        UUID sessionUUID = UUID.fromString(sessionId);
        sessionService.get(sessionUUID);
        tehoSession.saveSessionId(sessionUUID);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
