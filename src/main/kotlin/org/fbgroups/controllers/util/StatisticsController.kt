package org.fbgroups.controllers.util

import org.fbgroups.services.FbApiProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.facebook.api.Comment
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.HashMap


@RestController
internal class StatisticsController {


    @Autowired
    lateinit var apiProvider: FbApiProvider

    @RequestMapping("/statistics")
    fun getStatistics(groupId: String, currentUser: Principal): GroupStatistics {
        val api = apiProvider.getAPI(currentUser.name)
        var feed = api.feedOperations().getFeed(groupId)
        val likeOps = api.likeOperations()!!
        val commentOps = api.commentOperations()!!

        val date = Date.from(LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC))!!

        val likes: HashMap<String, Int> = HashMap()
        val posts: HashMap<String, Int> = HashMap()
        val comments: HashMap<String, Int> = HashMap()
        val receivedLikes: HashMap<String, Int> = HashMap()
        val receivedComments: HashMap<String, Int> = HashMap()

        fun getComments(id:String): List<Comment> {
            val comments = commentOps.getComments(id).map{it!!}
            return comments+comments.flatMap { getComments(it.id) }
        }

        do {
            val ps = feed.filter { it.createdTime > date }.map { it!! }
            ps.forEach { p ->
                posts.compute(p.from.name, ::inc)
                likeOps.getLikes(p.id).forEach { like ->
                    receivedLikes.compute(p.from.name, ::inc)
                    likes.compute(like.name, ::inc)
                }
                getComments(p.id).forEach { comment ->
                    receivedComments.compute(p.from.name, ::inc)
                    comments.compute(comment.from.name, ::inc)
                }
            }

            if (posts.isEmpty() || feed.nextPage == null)
                break
            feed = api.feedOperations().getFeed(groupId, feed.nextPage)

        } while (true)
        return GroupStatistics(sortMap(likes), sortMap(posts), sortMap(comments), sortMap(receivedLikes), sortMap(receivedComments))
    }

    private fun inc(k: String, v: Int?): Int? = if (v == null) 1 else v + 1

}


data class GroupStatistics(
        val likes: Map<String, Int> = HashMap(),
        val posts: Map<String, Int> = HashMap(),
        val comments: Map<String, Int> = HashMap(),
        val receivedLikes: Map<String, Int> = HashMap(),
        val receivedComments: Map<String, Int> = HashMap())

fun sortMap(map: HashMap<String, Int>) = map.toList().sortedByDescending { (_, value) -> value }.toMap()
