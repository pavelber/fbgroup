package org.fbgroups.services

import groovy.transform.CompileStatic
import org.fbgroups.entity.User
import org.fbgroups.entity.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.stereotype.Service

/**
 * Created by Pavel on 9/29/2015.
 */
@Service
@CompileStatic
internal class StartDownloads : IStartDownloads, Runnable {

    @Autowired
    var userRepository: UserRepository? = null


    override fun run() {
        val all = userRepository!!.findAll()
        all.forEach { user -> downloadForUser(user) }
    }

    @Async
    override fun downloadForUser(user: User) {

    }

    companion object {

        var logger = LoggerFactory.getLogger(StartDownloads::class.java!!)
    }


}
