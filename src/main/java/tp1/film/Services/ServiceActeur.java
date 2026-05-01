package tp1.film.Services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tp1.film.Entity.Acteur;
import tp1.film.Repository.ActeurRepository;
import tp1.film.Services.interfaces.IServiceActeur;

import java.util.List;

@Service
@AllArgsConstructor
public class ServiceActeur implements IServiceActeur {

    private final ActeurRepository acteurRepository;

    @Override
    public void addActeur(Acteur acteur) {
        acteurRepository.save(acteur);
    }

    @Override
    public List<Acteur> getAllActeurs() {
        return acteurRepository.findAll();
    }

    @Override
    public Acteur getActeurById(int id) {
        return acteurRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteActeur(int id) {
        acteurRepository.deleteById(id);

    }
}