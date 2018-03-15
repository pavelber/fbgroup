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
    public void downloadForUser(User user) {
        Facebook api = FBUtils.getApi(usersConnectionRepository, user.username);
        FeedOperations feedOperations = api.feedOperations();
        if (user.status == UserStatus.Registered || user.status == UserStatus.DownloadingOld) {
            downloadBackward(user, feedOperations)
        } else if (user.status == UserStatus.Normal) {
            downloadLast(user, feedOperations);
        }

    }


    public void downloadLast(User user, FeedOperations feedOperations) {
        int limit = 100;
        int offset = 0;
        //PagingParameters pagedListParameters = new PagingParameters(limit, offset, user.lastindexeddate?.time, null);
        //Because some bug we can't take after the date, so we take all and filter
        PagingParameters pagedListParameters = new PagingParameters(limit, offset, null, null);

        // assuming:
        // 1) lastindexeddate should not be null,
        // No more one page since last invocation (Problematic!!!)
        PagedList<Post> posts = feedOperations.getPosts(pagedListParameters);

        if (user.lastindexeddate != null) {
            posts.removeAll { it.createdTime.time <= user.lastindexeddate.time }
        }

        save(posts, user)

        Date newest = (posts.max { p -> p.getCreatedTime() })?.createdTime
        if (newest != null) {
            user.lastindexeddate = (user.lastindexeddate == null) ? newest : (user.lastindexeddate.before(newest)) ? newest :
                    user.lastindexeddate
            println "newest = $newest"
            user.lastindexeddate = newest;
        }
        userRepository.save(user)
    }

    public void downloadBackward(User user, FeedOperations feedOperations) {
        int limit = 50;
        int offset = 0;
        PagedList<Post> posts;
        Long time = user.firstindexeddate == null ? null : user.firstindexeddate.time + 1000
        PagingParameters pagedListParameters = new PagingParameters(limit, offset, null, time);

        while (true) {
            posts = feedOperations.getPosts(pagedListParameters);
            if (user.firstindexeddate != null) {
                posts.removeAll { it.createdTime.time >= user.firstindexeddate.time }
            }
            if (posts.size() == 0) {
                break;
            }
            pagedListParameters = posts.getNextPage();
            save(posts, user)
            Date oldest = (posts.min { p -> p.getCreatedTime() }).createdTime
            Date newest = (posts.max { p -> p.getCreatedTime() }).createdTime
            user.lastindexeddate = (user.lastindexeddate == null) ? newest : (user.lastindexeddate.before(newest)) ? newest :
                    user.lastindexeddate
            user.firstindexeddate = oldest;
            user.status = UserStatus.DownloadingOld;
            userRepository.save(user)
        }

        user.status = UserStatus.Normal
        userRepository.save(user)
    }

    public Iterable<Post> save(PagedList<Post> posts, User user) {
        return posts.each { p ->

            String url = "https://www.facebook.com/" + p.id.replace("_", "/posts/")
            (new FBPost(name: p.name,
                    message: p.message,
                    description: p.description,
                    caption: p.caption,
                    link: p.link,
                    url: url,
                    username: user.username,
                    date: p.createdTime.time))

        }
    }

}
