package org.fbgroups.services

import org.fbgroups.entity.FBGroup

/**
 * Created by Pavel on 1/19/2016.
 */
interface IDuplicatesSearcher : Runnable {
    fun downloadForGroup(group: FBGroup)
}