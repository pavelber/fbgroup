package org.fbgroups.entity

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by Pavel on 9/29/2015.
 */
interface UserRepository : JpaRepository<User, String>
