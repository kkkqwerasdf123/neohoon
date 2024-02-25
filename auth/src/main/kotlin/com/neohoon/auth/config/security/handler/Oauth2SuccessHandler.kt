package com.neohoon.auth.config.security.handler

import com.neohoon.auth.config.security.service.AuthService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

private val log = KotlinLogging.logger {}

@Component
class Oauth2SuccessHandler(
    private val authService: AuthService,
    @Value("\${neohoon.front.target}")
    private val frontTarget: String
): SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        authService.getTokenByAuthentication(authentication).also {
            with (response) {
                setHeader(AuthService.AUTHORIZATION_HEADER_NAME, it.accessToken)
                setHeader("Set-Cookie", authService.getRefreshTokenCookie(it.refreshToken).toString())
                sendRedirect(getRedirectUrl(it.accessToken))
            }
        }
    }

    private fun getRedirectUrl(accessToken: String): String {
        return UriComponentsBuilder.fromUriString("${frontTarget}/login/oauth2")
            .queryParam("accessToken", accessToken).build().toUriString()
    }

}