package net.breezeware.propel.patientcheckoutservice.apis;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.breezeware.propel.patientcheckoutservice.entity.PatientCheckOutEvent;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class PatientCheckOutLambda {

    //    private static final Logger logger = LogManager.getLogger(PatientCheckOutEvent.class);
    private static final Logger logger = LoggerFactory.getLogger(PatientCheckOutEvent.class);

    private static final String PATIENT_CHECKOUT_TOPIC = System.getenv("PATIENT_CHECKOUT_TOPIC");

    private final AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AmazonSNS amazonSNS = AmazonSNSClientBuilder.defaultClient();

    public void checkout(S3Event s3Event, Context context) {
//        LambdaLogger logger = context.getLogger();

        s3Event.getRecords().forEach(
                (record) -> {
                    S3ObjectInputStream s3InputStream = amazonS3.getObject(
                                    record.getS3()
                                            .getBucket()
                                            .getName(),
                                    record.getS3()
                                            .getObject()
                                            .getKey()
                            ).
                            getObjectContent();

                    List<PatientCheckOutEvent> patientCheckOutEvents = null;
                    try {
                        logger.info("Reading data from S3 object");
                        patientCheckOutEvents = Arrays.asList(objectMapper.readValue(s3InputStream, PatientCheckOutEvent[].class));
                        logger.info("PatientCheckOut events - {}", patientCheckOutEvents);
                        s3InputStream.close();
                    } catch (IOException e) {
//                        e.printStackTrace();
                        e.printStackTrace(new PrintWriter(new StringWriter()));
                        logger.error("Object conversion exception, e - {}", e.toString());
                        new RuntimeException("Exception occurred during object conversion, error - " + e.toString());
                    }

                    publishEventsToSnsTopic(patientCheckOutEvents);

                }
        );
    }

    private void publishEventsToSnsTopic(List<PatientCheckOutEvent> patientCheckOutEvents) {
//        LambdaLogger logger = context.getLogger();

        patientCheckOutEvents.forEach(
                (patientCheckOutEvent) -> {
                    try {
                        logger.info("Publishing event to SNS, event - {}", patientCheckOutEvent);
                        PublishResult publishResult = amazonSNS.publish(PATIENT_CHECKOUT_TOPIC, objectMapper.writeValueAsString(patientCheckOutEvent));
                        logger.info("Published event to SNS, result - {}", publishResult);
                    } catch (JsonProcessingException e) {
                        logger.error("JsonProcessingException occurred, e - {}", e);
                    }
                }
        );
    }
}
