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
    lateinit var userProfileRepository: UserProfileRepository

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

       private const val USER_CONNECTION = "MY_USER_CONNECTION"
        private const val USER_PROFILE = "MY_USER_PROFILE"
    }

}

