package net.breezeware.propel.patientcheckoutservice.apis;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DLQEventLambda {

    private static final Logger logger = LoggerFactory.getLogger(DLQEventLambda.class);

    public void execute(SNSEvent deadLetterQueueEvent, Context context) {
        deadLetterQueueEvent.getRecords().forEach(
                (event) -> {
                    logger.info("Dead letter queue event, {}", event.toString());
                }
        );
    }

}
