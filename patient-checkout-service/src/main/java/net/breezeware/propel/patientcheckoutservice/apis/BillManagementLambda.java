package net.breezeware.propel.patientcheckoutservice.apis;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.breezeware.propel.patientcheckoutservice.entity.PatientCheckOutEvent;

public class BillManagementLambda {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void manage(SNSEvent snsEvent) {
        snsEvent.getRecords().forEach(
                (snsRecord) -> {
                    try {
                        PatientCheckOutEvent patientCheckOutEvent = objectMapper.readValue(snsRecord.getSNS().getMessage(), PatientCheckOutEvent.class);
                        System.out.println(patientCheckOutEvent);
                    } catch (JsonProcessingException e) {
                        System.out.println("JsonProcessingException occurred, e - " + e);
                    }
                }
        );
    }
}
