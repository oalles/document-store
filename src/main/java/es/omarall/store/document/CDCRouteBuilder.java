package es.omarall.store.document;

import io.debezium.data.Envelope;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.kafka.connect.data.Struct;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

@Component
public class CDCRouteBuilder extends RouteBuilder {

    private final Predicate isCreateEvent =
            header(DebeziumConstants.HEADER_OPERATION).isEqualTo(
                    constant(Envelope.Operation.CREATE.code()));

    private final MetadataExtractor metadataExtractor = new MetadataExtractor();

    @Override
    public void configure() throws Exception {

        from("debezium-postgres:localPG?offsetStorageFileName=/tmp/offset.dat")
                .routeId(this.getClass().getName() + ".documents")
                .log(LoggingLevel.DEBUG, "Incoming message with headers ${headers}")
                .choice()
                    .when(isCreateEvent)
                        .process(exchange -> {
                            // Use converter for this: convertBodyTo()
                            Struct struct = exchange.getIn().getBody(Struct.class);

                            String documentId = struct.getString("id");
                            String contentType = struct.getString("content_type");

                            ByteBuffer buf = (ByteBuffer) struct.get("data");
                            ByteArrayInputStream bais = new ByteArrayInputStream(buf.array(), buf.position(), buf.limit());

                            // Get Document Metadata
                            String metadataString = this.metadataExtractor.extractAsString(bais);
                            log.info("\nDocument ID: {} - Content Type: {}, \nMetadata: {}", documentId, contentType, metadataString);

                        })
                    .endChoice()
                    .otherwise()
                        .log(LoggingLevel.WARN, "Debezium Operation"
                                + "=${headers[" + DebeziumConstants.HEADER_OPERATION + "]}")
                .endParent();
    }
}
