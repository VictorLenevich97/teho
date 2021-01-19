package va.rit.teho.controller.labordistribution;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import va.rit.teho.dto.labordistribution.DistributionIntervalDTO;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "workhours-distribution-interval", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Интервалы трудоемкости ремонта")
public class WorkhoursDistributionIntervalController {
    private final LaborInputDistributionService laborInputDistributionService;

    public WorkhoursDistributionIntervalController(LaborInputDistributionService laborInputDistributionService) {
        this.laborInputDistributionService = laborInputDistributionService;
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список интервалов трудоемкости ремонта")
    public ResponseEntity<List<DistributionIntervalDTO>> getDistributionIntervals() {
        return ResponseEntity.ok(
                laborInputDistributionService
                        .listDistributionIntervals()
                        .stream()
                        .map(DistributionIntervalDTO::from)
                        .collect(Collectors.toList()));
    }
}
