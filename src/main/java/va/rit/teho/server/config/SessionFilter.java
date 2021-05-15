package va.rit.teho.server.config;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import va.rit.teho.entity.session.TehoSession;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.service.session.SessionService;

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
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String sessionId = httpServletRequest.getHeader("Session-Id");
        if (sessionId == null || sessionId.equals("null")) {
            prepareSessionErrorResponse(httpServletResponse, "Missing Session-Id header!");
        } else {
            UUID sessionUUID = UUID.fromString(sessionId);
            TehoSession session;
            try {
                session = sessionService.get(sessionUUID);
            } catch (NotFoundException e) {
                prepareSessionErrorResponse(httpServletResponse, "Session \"" + sessionUUID + "\" not found!");
                return;
            }
            tehoSession.saveSession(session);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private void prepareSessionErrorResponse(HttpServletResponse httpServletResponse, String message) throws
            IOException {
        httpServletResponse.resetBuffer();
        httpServletResponse.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        httpServletResponse.setHeader("Content-Type", "application/json");
        httpServletResponse.getOutputStream().print("{\"message\": \"" + message + "\"}");
        httpServletResponse.flushBuffer();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().contains("session");
    }
}
