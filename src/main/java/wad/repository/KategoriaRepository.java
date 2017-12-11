package wad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wad.domain.Kategoria;

public interface KategoriaRepository extends JpaRepository<Kategoria, Long> {
    public Kategoria findByNimi(String nimi);
}
