package org.fbgroups.entity

import java.util.*
import javax.persistence.*

@Entity
data class ContentHash(@Id @GeneratedValue val id: Long? = null,val hash:Int, @Temporal(TemporalType.TIMESTAMP) val timestamp: Date, val link:String, val groupId:String)
