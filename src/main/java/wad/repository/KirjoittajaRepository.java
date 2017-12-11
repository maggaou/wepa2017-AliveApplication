package wad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wad.domain.Kirjoittaja;

public interface KirjoittajaRepository extends JpaRepository<Kirjoittaja, Long> {
    public Kirjoittaja findByNimi(String nimi);
}
