package tp1.film.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    @Column(nullable = false)
    private String password;

    private RoleEnum role;

    @Builder.Default
    private boolean enabled = false;

    public enum RoleEnum {
        USER,
        ADMIN
    }

}
