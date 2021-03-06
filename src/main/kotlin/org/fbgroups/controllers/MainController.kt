package org.fbgroups.controllers

import org.fbgroups.controllers.util.SocialControllerUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.HttpServletRequest
import java.security.Principal

/**
 * Created by magnus on 18/08/14.
 */
@Controller
class MainController {

    @Autowired
    lateinit var util: SocialControllerUtil

    @RequestMapping("/login")
    fun login(request: HttpServletRequest, currentUser: Principal?, model: Model): String {
        util.setModel(request, currentUser, model)
        return "login"
    }

}
