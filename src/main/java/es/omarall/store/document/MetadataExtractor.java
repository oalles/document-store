package es.omarall.store.document;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MetadataExtractor {

    public String extractAsString(InputStream is) {

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        try {
            parser.parse(is, handler, metadata, context);
            return Arrays.stream(metadata.names()).map(name -> name + ": " + metadata.get(name))
                    .collect(Collectors.joining(" | "));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }
}
