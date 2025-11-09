package com.ilia.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimpleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception { //автоматически добавилось при создании проекта
        from("timer://hello?period=5000")
                .setBody(simple("🐪 Hello from Apache Camel! Time now: ${date:now:yyyy-MM-dd HH:mm:ss}"))
                .to("stream:out");
    }
}
