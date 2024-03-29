package com.neohoon.api.config.security.userdetails

import com.neohoon.api.exception.security.MemberNotFoundException
import com.neohoon.api.app.repository.member.MemberRepository
import com.neohoon.domain.entity.member.Member
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
): UserDetailsService {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun loadUserByUsername(username: String): UserDetails {

        log.debug("loadUserByUsername / username: {}", username)

        return memberRepository.findByIdOrNull(username.toLong())
            ?.takeIf { !it.deleted }
            ?.let { createUserInfo(it) }
            ?: throw MemberNotFoundException()
    }

    private fun createUserInfo(member: Member): UserInfo {

        val authorities = member.roles
            .map { SimpleGrantedAuthority("ROLE_${it.role.name}") }
            .toMutableList()

        return UserInfo(
            id = member.id!!,
            authorities = authorities
        )
    }

}