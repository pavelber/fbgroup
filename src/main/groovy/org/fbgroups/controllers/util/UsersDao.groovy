package org.fbgroups.controllers.util

import org.apache.commons.lang3.RandomStringUtils
import org.fbgroups.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UsersDao {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;


    void createUser(String userId, UserProfile profile) {

        userRepository.save(new User(username: userId, enabled: true, status: UserStatus.Registered,
                password: RandomStringUtils.randomAlphanumeric(8)));
        authorityRepository.save(new Authority(username: userId, authority: "USER"))
        userProfileRepository.save(new UserProfile(userid: userId, name: profile.name,
                firstname: profile.firstname, lastname: profile.lastname,
                email: profile.email, username: profile.username
        ))


    }
}
