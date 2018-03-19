package org.fbgroups.services

import org.fbgroups.entity.FBGroup

interface ILikesReporter : Runnable {
    fun statsForGroup(group: FBGroup)
}
