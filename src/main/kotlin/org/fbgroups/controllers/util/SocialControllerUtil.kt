package org.fbgroups.controllers.util

import org.fbgroups.entity.UserConnection
import org.fbgroups.entity.UserConnectionRepository
import org.fbgroups.entity.UserProfile
import org.fbgroups.entity.UserProfileRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.WebAttributes
import org.springframework.stereotype.Component
import org.springframework.ui.Model
import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * Created by magnus on 2014-08-24.
 */
@Component
class SocialControllerUtil {

    @Autowired
    lateinit var userConnectionRepository: UserConnectionRepository

    @Autowired
    lateinit var springConnectionRepo: org.springframework.social.connect.UsersConnectionRepository

    @Autowired
    lateinit var userProfileRepository: UserProfileRepository


    @Autowired
    lateinit var usersDao: UsersDao

    fun setModel(request: HttpServletRequest, currentUser: Principal?, model: Model) {

        // SecurityContext ctx = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

        val userId = currentUser?.name
        val path = request.requestURI
        val session = request.session

        var connection: UserConnection? = null
        var profile: UserProfile? = null
        var displayName: String? = null

        // Collect info if the user is logged in, i.e. userId is set
        if (userId != null) {

            // Get the current UserConnection from the http session
            connection = getUserConnection(session, userId)

            // Get the current UserProfile from the http session
            profile = getUserProfile(session, userId)

            // Compile the best display name from the connection and the profile
            displayName = getDisplayName(connection, profile)

        }

        val exception = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) as Throwable?

        // Update the model with the information we collected
        model.addAttribute("exception", exception?.message)
        model.addAttribute("currentUserId", userId)
        model.addAttribute("currentUserProfile", profile)
        model.addAttribute("currentUserConnection", connection)
        model.addAttribute("currentUserDisplayName", displayName)

        if (LOG.isDebugEnabled) {
            logInfo(request, model, userId, path, session)
        }
    }

    protected fun logInfo(request: HttpServletRequest, model: Model, userId: String?, path: String, session: HttpSession) {
        // Log the content of the model
        LOG.debug("Path: $path, currentUserId: $userId")

        LOG.debug("Non-null request-attributes:")
        val rane = request.attributeNames
        while (rane.hasMoreElements()) {
            val key = rane.nextElement()
            val value = session.getAttribute(key)
            if (value != null) {
                LOG.debug(" - $key = $value")
            }
        }

        LOG.debug("Session-attributes:")
        val sane = session.attributeNames
        while (sane.hasMoreElements()) {
            val key = sane.nextElement()
            LOG.debug(" - " + key + " = " + session.getAttribute(key))
        }

        val me = model.asMap().entries
        LOG.debug("ModelElements (" + me.size + "):")
        for ((key, value) in me) {
            LOG.debug(" - $key = $value")
        }
    }

    /**
     * Get the current UserProfile from the http session
     *
     * @param session
     * @param userId
     * @return
     */
    protected fun getUserProfile(session: HttpSession, userId: String): UserProfile {
        val profile: UserProfile = session.getAttribute(USER_PROFILE) as UserProfile?
                ?: userProfileRepository.getOne(userId)

        session.setAttribute(USER_PROFILE, profile)

        return profile
    }

    /**
     * Get the current UserConnection from the http session
     *
     * @param session
     * @param userId
     * @return
     */
    internal fun getUserConnection(session: HttpSession, userId: String?): UserConnection {
        val connection: UserConnection =
                (session.getAttribute(USER_CONNECTION) as UserConnection?) ?: userConnectionRepository.getOne(userId)

        session.setAttribute(USER_CONNECTION, connection)

        return connection
    }

    /**
     * Compile the best display name from the connection and the profile
     *
     * @param connection
     * @param profile
     * @return
     */
    internal fun getDisplayName(connection: UserConnection?, profile: UserProfile?): String {

        // The name is set differently in different providers so we better look in both places...
        return connection!!.displayname ?: profile!!.name
    }


    companion object {

        private val LOG = LoggerFactory.getLogger(SocialControllerUtil::class.java)

        private val USER_CONNECTION = "MY_USER_CONNECTION"
        private val USER_PROFILE = "MY_USER_PROFILE"
    }

}

