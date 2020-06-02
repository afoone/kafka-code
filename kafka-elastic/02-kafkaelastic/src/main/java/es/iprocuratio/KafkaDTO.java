package es.iprocuratio;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * KafkaDTO
 */
public class KafkaDTO {

    KafkaDTO(ConsumerRecord<String, String> record) {
        this.value = record.value();
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String result = "{}";
        try {
            result = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }
    
}