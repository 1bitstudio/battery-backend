package ru.battery.main.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.battery.main.requests.Request;

@Entity
@Table(name = "battery_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @Column(name = "cycle_number")
    private Integer cycleNumber;

    @Column(name = "point_index")
    private Integer pointIndex;

    @Column(name = "voltage_in_v")
    private Double voltageInV;

    @Column(name = "current_in_a")
    private Double currentInA;

    @Column(name = "charge_capacity_in_ah")
    private Double chargeCapacityInAh;

    @Column(name = "discharge_capacity_in_ah")
    private Double dischargeCapacityInAh;

    @Column(name = "nominal_capacity_in_ah")
    private Double nominalCapacityInAh;

    @Column(name = "soc_start")
    private Double socStart;

    @Column(name = "soc_end")
    private Double socEnd;
}
