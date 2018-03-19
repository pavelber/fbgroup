package org.fbgroups.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ContentHashRepository : JpaRepository<ContentHash, String> {
    @Query("SELECT MAX(h.timestamp) from ContentHash h where h.groupId = ?1")
    fun getMaxDate(groutpId:String): Date?

    @Query("SELECT h from ContentHash h where h.hash in  ?1")
    fun findByHashes(hashes: List<Int>): List<ContentHash>
}

interface DuplicatesRepository: JpaRepository<Duplicate, Long>{
    fun findByGroupIdOrderByUrlDateDesc(groutpId: String): List<Duplicate>
}