package org.fbgroups


import org.fbgroups.config.Application
import org.fbgroups.entity.FBGroup
import org.fbgroups.entity.FBGroupStatus
import org.fbgroups.entity.FBGroupsRepository
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class])
@Import(Application::class)
class JPATests {

    @Autowired lateinit var fbGroupsRepository: FBGroupsRepository

    @Test
    fun exampleTest() {
        fbGroupsRepository.save(FBGroup("1","1","1",  SimpleDateFormat("YYYY").parse("2000"), FBGroupStatus.CHECKED))
    }

    @After
    fun clean() {
        fbGroupsRepository.delete("1")
    }

}