package va.rit.teho.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.SessionDTO;
import va.rit.teho.entity.TehoSession;
import va.rit.teho.service.SessionService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<List<SessionDTO>> listSessions() {
        return ResponseEntity.ok(sessionService.list().stream().map(SessionDTO::from).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<SessionDTO> createSession(@RequestBody SessionDTO sessionDTO) {
        TehoSession tehoSession = sessionService.create(sessionDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(SessionDTO.from(tehoSession));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Object> deleteSession(@PathVariable UUID sessionId) {
        sessionService.delete(sessionId);
        return ResponseEntity.noContent().build();
    }
}
