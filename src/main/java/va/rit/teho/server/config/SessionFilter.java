package va.rit.teho.server.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.service.session.SessionService;

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
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String sessionId = httpServletRequest.getHeader("Session-Id");
        if (sessionId == null) {
            prepareBadRequestResponse(httpServletResponse, "Missing Session-Id header!");
        } else {
            UUID sessionUUID = UUID.fromString(sessionId);
            try {
                sessionService.get(sessionUUID);
            } catch (NotFoundException e) {
                prepareBadRequestResponse(httpServletResponse, "Session \"" + sessionUUID + "\" not found!");
                return;
            }
            tehoSession.saveSessionId(sessionUUID);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
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
        List<String> filterPaths = Arrays.asList("/formation/repair-formation/unit/capabilities", "/labor-distribution", "/equipment-per-base");
        String path = request.getServletPath();
        boolean rfuStaffPath = path.contains("/formation/repair-formation/unit") && !path.contains("staff");
        boolean equipmentPerFormationPath = !(path.contains("/formation") && path.contains("/equipment") && (path.contains(
                "/table") || path.contains("/daily-failure")));
        return filterPaths
                .stream()
                .noneMatch(path::contains) && equipmentPerFormationPath && rfuStaffPath;
    }
}
