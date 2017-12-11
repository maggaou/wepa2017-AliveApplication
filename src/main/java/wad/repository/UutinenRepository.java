package wad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wad.domain.Uutinen;

public interface UutinenRepository extends JpaRepository<Uutinen, Long> {
}
