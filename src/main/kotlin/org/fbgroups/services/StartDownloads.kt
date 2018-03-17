package org.fbgroups.services

import groovy.transform.CompileStatic
import org.fbgroups.entity.ContentHash
import org.fbgroups.entity.ContentHashRepository
import org.fbgroups.entity.FBGroup
import org.fbgroups.entity.FBGroupsRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.social.facebook.api.Facebook
import org.springframework.stereotype.Service
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.imageio.ImageIO
import java.awt.image.DataBufferByte




/**
 * Created by Pavel on 9/29/2015.
 */
@Service
@CompileStatic
internal class StartDownloads : IStartDownloads, Runnable {

    @Autowired
    lateinit var fbGroupsRepository: FBGroupsRepository

    @Autowired
    lateinit var fbApiProvider: FbApiProvider

    @Autowired
    lateinit var contentHashRepository: ContentHashRepository


    override fun run() {
        val all = fbGroupsRepository.findAll()
        all.forEach { group -> downloadForGroup(group) }
    }

    @Async
    override fun downloadForGroup(group: FBGroup) {
        val api = fbApiProvider.getAPI(group.userId)

        val maxDate = contentHashRepository.getMaxDate(group.id)
                ?: Date.from(LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC))!!

        downloadUpToDate(maxDate, api, group.id)

    }

    private fun downloadUpToDate(date: Date, api: Facebook, id: String) {
        var feed = api.feedOperations().getFeed(id)
        do {
            val posts = feed.filter { it.createdTime > date }
            posts.forEach { p ->
                val postLink = createLink(p.id)
                val creator = ContentHashCreator(p.createdTime, postLink, id)
                val hashes = listOf(
                        creator.hash(p.message),
                        creator.hash(p.description),
                        creator.hash(p.link),
                        creator.image(p.picture)).filter { it != null }.map { it!! }
                if (hashes.isNotEmpty()) {
                    val bayans = contentHashRepository.findByHashes(hashes.map { it.hash }).map{it.link}.toSet()
                    bayans.forEach { println("$postLink duplicates $it") }
                    contentHashRepository.save(hashes)
                }
            }
            if (posts.isEmpty())
                break
            feed = api.feedOperations().getFeed(id, feed.nextPage)

        } while (true)
    }

    private fun createLink(id: String): String {
        val ids = id.split("_")
        return "https://www.facebook.com/groups/${ids[0]}/permalink/${ids[1]}/"
    }

    companion object {

        var logger = LoggerFactory.getLogger(StartDownloads::class.java)
    }

    class ContentHashCreator(val date: Date, val link: String, val groupId: String) {
        fun hash(text: String?): ContentHash? {
            if (text != null)
                return ContentHash(hash = text.hashCode(), timestamp = date, link = link, groupId = groupId)
            else return null
        }

        fun image(imageurl: String?): ContentHash? {
            if (imageurl != null) {
                val url = URL(imageurl)
                url.openStream().use {
                    val image = ImageIO.read(it)!!
                    val bytes = (image.getRaster().getDataBuffer() as DataBufferByte).data!!
                    return ContentHash(hash = bytes.contentHashCode(), timestamp = date, link = link, groupId = groupId)
                }

            } else return null
        }
    }

}
