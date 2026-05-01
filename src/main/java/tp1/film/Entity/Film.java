package tp1.film.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String titre;
    private String description;
    private int annepartution;
    private String photo;
    @ManyToOne
    private Category category;
    @ManyToMany
    private List<Acteur> acteurs;
}
