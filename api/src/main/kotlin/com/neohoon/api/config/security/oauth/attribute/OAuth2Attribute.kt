package com.neohoon.api.config.security.oauth.attribute

import com.neohoon.domain.enums.member.Provider

sealed class OAuth2Attribute(val attributes: MutableMap<String, Any>) {
    abstract val providerId: String
    abstract val provider: Provider
    abstract val email: String
}

class KakaoAttribute(attributes: MutableMap<String, Any>): OAuth2Attribute(attributes) {
    override val providerId = attributes["id"].toString()
    override val provider = Provider.kakao
    override val email = (attributes["kakao_account"] as MutableMap<*, *>)["email"].toString()
}

class NaverAttribute(attributes: MutableMap<String, Any>): OAuth2Attribute(attributes) {
    private val attributeResponse: Map<*, *> = attributes["response"] as Map<*, *>
    override val providerId = attributeResponse["id"].toString()
    override val provider = Provider.naver
    override val email = attributeResponse["email"].toString()
}

