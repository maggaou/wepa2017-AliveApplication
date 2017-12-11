package wad.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Kirjoittaja extends AbstractPersistable<Long> {
    private String nimi;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Uutinen> uutiset;
    
    public Kirjoittaja(String nimi) {
        this.nimi = nimi;
        uutiset = new ArrayList();
    }
}
