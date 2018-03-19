package org.fbgroups.services

import groovy.transform.CompileStatic
import org.fbgroups.entity.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.facebook.api.Post
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
internal class DuplicatesSearcher : IDuplicatesSearcher, Runnable {

    @Autowired
    lateinit var fbGroupsRepository: FBGroupsRepository

    @Autowired
    lateinit var fbApiProvider: FbApiProvider

    @Autowired
    lateinit var contentHashRepository: ContentHashRepository

    @Autowired
    lateinit var duplicatesRepository: DuplicatesRepository

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
                        creator.hash(p.message,"message", 64),
                        creator.hash(p.link,"link",0),
                        creator.image(p.picture,p.type)).filter { it != null }.map { it!! }
                if (hashes.isNotEmpty()) {
                    val old = contentHashRepository.findByHashes(hashes.map { it.hash })
                    val bayans = hashes.flatMap { hash ->  old.filter { it.hash==hash.hash }.map{Pair(it,hash)}}
                    bayans.forEach { println("${it.first.link} ${it.first.type} duplicates ${it.second.link} ${it.second.type}")}
                    contentHashRepository.save(hashes)
                    duplicatesRepository.save(bayans.map{Duplicate.of(it.first,it.second)})
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

        var logger = LoggerFactory.getLogger(DuplicatesSearcher::class.java)
    }

    class ContentHashCreator(private val date: Date, private val link: String, private val groupId: String) {
        fun hash(text: String?, type: String, limit:Int): ContentHash? {
            if (text != null && text.length > limit)
                return ContentHash(hash = text.hashCode(), timestamp = date, link = link, groupId = groupId, type = type)
            else return null
        }

        fun image(imageurl: String?, type: Post.PostType): ContentHash? {
            if (imageurl != null && (type == Post.PostType.PHOTO || type==Post.PostType.VIDEO)) {
                val url = URL(imageurl)
                url.openStream().use {
                    val image = ImageIO.read(it)!!
                    val bytes = (image.getRaster().getDataBuffer() as DataBufferByte).data!!
                    return ContentHash(hash = bytes.contentHashCode(), timestamp = date, link = link, groupId = groupId,type=type.name)
                }

            } else return null
        }
    }

}
