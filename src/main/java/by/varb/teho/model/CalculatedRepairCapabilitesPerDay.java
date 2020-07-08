package by.varb.teho.model;

import javax.persistence.*;

@Entity
@Table(name = "calculated_repair_capabilities_per_day")
public class CalculatedRepairCapabilitesPerDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long baseId;
    private Long equipmentId;
    private Integer amount;
}