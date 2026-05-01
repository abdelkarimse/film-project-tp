package tp1.film.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Acteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;
    private String prenom;
    private Boolean active;

    @JsonIgnore
    @ManyToMany(mappedBy = "acteurs")
    private List<Film> films;
}