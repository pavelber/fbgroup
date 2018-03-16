package org.fbgroups.controllers.util

import org.springframework.dao.DataAccessException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.social.security.SocialUser
import org.springframework.social.security.SocialUserDetails
import org.springframework.social.security.SocialUserDetailsService

class SimpleSocialUsersDetailService(private val userDetailsService: UserDetailsService) : SocialUserDetailsService {

    @Throws(UsernameNotFoundException::class, DataAccessException::class)
    override fun loadUserByUserId(userId: String): SocialUserDetails {
        val userDetails = userDetailsService.loadUserByUsername(userId)
        return SocialUser(userDetails.username, userDetails.password, userDetails.authorities)
    }

}