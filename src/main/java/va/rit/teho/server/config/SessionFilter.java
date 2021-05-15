package va.rit.teho.server.config;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import va.rit.teho.entity.session.TehoSession;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.service.session.SessionService;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String sessionId = httpServletRequest.getHeader("Session-Id");
        TehoSession session;
        Cookie cookie = WebUtils.getCookie(httpServletRequest, "TEHO_SESSION_ID");
        if(cookie != null) {
            session = sessionService.get(UUID.fromString(cookie.getValue()));
            tehoSession.saveSession(session);
        } else if (sessionId == null || sessionId.equals("null")) {
            //prepareBadRequestResponse(httpServletResponse, "Missing Session-Id header!");
        } else {
            UUID sessionUUID = UUID.fromString(sessionId);
            try {
                session = sessionService.get(sessionUUID);
                Cookie newCookie = new Cookie("TEHO_SESSION_ID", sessionId);
                httpServletResponse.addCookie(newCookie);
                tehoSession.saveSession(session);
            } catch (NotFoundException e) {
                prepareBadRequestResponse(httpServletResponse, "Session \"" + sessionUUID + "\" not found!");
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void prepareBadRequestResponse(HttpServletResponse httpServletResponse, String message) throws
            IOException {
        httpServletResponse.resetBuffer();
        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        httpServletResponse.setHeader("Content-Type", "application/json");
        httpServletResponse.getOutputStream().print("{\"message\": \"" + message + "\"}");
        httpServletResponse.flushBuffer();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().contains("session");
    }
}
