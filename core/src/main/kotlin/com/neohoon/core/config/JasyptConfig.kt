package com.neohoon.api.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JasyptConfig (
    @Value("\${jasypt.encryptor.password:rlaehdgurWkd}")
    var password: String
) {
    val algorithm: String = "PBEWithMD5AndDES"
    val keyObtentionIterations: Int = 1000
    val poolSize: Int = 1
    val outputType: String = "base64"

    @Bean("jasyptStringEncryptor")
    fun stringEncryptor(): StringEncryptor {
        SimpleStringPBEConfig()
            .let {
                it.password = password
                it.algorithm = algorithm
                it.keyObtentionIterations = keyObtentionIterations
                it.poolSize = poolSize
                it.stringOutputType = outputType

                password = ""

                val encryptor = PooledPBEStringEncryptor()
                encryptor.setConfig(it)
                return encryptor
            }
    }


}