package fitlog.backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MusictagEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 자동화
    private Long id;
    private String name;
}
