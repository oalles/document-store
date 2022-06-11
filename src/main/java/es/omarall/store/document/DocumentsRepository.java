package es.omarall.store.document;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentsRepository extends PagingAndSortingRepository<Document, UUID> {
}

