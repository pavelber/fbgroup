package org.fbgroups.entity

import org.springframework.data.jpa.repository.JpaRepository


internal interface FBGroupsRepository : JpaRepository<FBGroup, String> {
    fun findByUserId(userId: String): List<FBGroup>
}
