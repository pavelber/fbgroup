package org.fbgroups.config

import org.fbgroups.controllers.util.AccountConnectionSignUpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.social.UserIdSource
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer
import org.springframework.social.config.annotation.EnableSocial
import org.springframework.social.config.annotation.SocialConfigurer
import org.springframework.social.connect.ConnectionFactoryLocator
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository
import org.springframework.social.security.AuthenticationNameUserIdSource
import javax.sql.DataSource

/**
 * Created by magnus on 18/08/14.
 */
@Configuration
@EnableSocial
open class SocialConfig : SocialConfigurer {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var accountConnectionSignUpService: AccountConnectionSignUpService

    override fun addConnectionFactories(connectionFactoryConfigurer: ConnectionFactoryConfigurer, environment: Environment) {}

    override fun getUserIdSource(): UserIdSource {
        return AuthenticationNameUserIdSource()
    }

    override fun getUsersConnectionRepository(connectionFactoryLocator: ConnectionFactoryLocator): UsersConnectionRepository {
        val repository = JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText())
        repository.setConnectionSignUp(accountConnectionSignUpService)
        return repository
    }
}
