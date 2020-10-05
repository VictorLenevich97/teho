package va.rit.teho.server;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import va.rit.teho.service.SessionService;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
        if (sessionId == null) {
            httpServletResponse.resetBuffer();
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpServletResponse.setHeader("Content-Type", "application/json");
            httpServletResponse.getOutputStream().print("{\"message\": \"Missing Session-Id header!\"}");
            httpServletResponse.flushBuffer();
        } else {
            UUID sessionUUID = UUID.fromString(sessionId);
            sessionService.get(sessionUUID);
            tehoSession.saveSessionId(sessionUUID);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> filterPaths = Arrays.asList("/repair-capabilities", "/labor-distribution");
        String path = request.getServletPath();
        return filterPaths.stream().noneMatch(path::contains) && !(path.contains("/repair-station") && path.contains(
                "/subtype"));
    }
}
