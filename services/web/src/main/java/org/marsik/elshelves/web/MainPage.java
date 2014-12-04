package org.marsik.elshelves.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainPage {
    @RequestMapping("/status")
    @ResponseBody
    public String getStatus() {
        return "ok";
    }

    @RequestMapping(value = "/error-notification", method = RequestMethod.POST)
    public void errorReported(@RequestParam("stack") String stack) {

    }

    /* MUST STAY AT THE BOTTOM */
    @RequestMapping(value = {"/", "/**"})
    public String mainPage() {
        return "forward:/index.html";
    }
}
