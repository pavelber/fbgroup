package org.fbgroups.entity

import org.springframework.data.jpa.repository.JpaRepository


interface FBGroupsRepository : JpaRepository<FBGroup, String> {
    fun findByUserId(userId: String): List<FBGroup>
}
