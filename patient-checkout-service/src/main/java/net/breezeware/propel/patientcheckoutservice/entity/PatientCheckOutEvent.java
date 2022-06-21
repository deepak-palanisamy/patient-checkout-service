package net.breezeware.propel.patientcheckoutservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientCheckOutEvent {
    private String firstName;
    private String lastName;
    private int age;
}
