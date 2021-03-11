package va.rit.teho.controller.labordistribution;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.labordistribution.DistributionIntervalDTO;
import va.rit.teho.service.labordistribution.WorkhoursDistributionIntervalService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "workhours-distribution-interval", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Интервалы трудоемкости ремонта")
public class WorkhoursDistributionIntervalController {
    private final WorkhoursDistributionIntervalService workhoursDistributionIntervalService;

    public WorkhoursDistributionIntervalController(WorkhoursDistributionIntervalService workhoursDistributionIntervalService) {
        this.workhoursDistributionIntervalService = workhoursDistributionIntervalService;
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список интервалов трудоемкости ремонта")
    public ResponseEntity<List<DistributionIntervalDTO>> getDistributionIntervals() {
        return ResponseEntity.ok(
                workhoursDistributionIntervalService
                        .listSorted()
                        .stream()
                        .map(DistributionIntervalDTO::from)
                        .collect(Collectors.toList()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Добавить интервал распределения")
    public ResponseEntity<DistributionIntervalDTO> addDistributionInterval(@RequestBody DistributionIntervalDTO interval) {
        return ResponseEntity.ok(DistributionIntervalDTO.from(
                workhoursDistributionIntervalService.add(interval.getFrom(),
                                                         interval.getTo(),
                                                         interval.getRestorationType().getId())));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Обновить интервал распределения")
    public ResponseEntity<DistributionIntervalDTO> updateDistributionInterval(@PathVariable Long id,
                                                                              @RequestBody DistributionIntervalDTO interval) {
        return ResponseEntity.ok(DistributionIntervalDTO.from(
                workhoursDistributionIntervalService.update(id,
                                                            interval.getFrom(),
                                                            interval.getTo(),
                                                            interval.getRestorationType().getId())));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @ApiOperation(value = "Удалить интервал распределения")
    public ResponseEntity<DistributionIntervalDTO> deleteDistributionInterval(@PathVariable Long id) {
        return ResponseEntity.ok(DistributionIntervalDTO.from(workhoursDistributionIntervalService.delete(id)));
    }
}
