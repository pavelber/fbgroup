package org.fbgroups.services

import groovy.transform.CompileStatic
import org.fbgroups.FBUtils
import org.fbgroups.entity.User
import org.fbgroups.entity.UserRepository
import org.fbgroups.entity.UserStatus
import org.fbgroups.lucene.FBPost
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.facebook.api.*
import org.springframework.stereotype.Service

/**
 * Created by Pavel on 9/29/2015.
 */
@Service
@CompileStatic
class StartDownloads implements IStartDownloads, Runnable {

    static Logger logger = LoggerFactory.getLogger(StartDownloads.class)

    @Autowired
    UsersConnectionRepository usersConnectionRepository;

    @Autowired
    UserRepository userRepository;



    @Override
    public void run() {
        def all = userRepository.findAll()
        all.each { user ->
            downloadForUser(user)
        }
    }

    @Async
    void downloadForUser(User user) {
        println("USER: ${user.username}")
        Facebook api = FBUtils.getApi(usersConnectionRepository, user.username);
        List<Group> groupList = api.fetchConnections("me", "groups", Group.class);
        groupList.each {g->
            System.out.println(g.getName())
            PagedList<Post> feed = api.feedOperations().getFeed(g.getId())
            feed.each{p-> System.out.println("\t" + p.getMessage())}
        }

    }


}
