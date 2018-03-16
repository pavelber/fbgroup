package org.fbgroups.entity


import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by Pavel on 11/17/2015.
 */
interface UserConnectionRepository : JpaRepository<UserConnection, String>
