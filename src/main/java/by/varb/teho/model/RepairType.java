package by.varb.teho.model;

import javax.persistence.*;

@Entity
@Table(name = "repair_type")
public class RepairType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
