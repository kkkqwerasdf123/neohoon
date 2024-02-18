package com.neohoon.core.authentication.token

class TokenUserDto(
    val username: String,
    val authorities: Collection<String>,
    val validationKey: String = ""
) {
}