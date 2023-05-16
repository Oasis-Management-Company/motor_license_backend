package com.app.IVAS.configuration;


import com.app.IVAS.security.JwtAuthEntryPoint;
import com.app.IVAS.security.JwtAuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true
)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }
    

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()
                .antMatchers("/api/auth/**","/v3/api-docs/**",
                       "/ivas-docs",
                        "/configuration/**",
                        "/webjars/**",
                        "/api/validate",
                        "/api/validate/**",
                        "/api/verify/ucode/**",
                        "/api/user/login",
                        "/api/user/login/**",
                        "/api/user/generate-otp",
                        "/api/user/generate-otp/**",
                        "/api/user/reset-password",
                        "/api/user/reset-password/**",
                        "/api/validate-asin",
                        "/api/verification/**",
                        "/api/home",
                        "/api/payment/send",
                        "/api/user/check-otp",
                        "/api/generate/dashboard/request",
                        "/api/verification/invoice/view/**",
                        "/api/payment/send/**",
                        "/api/payment/send",
                        "/api/payment/respondse/payment",
                        "/api/payment/insurance/send"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout().logoutUrl("/api/auth/logout").invalidateHttpSession(true);

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", 
                "/static/**",
                "/css/**", 
                "/js/**", 
                "/images/**",
                "/console/**",
                "/v3/api-docs",
                "/swagger-ui/index.html",
                "/configuration/ui",
                "**/swagger-resources/**",
                "/configuration/security",
                "/webjars/**",
                "/templates/**",
                "/api/document-manager/getImage/**"
        );
    }

}