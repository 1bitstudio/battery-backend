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

    @Column(name = "time_in_s")
    private Double timeInS;

    @Column(name = "temperature_in_c")
    private Double temperatureInC;

    @Column(name = "internal_resistance_in_ohm")
    private Double internalResistanceInOhm;

    @Column(name = "form_factor")
    private String formFactor;

    @Column(name = "anode_composition")
    private String anodeComposition;

    @Column(name = "cathode_composition")
    private String cathodeComposition;

}
