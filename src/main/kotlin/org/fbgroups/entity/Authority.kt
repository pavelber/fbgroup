package org.fbgroups.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Pavel on 1/16/2016.
 */
@Entity
@Table(name = "authorities")
data class Authority(@Id val username:String, val authority: String)
