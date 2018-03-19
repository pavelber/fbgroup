package org.fbgroups.controllers


import org.fbgroups.entity.*
import org.fbgroups.services.FbApiProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.facebook.api.Group
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
internal class DuplicatesController {

    @Autowired
    lateinit var repo: DuplicatesRepository


    @RequestMapping("/duplicates")
    fun getGroups(groupId:String): List<Duplicate> {
        return repo.findByGroupIdOrderByUrlDateDesc(groupId).distinctBy { Pair(it.url,it.original) }
    }


}
