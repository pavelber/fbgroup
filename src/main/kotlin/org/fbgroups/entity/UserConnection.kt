package org.fbgroups.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Pavel on 1/16/2016.
 */
@Entity
@Table(name = "userconnection")
data class UserConnection(@Id val userid: String,
                              val providerid: String,
                              val provideruserid: String,
                              val rank: Int = 0,
                              val displayname: String,
                              val profileurl: String,
                              val imageurl: String,
                              val accesstoken: String,
                              val secret: String,
                              val refreshtoken: String,
                              val expiretime: Long)
