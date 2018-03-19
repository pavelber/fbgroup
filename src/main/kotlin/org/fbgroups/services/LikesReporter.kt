package org.fbgroups.services

import com.google.gson.Gson
import org.fbgroups.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.facebook.api.Comment
import org.springframework.social.facebook.api.PagingParameters
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.*

@Service
internal class LikesReporter : ILikesReporter {

    @Autowired
    lateinit var fbGroupsRepository: FBGroupsRepository

    @Autowired
    lateinit var weekStatisticsRepository: WeekStatisticsRepository

    @Autowired
    lateinit var fbApiProvider: FbApiProvider

    val gson = Gson()

    override fun run() {
        val all = fbGroupsRepository.findAll()
        all.forEach { group -> statsForGroup(group) }
    }

    override fun statsForGroup(group: FBGroup) {
        val from = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))!!
        val to = from.minusWeeks(1)
        val oldStat = weekStatisticsRepository.findOne(group.id)
        if (oldStat == null || oldStat.date < from.toDate()) {
            val stat = getStatistics(group.id, group.userId, from.toDate(), to.toDate())
            weekStatisticsRepository.save(WeekStatistics(group.id, gson.toJson(stat), Date()))
        }
    }

    fun getStatistics(groupId: String, userName: String, fromDate: Date, toDate: Date): GroupStatistics {
        val api = fbApiProvider.getAPI(userName)
        var feed = api.feedOperations().getFeed(groupId, PagingParameters(100, 0, toDate.time / 1000, fromDate.time / 1000))
        val likeOps = api.likeOperations()!!
        val commentOps = api.commentOperations()!!

        val likes: HashMap<String, Int> = HashMap()
        val posts: HashMap<String, Int> = HashMap()
        val comments: HashMap<String, Int> = HashMap()
        val receivedLikes: HashMap<String, Int> = HashMap()
        val receivedComments: HashMap<String, Int> = HashMap()

        fun getComments(id: String): List<Comment> {
            val comments = commentOps.getComments(id).map { it!! }
            return comments + comments.flatMap { getComments(it.id) }
        }

        println(" ************ SEEKING FROM $fromDate to $toDate")
        do {
            val convertedToPosts = feed.map { it!! }.toList().sortedBy { it.createdTime }
            val skipnew = convertedToPosts.filter { it.createdTime < fromDate }
            val allAreTooNew = skipnew.isEmpty()
            val ps = skipnew.filter { it.createdTime > toDate }
            val allAreTooOld = !allAreTooNew && ps.isEmpty()

            if (convertedToPosts.isNotEmpty())
                println("*** BATCH ${convertedToPosts.size}, oldest ${convertedToPosts.first().createdTime}, newwest ${convertedToPosts.last().createdTime} ")

            ps.forEach { p ->
                posts.compute(p.from.name, ::inc)
                likeOps.getLikes(p.id).forEach { like ->
                    receivedLikes.compute(p.from.name, ::inc)
                    likes.compute(like.name, ::inc)
                }
                /*
                 getComments(p.id).forEach { comment ->
                     receivedComments.compute(p.from.name, ::inc)
                     comments.compute(comment.from.name, ::inc)
                 }*/
            }

            if (allAreTooOld || feed.nextPage == null)
                break
            feed = api.feedOperations().getFeed(groupId, feed.nextPage)

        } while (true)
        return GroupStatistics(topNOfMap(likes, 20), topNOfMap(posts, 20), topNOfMap(comments, 20), topNOfMap(receivedLikes, 20), topNOfMap(receivedComments, 20),
                posts.size, comments.size, likes.size)
    }

    private fun inc(k: String, v: Int?): Int? = if (v == null) 1 else v + 1

}

fun LocalDate.toDate() = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

fun topNOfMap(map: HashMap<String, Int>, top: Int) = map.toList().sortedByDescending { (_, value) -> value }.take(top).toMap()

