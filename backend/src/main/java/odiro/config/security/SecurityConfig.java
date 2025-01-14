package odiro.config.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import odiro.config.jwt.JwtFilter;
import odiro.config.jwt.exception.JwtAccessDeniedException;
import odiro.config.jwt.exception.JwtAuthenticationEntryPoint;
import odiro.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final MemberRepository memberRepository;

    private final CorsConfig corsConfig;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtFilter jwtFilter;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/ws").permitAll()
                        // 해당 API에 대해서는 모든 요청을 허가
                        .requestMatchers("/api/location/festival/research").permitAll()
                        .requestMatchers("/api/home/plan").permitAll()
                        .requestMatchers("/api/user/check-username").permitAll()
                        .requestMatchers("/api/kakao/**").permitAll()
                        .requestMatchers("/api/emails/**").permitAll()
                        .requestMatchers("/api/signin").permitAll()
                        .requestMatchers("/api/signup").permitAll()
                        .requestMatchers("/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().authenticated())
                .addFilterBefore(corsConfig.corsFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex->{
                    ex.accessDeniedHandler(new JwtAccessDeniedException());
                    ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint());
                    ex.accessDeniedHandler(new JwtAccessDeniedException());})
                .build();

    }


}