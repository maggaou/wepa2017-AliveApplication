package wad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wad.domain.FileObject;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
}
