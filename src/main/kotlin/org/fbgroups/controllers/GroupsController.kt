package org.fbgroups.controllers


import org.fbgroups.entity.FBGroup
import org.fbgroups.entity.FBGroupStatus
import org.fbgroups.entity.FBGroupsRepository
import org.fbgroups.services.FbApiProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.facebook.api.Group
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.*

@RestController
internal class GroupsController {

    @Autowired
    lateinit var repo: FBGroupsRepository

    @Autowired
    var apiProvider: FbApiProvider? = null

    @RequestMapping("/groups")
    fun getGroups(currentUser: Principal): List<FBGroup> {
        val api = apiProvider!!.getAPI(currentUser.name)
        val userId = api.userOperations().userProfile.id

        val stored = repo.findByUserId(userId).toSet()
        val real = api.fetchConnections("me", "groups", Group::class.java).map { g -> FBGroup.fromGroup(g, userId) }.toSet()
        val ids = real.map({ it.id })
        val sameIds = repo.findAll(ids).filter { it.userId != userId && it.status == FBGroupStatus.CHECKED }.toSet()





        return Arrays.asList(FBGroup("1", "111", "group a", Date(), FBGroupStatus.CHECKED),
                FBGroup("2", "222", "group B", Date(), FBGroupStatus.NONCHECKED))
    }
}
