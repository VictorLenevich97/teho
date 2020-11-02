package va.rit.teho.controller.labordistribution;

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
@RequestMapping("workhours-distribution-interval")
public class WorkhoursDistributionIntervalController {
    private final LaborInputDistributionService laborInputDistributionService;

    public WorkhoursDistributionIntervalController(LaborInputDistributionService laborInputDistributionService) {
        this.laborInputDistributionService = laborInputDistributionService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<DistributionIntervalDTO>> getDistributionIntervals() {
        return ResponseEntity.ok(
                laborInputDistributionService
                        .getDistributionIntervals()
                        .stream()
                        .map(DistributionIntervalDTO::from)
                        .collect(Collectors.toList()));
    }
}
