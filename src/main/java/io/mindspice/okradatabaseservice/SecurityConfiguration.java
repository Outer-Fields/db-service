package io.mindspice.okradatabaseservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;



@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> {
                                           try {
                                               authz
                                                       .anyRequest().authenticated().and()
                                                       .csrf().disable();
                                           } catch (Exception e) {
                                               throw new RuntimeException(e);
                                           }
                                       }
                )
                .httpBasic(withDefaults());
        return http.build();
    }

}

