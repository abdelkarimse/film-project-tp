package tp1.film.Dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FilmUpdateRequest {
    private int id;
    private String titre;
    private String description;
    private int annee;
    private int idCategorie;
    private List<Integer> idActeurs;
    private MultipartFile photo;
}