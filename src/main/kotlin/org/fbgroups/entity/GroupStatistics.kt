package org.fbgroups.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob

data class GroupStatistics(
        val likes: Map<String, Int> = HashMap(),
        val posts: Map<String, Int> = HashMap(),
        val comments: Map<String, Int> = HashMap(),
        val receivedLikes: Map<String, Int> = HashMap(),
        val receivedComments: Map<String, Int> = HashMap(),
        val posters: Int,
        val commenters:Int,
        val likers:Int)

@Entity
data class WeekStatistics(@Id val groupId:String, @Lob val json: String, val date: Date)