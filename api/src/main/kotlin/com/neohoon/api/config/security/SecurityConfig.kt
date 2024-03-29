package com.neohoon.api.config.security

import com.neohoon.api.config.security.filter.JwtFilter
import com.neohoon.api.config.security.handler.CustomAccessDeniedHandler
import com.neohoon.api.config.security.handler.CustomAuthenticationEntryPoint
import com.neohoon.api.config.security.handler.Oauth2SuccessHandler
import com.neohoon.api.config.security.service.AuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val accessDeniedHandler: CustomAccessDeniedHandler,
    private val oauth2SuccessHandler: Oauth2SuccessHandler,
    private val jwtFilter: JwtFilter,

    @Value("\${neohoon.security.cors.allowed-origins}")
    private val allowedOrigins: Array<String>
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        with(http) {
            csrf { it.disable() }
            headers { it.disable() }
            cors { it.configurationSource(corsConfigurationSource()) }
            authorizeHttpRequests {
                it.requestMatchers("/actuator/**").permitAll()

                it.requestMatchers(*antMatchers("/login/oauth2/code/**")).permitAll()
                it.requestMatchers(*antMatchers("/oauth2/authorization/**")).permitAll()
                it.requestMatchers(*antMatchers("/api/v1/test/**")).permitAll()
                it.requestMatchers(*antMatchers("/api/v1/**")).authenticated()
                it.anyRequest().denyAll()
            }
            sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            exceptionHandling {
                it.accessDeniedHandler(accessDeniedHandler)
                it.authenticationEntryPoint(authenticationEntryPoint)
            }
            oauth2Login {
                it.successHandler(oauth2SuccessHandler)
            }
            addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            build()
        }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.allowedOrigins = allowedOrigins.toMutableList()
        config.allowedMethods = mutableListOf(GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD).map { it.name() }
        config.allowedHeaders = mutableListOf(AuthService.AUTHORIZATION_HEADER_NAME)
        config.exposedHeaders = mutableListOf(AuthService.AUTHORIZATION_HEADER_NAME)
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    private fun antMatchers(vararg patterns: String): Array<RequestMatcher> {
        return patterns.map { AntPathRequestMatcher(it) }.toTypedArray()
    }

}