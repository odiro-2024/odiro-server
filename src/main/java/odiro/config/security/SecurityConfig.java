package odiro.config.security;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import odiro.config.jwt.JwtAccessDeniedException;
import odiro.config.jwt.JwtAuthenticationEntryPoint;
import odiro.config.jwt.JwtToken;
import odiro.config.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor // final arg constructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    // 권한이 필요없는 페이지
//    @Builder
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/").permitAll() //모든 user 허용
//                        .anyRequest().authenticated()
//                )
//                .formLogin(Customizer.withDefaults());
//
//
//        return http.build();
//    }

    @Bean
    @Builder
    public SecurityFilterChain filterChain(HttpSecurity http) throws
            Exception {
        http.authorizeHttpRequests((auth)->auth
                .requestMatchers("/"))
        ;
        return http.build();
    }

    //user 권한이 필요한 페이지
    public UserDetailsService userDetailsService(HttpSecurity http) throws Exception {
        UserDetails user = User.withUsername("user").password("{none}1111").authorities("ROLE_USER").build()
        ;
        return new InMemoryUserDetailsManager(user);
    }
}


