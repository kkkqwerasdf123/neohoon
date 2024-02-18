package com.neohoon.api.config.security.userdetails

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class UserInfo(
    username: String,
    authorities: MutableCollection<out GrantedAuthority>,
    validationKey: String = UUID.randomUUID().toString().substring(0..7),
) : UserDetails {

    private val username = username

    private val authorities: MutableCollection<out GrantedAuthority> = authorities

    val validationKey: String = validationKey

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getPassword(): String = ""

    override fun getUsername(): String {
        return this.username
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    override fun toString(): String {
        return "UserInfo(authorities=$authorities, username=$username, validationKey='$validationKey'"
    }

}