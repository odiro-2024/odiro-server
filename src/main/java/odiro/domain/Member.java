package odiro.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
//import odiro.domain.Comment;
//import odiro.domain.Plan;
//import odiro.domain.PlanMember;
//import odiro.domain.Todo;

import static lombok.AccessLevel.*;

@Entity
@Builder
@Getter@Setter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PUBLIC)
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 25)
    private String email;

    @Column(length = 20, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column(nullable = false)
    private boolean emailVerified;

    //비밀번호 암호화
    public void passwordEncoding(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
//    @OneToMany(mappedBy = "initializer")
//    private List<Plan> initalizedPlans = new ArrayList<>();
//
//    @OneToMany(mappedBy = "proposer")
//    private List<Todo> proposedTodos = new ArrayList<>();
//
//    @OneToMany(mappedBy = "writer")
//    private List<Comment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "participant")
//    private List<PlanMember> joinedPlan = new ArrayList<>();

}