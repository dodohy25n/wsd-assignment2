package hello.wsdassignment2.domain.user.entity;

import hello.wsdassignment2.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime deletedAt;

    public static User create(String username, String password, String email, String name) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .name(name)
                .role(Role.ROLE_USER)
                .build();
    }

    public void update(String password, String name) {
        if (password != null) {
            this.password = password;
        }
        if (name != null) {
            this.name = name;
        }
    }

    public static User createForAuthentication(Long id, String username, Role role) {
        return User.builder()
                .id(id)
                .username(username)
                .password("")
                .email(username) // dummy unique value
                .name("user") // dummy value
                .role(role)
                .build();
    }
}
