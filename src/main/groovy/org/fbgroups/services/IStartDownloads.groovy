package org.fbgroups.services

import org.fbgroups.entity.User

/**
 * Created by Pavel on 1/19/2016.
 */
interface IStartDownloads extends Runnable {
    void downloadForUser(User user)
}