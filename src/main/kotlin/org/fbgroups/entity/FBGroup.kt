package org.fbgroups.entity

import org.springframework.social.facebook.api.Group
import java.text.SimpleDateFormat
import java.util.*

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
data class FBGroup(@Id val id:String, val userId:String, val name:String, @Temporal(TemporalType.TIMESTAMP) val lastCheck: Date, val status: FBGroupStatus){

    override fun equals(other: Any?): Boolean {
        if (other is FBGroup)
            return id == other.id
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        val NOT_CHECKED_YET_DATE = SimpleDateFormat("YYYY").parse("2000")
        fun fromGroup(group: Group, userId: String, status: FBGroupStatus = FBGroupStatus.NONCHECKED): FBGroup {
            return FBGroup(group.id,userId, group.name, NOT_CHECKED_YET_DATE, status)
        }
    }
}
