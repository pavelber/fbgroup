package org.fbgroups.entity

import org.springframework.social.facebook.api.Group

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class FBGroup {
    @Id
    String id
    String userId
    String name
    Date lastCheck
    FBGroupStatus status

    static def fromGroup(Group group, String userId) {
        return new FBGroup(id:group.id, name: group.name, userId:userId)
    }
}
