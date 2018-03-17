package org.fbgroups.config

import org.fbgroups.controllers.util.SimpleSocialUsersDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.social.security.SocialUserDetailsService
import org.springframework.social.security.SpringSocialConfigurer
import javax.sql.DataSource

/**
 * Created by magnus on 18/08/14.
 */
@Configuration
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.jdbcAuthentication()
                .dataSource(dataSource)//.withDefaultSchema();
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/authenticate")
                .failureUrl("/login?param.error=bad_credentials")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .and()
                .authorizeRequests()
                .antMatchers("/login*", "/favicon.ico", "/css/**", "/webjars/**", "/static-resources/**").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .rememberMe()
                .and()
                .apply(SpringSocialConfigurer()
                        .postLoginUrl("/index.html")
                        .alwaysUsePostLoginUrl(true))
    }

    @Bean
    public open fun socialUsersDetailService(): SocialUserDetailsService {
        return SimpleSocialUsersDetailService(userDetailsService())
    }
}