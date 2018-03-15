package org.fbgroups.controllers.util;

import org.fbgroups.entity.UserProfile;
import org.fbgroups.entity.UserRepository;
import org.fbgroups.services.IStartDownloads;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Spring calls this on first (!) user login
 */
@Service
public class AccountConnectionSignUpService implements ConnectionSignUp {

    @Autowired
    UsersDao usersDao;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BeanFactory beanFactory;

    IStartDownloads startDownloads;

    public String execute(Connection<?> connection) {


        if (startDownloads == null){
            startDownloads = beanFactory.getBean(IStartDownloads.class); // to prevent circular ref
        }


        //TODO: Take more! { "id", "about", "age_range", "birthday", "context", "cover", "currency", "devices", "education", "email", "favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type", "is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format", "political", "quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other", "sports", "test_group", "timezone", "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift", "website", "work"}
        String [] fields = { "id", "email",  "first_name", "last_name" };
        Facebook facebook = ((Connection<Facebook>)connection).getApi();
        User profile = facebook.fetchObject("me", User.class, fields);
        String userId = profile.getId();

        usersDao.createUser(userId, new UserProfile(userId, profile));
        startDownloads.downloadForUser(userRepository.findOne(userId)); // should be async
        return userId;
    }
}