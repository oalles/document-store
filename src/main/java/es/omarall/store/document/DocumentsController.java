package es.omarall.store.document;

import es.omarall.store.document.exceptions.RequestValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.status;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(path = "/")
@Slf4j
public class DocumentsController {

    private final DocumentsRepository documentsRepository;

    public DocumentsController(final DocumentsRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }

    @PostMapping(path = "/")
    public ResponseEntity<Document> uploadFile(@RequestParam("file") MultipartFile multifile) throws IOException {

        // Validate mime type
        getValidMimeType(multifile.getContentType());

        Document document = Document.builder()
                .id(UUID.randomUUID())
                .contentType(multifile.getContentType())
                .data(multifile.getBytes())
                .size(multifile.getSize())
                .name(multifile.getOriginalFilename())
                .build();
        if (StringUtils.hasText(multifile.getName())) {
            document.setName(multifile.getName());
        }
        this.documentsRepository.save(document);

        document.setData(null);

        return status(HttpStatus.CREATED)
                .location(UriComponentsBuilder.fromPath("/{id}")
                        .buildAndExpand(document.getId()).toUri())
                .body(document);
    }

    @GetMapping(path = "/{documentId}")
    public ResponseEntity<Resource> serveFile(@PathVariable String documentId) {

        UUID id = getUUID(documentId);

        Document document = documentsRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        Resource file = new InputStreamResource(new ByteArrayInputStream(document.getData()));

        MediaType mt = this.getValidMimeType(document.getContentType());

        return ResponseEntity
                .ok()
                .header("Cache-Control", "public")
                .contentType(mt)
                .body(file);
    }

    @DeleteMapping(path = "/{documentId}")
    public ResponseEntity deleteFile(@PathVariable String documentId) {

        UUID id = getUUID(documentId);

        Document document = documentsRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        documentsRepository.deleteById(id);

        return ResponseEntity
                .accepted().build();
    }

    @GetMapping(value = "/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/actuator/health");
    }

    private UUID getUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (Throwable t) {
            throw new RequestValidationException("Invalid document identifier", String.format("Not valid uuid: %s", id));
        }
    }

    public MediaType getValidMimeType(final String mimeType) {
        try {
            String mimes[] = mimeType.split("/");
            MediaType mt = new MediaType(mimes[0], mimes[1]);
            return mt;
        } catch (Throwable t) {
            throw new RequestValidationException("content type", String.format("Invalid content type: %s", mimeType));
        }
    }

}
