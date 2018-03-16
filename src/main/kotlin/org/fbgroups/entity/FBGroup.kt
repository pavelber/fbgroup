package org.fbgroups.entity

import org.springframework.social.facebook.api.Group
import java.util.*

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class FBGroup(@Id val id:String, val userId:String, val name:String, val lastCheck: Date, val status: FBGroupStatus){

    companion object {
        fun fromGroup(group: Group, userId: String): FBGroup {
            return FBGroup(group.id,group.name, userId, Date(Long.MIN_VALUE), FBGroupStatus.NONCHECKED);
        }
    }
}
