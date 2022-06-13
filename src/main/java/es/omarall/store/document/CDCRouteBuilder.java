package es.omarall.store.document;

import io.debezium.data.Envelope;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.kafka.connect.data.Struct;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class CDCRouteBuilder extends RouteBuilder {

    final Predicate isCreateEvent =
            header(DebeziumConstants.HEADER_OPERATION).isEqualTo(
                    constant(Envelope.Operation.CREATE.code()));

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

//                        ByteBuffer buf = (ByteBuffer)struct.get("data");
//                        byte[] dataArr = new byte[buf.remaining()];
//                        buf.get(dataArr);

                        Document document = Document.builder()
                                .contentType(struct.getString("content_type"))
                                .id(UUID.fromString(struct.getString("id")))
                                .size(struct.getInt64("size"))
                                .name(struct.getString("name"))
//                                .data(dataArr)
                                .build();

                        log.info("Document Created: {}", document.toString());
                    })
//                    .bean(documentProcessor, "process")
                        .endChoice()
                    .otherwise()
                        .log(LoggingLevel.WARN, "Debezium Operation"
                                + "=${headers[" + DebeziumConstants.HEADER_OPERATION + "]}")
                .endParent();
    }
}
