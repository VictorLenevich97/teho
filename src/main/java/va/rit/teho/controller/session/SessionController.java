package va.rit.teho.controller.session;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.session.SessionDTO;
import va.rit.teho.entity.session.TehoSession;
import va.rit.teho.service.session.SessionService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "session", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Сессии")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    @ApiOperation(value = "Получить список сессий")
    public ResponseEntity<List<SessionDTO>> listSessions() {
        return ResponseEntity.ok(sessionService.list().stream().map(SessionDTO::from).collect(Collectors.toList()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Создать сессию")
    public ResponseEntity<SessionDTO> createSession(@ApiParam(value = "Данные о сессии", required = true) @Valid @RequestBody SessionDTO sessionDTO) {
        TehoSession tehoSession = sessionService.create(sessionDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(SessionDTO.from(tehoSession));
    }

    @PutMapping(path = "/{sessionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Скопировать сессию")
    public ResponseEntity<SessionDTO> copySession(@ApiParam(value = "Ключ оригинальной сессии", required = true) @PathVariable UUID sessionId,
                                                  @ApiParam(value = "Данные о новой сессии", required = true) @Valid @RequestBody SessionDTO sessionDTO) {
        TehoSession tehoSession = sessionService.copy(sessionId, sessionDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(SessionDTO.from(tehoSession));
    }

    @DeleteMapping("/{sessionId}")
    @ApiOperation(value = "Удалить сессию и все связанные с ней данные")
    public ResponseEntity<Object> deleteSession(@ApiParam(value = "Ключ сессии", required = true) @PathVariable UUID sessionId) {
        sessionService.get(sessionId);
        sessionService.delete(sessionId);
        return ResponseEntity.noContent().build();
    }
}
