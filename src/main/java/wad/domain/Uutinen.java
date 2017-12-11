package wad.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "Uutinen")
public class Uutinen extends AbstractPersistable<Long> implements Comparable<Uutinen> {
    private String otsikko;
    private boolean paivitetty;
    private String ingressi;
    @OneToOne
    private FileObject kuva;
    private String sisalto;
    private Date julkaisuaika;
    private Date paivitysaika;
    private int lukumaara;
    @ManyToMany(mappedBy = "uutiset", fetch = FetchType.EAGER)
    private List<Kategoria> kategoriat;
    @ManyToMany(mappedBy = "uutiset")
    private List<Kirjoittaja> kirjoittajat;
    
    public Uutinen(String otsikko, Date julkaisuaika) {
        this.otsikko = otsikko;
        this.julkaisuaika = julkaisuaika;
    }

    @Override
    public int compareTo(Uutinen t) {
        return t.lukumaara-this.lukumaara;
    }
}
