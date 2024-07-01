package odiro.config.security;


import lombok.RequiredArgsConstructor;
//import odiro.config.jwt.JwtAccessDeniedException;
//import odiro.config.jwt.JwtAuthenticationEntryPoint;
//import odiro.config.jwt.JwtToken;
//import odiro.config.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import odiro.config.jwt.JwtAuthenticationFilter;
import odiro.config.jwt.JwtAuthorizationFilter;
import odiro.config.jwt.JwtTokenProvider;
import odiro.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor // final arg constructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

//    @Value("${jwt.secret}")
//    private String secretNumber;
    //권한 설정
//    @Bean
//    @Builder
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((auth)->auth
//                .requestMatchers("/").permitAll()
//                .requestMatchers("/signup").permitAll()
//                .anyRequest().authenticated()) // "/"외 모든 주소 로그인 필요
//        ;
//        return http.build();
//    }
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CorsConfig corsConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 해당 API에 대해서는 모든 요청을 허가
//                        .requestMatchers("/api/signup", "/api/signin").permitAll()
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().permitAll())
//                        // USER 권한이 있어야 요청할 수 있음
//                        .requestMatchers("/members/test").hasRole("USER"))
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
//                        UsernamePasswordAuthenticationFilter.class
//                )
                .build();
    }
    public class AddFilter extends AbstractHttpConfigurer<AddFilter, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository));
        }
    }

    //(더미 데이터) 유저 생성
    public UserDetailsService userDetailsService(HttpSecurity http) throws Exception {
        UserDetails user = User.withUsername("root").password("{none}1234").authorities("ROLE_USER").build()
        ;
        return new InMemoryUserDetailsManager(user);
    }
}

