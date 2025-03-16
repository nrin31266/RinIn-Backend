package com.linkedin.backend.features.ws.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Controller
public class GreetingController {


    //app/hello
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String helloMessage) {
        log.info("Message received: {}", helloMessage);
        return "Hello. I have received a greeting message: " + helloMessage;
    }
}
