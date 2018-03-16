package org.fbgroups.controllers

import org.fbgroups.FBUtils
import org.fbgroups.entity.FBGroup
import org.fbgroups.entity.FBGroupStatus
import org.fbgroups.entity.FBGroupsRepository
import org.fbgroups.services.FbApiProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.facebook.api.Group
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

@RestController
class GroupsController {

    @Autowired
    FBGroupsRepository repo

    @Autowired
    FbApiProvider apiProvider

    @RequestMapping("/groups")
    List<FBGroup> getGroups(Principal currentUser) {
        Facebook api = apiProvider.getAPI(currentUser.name)
        String userId = api.userOperations().getUserProfile().id
        List<FBGroup> stored = repo.findByUserId(userId)
        List<FBGroup> real = api.fetchConnections("me", "groups", Group.class).each{FBGroup.fromGroup(it,userId)}
        Iterable<String> ids = real.each { it.id } as Iterable<String>
        List<FBGroup> sameIds = repo.findAll(ids)//.findAll {it.userId!=userId && it.status == FBGroupStatus.CHECKED}



        return [new FBGroup(id: "1", name: "group a", lastCheck: new Date(), status: FBGroupStatus.CHECKED),
                new FBGroup(id: "2", name: "group B", lastCheck: new Date(), status: FBGroupStatus.NONCHECKED)]
    }
}
