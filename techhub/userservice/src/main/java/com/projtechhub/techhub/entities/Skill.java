package com.projtechhub.techhub.entities;

/**
 * @author pc
 **/
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "skills",
        indexes = {
                @Index(name = "idx_skills_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_skill_user"))
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Level level;

    @Column(name = "years_experience")
    private Short yearsExperience;

    public enum Level {
        BEGINNER,
        INTERMEDIATE,
        EXPERT
    }
}
