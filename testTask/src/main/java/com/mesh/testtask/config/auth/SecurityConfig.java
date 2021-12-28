package com.mesh.testtask.config.auth;

import com.mesh.testtask.service.UserService;
import com.mesh.testtask.service.impl.TokenAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final UserService userService;
    final TokenAuthenticationManager tokenAuthenticationManager;

    @Autowired
    public SecurityConfig(UserService userService, TokenAuthenticationManager tokenAuthenticationManager) {
        this.userService = userService;
        this.tokenAuthenticationManager = tokenAuthenticationManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // We don't need CSRF for test app
        http.csrf().disable().
                httpBasic().disable().
                // all "/rest" requests need to be authenticated
              authorizeRequests().antMatchers("/rest/**").authenticated().and().
                authorizeRequests().anyRequest().permitAll();

        http.addFilterBefore(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
     AuthFilter restAuthenticationFilter() {
        AuthFilter restTokenAuthenticationFilter = new AuthFilter();
        restTokenAuthenticationFilter.setAuthenticationManager(tokenAuthenticationManager);
        restTokenAuthenticationFilter.setAuthenticationSuccessHandler(successHandler());
        return restTokenAuthenticationFilter;
    }


    @Bean
    FilterRegistrationBean disableAutoRegistration(final AuthFilter filter) {
        final FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
     AuthenticationEntryPoint forbiddenEntryPoint() {
        return new HttpStatusEntryPoint(FORBIDDEN);
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy(new NoRedirectStrategy());
        return successHandler;
    }
}
