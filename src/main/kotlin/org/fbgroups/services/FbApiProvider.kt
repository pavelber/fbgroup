package org.fbgroups.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.facebook.api.Facebook
import org.springframework.stereotype.Service

@Service
internal class FbApiProvider {
    @Autowired
    lateinit var usersConnectionRepository: UsersConnectionRepository

    fun getAPI(user: String?): Facebook {
        val connectionRepository = usersConnectionRepository.createConnectionRepository(user)
        var connections = connectionRepository.findConnections<Facebook>(Facebook::class.java)
        var c = 0
        while (connections.size == 0 && c < 5) { // not created yet
            Thread.sleep(5000)
            c++
            connections = connectionRepository.findConnections<Facebook>(Facebook::class.java)
        }
        val connection = connections[0]
        return connection.api as Facebook
    }
}
