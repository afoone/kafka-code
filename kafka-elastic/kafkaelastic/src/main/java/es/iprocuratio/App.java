package es.iprocuratio;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka + elastic
 *
 */
public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    private static KafkaConsumer<String, String> getConsumer(String topic) {

        final String boostrapServer = "127.0.0.1:9092";
        final String groupId = "elastic";

        final Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }

    public static void runKafkaConsumer() throws IOException {
        final String topic = "logs";
        KafkaConsumer<String, String> consumer = getConsumer(topic);
        final RestHighLevelClient client = createClient();
        IndexRequest request ;
        IndexResponse indexResponse;

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));


            for (ConsumerRecord<String, String> record : records) {

                KafkaDTO result = new KafkaDTO(record);
                request = new IndexRequest(record.topic())
                    .source(result.toString(), XContentType.JSON);
                indexResponse = client.index(request, RequestOptions.DEFAULT);

                logger.info("Key: " + record.key() + " Value " + record.value()+ " id: "+ indexResponse.getId() +" result "+ result.toString());
                logger.info("Partition: " + record.partition() + " Offset: " + record.offset());
            }
        }
    }

    public static RestHighLevelClient createClient() {
        final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"), new HttpHost("localhost", 9201, "http")));
        return client;
    }

    private static void testElastic() throws IOException {

        final RestHighLevelClient client = createClient();

        logger.info("Cliente creado", App.class);

        final String jsonString = "{" + "\"user\":\"kimchy2\"," + "\"postDate\":\"2019-01-30\","
                + "\"message\":\"trying out Elasticsearch\"" + "}";

        // Insertar datos de prueba
        final IndexRequest request = new IndexRequest("logs").source(jsonString, XContentType.JSON);

        final IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        final String _id = indexResponse.getId();
        logger.info("ID: " + _id);
    }

    public static void main(final String[] args) throws IOException {

        // testElastic();
        runKafkaConsumer();
        return;
    }
}
