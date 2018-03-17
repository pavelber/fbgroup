package org.fbgroups.controllers.util

import org.fbgroups.entity.UserProfile
import org.fbgroups.entity.UserRepository
import org.fbgroups.services.IStartDownloads
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionSignUp
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.facebook.api.User
import org.springframework.stereotype.Service

import java.util.UUID

/**
 * Spring calls this on first (!) user login
 */
@Service
class AccountConnectionSignUpService : ConnectionSignUp {

    @Autowired
    lateinit var usersDao: UsersDao

    override fun execute(connection: Connection<*>): String {

        //TODO: Take more! { "id", "about", "age_range", "birthday", "context", "cover", "currency", "devices", "education", "email", "favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type", "is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format", "political", "quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other", "sports", "test_group", "timezone", "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift", "website", "work"}
        val fields = arrayOf("id", "email", "first_name", "last_name")
        val facebook = (connection as Connection<Facebook>).api
        val profile = facebook.fetchObject("me", User::class.java, *fields)
        val userId = profile.id

        usersDao.createUser(userId, UserProfile(userId, profile))
        return userId
    }
}