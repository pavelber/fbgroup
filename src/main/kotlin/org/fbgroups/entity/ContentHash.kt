package org.fbgroups.entity

import java.util.*
import javax.persistence.*

@Entity
data class ContentHash(@Id @GeneratedValue val id: Long? = null,val hash:Int, @Temporal(TemporalType.TIMESTAMP) val timestamp: Date, val link:String, val groupId:String, val type: String)

@Entity
class Duplicate(val groupId: String, val url:String, val original:String, val urlDate:Date, val originalDate:Date){

    @Id @GeneratedValue val id: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Duplicate

        if (url != other.url) return false
        if (original != other.original) return false
        if (urlDate != other.urlDate) return false
        if (originalDate != other.originalDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + original.hashCode()
        result = 31 * result + urlDate.hashCode()
        result = 31 * result + originalDate.hashCode()
        return result
    }

    companion object {
        fun of(c1:ContentHash,c2:ContentHash):Duplicate {
            return if (c1.timestamp < c2.timestamp)
                Duplicate(c1.groupId, c2.link,c1.link,c2.timestamp, c1.timestamp)
            else
                Duplicate(c1.groupId, c1.link,c2.link,c1.timestamp, c2.timestamp)
        }
    }
}
