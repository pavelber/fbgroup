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

@RestController
internal class GroupsController {

    @Autowired
    lateinit var repo: FBGroupsRepository

    @Autowired
    lateinit var apiProvider: FbApiProvider

    @RequestMapping("/groups")
    fun getGroups(currentUser: Principal): List<FBGroup> {
        val api = apiProvider.getAPI(currentUser.name)
        val userId = api.userOperations().userProfile.id

        val stored = repo.findByUserId(userId).toSet()
        val real = api.fetchConnections("me", "groups", Group::class.java).map { g -> FBGroup.fromGroup(g, userId) }.toSet()
        val ids = real.map({ it.id })
        val taken = repo.findAll(ids).filter { it.userId != userId && it.status == FBGroupStatus.CHECKED }.toSet()

        val removed = stored.filter { it !in real }
        val realWithStatuses = real.map { g ->
            when (g) {
                in taken -> g.copy(status = FBGroupStatus.TAKEN)
                in stored -> g.copy(status = FBGroupStatus.CHECKED)
                else -> g
            }
        }

        return realWithStatuses + removed
    }

    @RequestMapping("/group")
    fun changeGroupStatus(id: String, manage: Boolean, currentUser: Principal) {
        if (manage) {
            val api = apiProvider.getAPI(currentUser.name)
            val userId = api.userOperations().userProfile.id
            val group = api.fetchConnections("me", "groups", Group::class.java).map { g -> FBGroup.fromGroup(g, userId, FBGroupStatus.CHECKED) }.filter { it.id == id }.first()
            repo.save(group)
        } else repo.delete(id)
    }
}
