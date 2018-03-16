package org.fbgroups.entity

import org.springframework.data.jpa.repository.JpaRepository


interface FBGroupsRepository extends JpaRepository<FBGroup,String>{
    List<FBGroup> findByUserId(String userId)
}
