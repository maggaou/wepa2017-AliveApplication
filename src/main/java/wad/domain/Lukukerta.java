package wad.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Lukukerta extends AbstractPersistable<Long> {
    private Date ajankohta;
    @OneToOne
    private Uutinen uutinen;
}
