package org.fbgroups.controllers

import org.fbgroups.entity.GroupStatistics
import org.fbgroups.entity.WeekStatisticsRepository
import org.fbgroups.services.FbApiProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
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
    lateinit var weekStatisticsRepository: WeekStatisticsRepository

    @RequestMapping("/statistics", produces = [MediaType.APPLICATION_JSON_VALUE ])
    fun getStatistics(groupId: String): String {
        return weekStatisticsRepository.findOne(groupId).json
    }



}

