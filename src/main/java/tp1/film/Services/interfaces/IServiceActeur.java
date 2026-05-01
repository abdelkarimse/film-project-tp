package tp1.film.Services.interfaces;

import tp1.film.Entity.Acteur;

import java.util.List;

public interface IServiceActeur {

    public void addActeur(Acteur acteur);

    public List<Acteur> getAllActeurs();

    public Acteur getActeurById(int id);

    public void deleteActeur(int id);
}