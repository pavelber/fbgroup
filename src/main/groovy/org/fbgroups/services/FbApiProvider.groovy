package org.fbgroups.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.facebook.api.Facebook
import org.springframework.stereotype.Service

@Service
class FbApiProvider {
    @Autowired
    UsersConnectionRepository usersConnectionRepository

    Facebook getAPI(String user){
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(user)
        List<Connection<Facebook>> connections = connectionRepository.findConnections(Facebook.class)
        int c = 0
        while (connections.size() == 0 && c < 5) { // not created yet
            Thread.sleep(5000)
            c++
            connections = connectionRepository.findConnections(Facebook.class)
        }
        Connection<?> connection = connections.get(0)
        return (Facebook) connection.getApi()
    }
}
