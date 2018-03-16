package org.fbgroups.controllers.util

import org.apache.commons.lang3.RandomStringUtils
import org.fbgroups.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UsersDao {

    @Autowired
    var authorityRepository: AuthorityRepository? = null

    @Autowired
    var userRepository: UserRepository? = null

    @Autowired
    var userProfileRepository: UserProfileRepository? = null


    fun createUser(userId: String, profile: UserProfile) {

        userRepository!!.save(User(userId, RandomStringUtils.randomAlphanumeric(8), true))
        authorityRepository!!.save(Authority(userId, "USER"))
        userProfileRepository!!.save(UserProfile(userId, profile.name,
                profile.firstname, profile.lastname,
                profile.email, profile.username
        ))


    }
}
