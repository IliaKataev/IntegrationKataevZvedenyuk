package com.ilia.routes;

import com.ilia.model.Spare;
import com.ilia.services.SpareSyncService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CmsToDbRoute extends RouteBuilder {

    private final SpareSyncService spareService;

    public CmsToDbRoute(SpareSyncService spareService) {
        this.spareService = spareService;
    }

    @Override
    public void configure() {

        from("timer:cmsFetch?period=60000")
                .setProperty("page", constant(0))
                .setProperty("size", constant(10))
                .setProperty("hasNext", constant(true))

                .loopDoWhile(simple("${exchangeProperty.hasNext} == true"))
                .toD("http://212.237.219.35:8080/students/7/cms/spares"
                        + "?page=${exchangeProperty.page}"
                        + "&size=${exchangeProperty.size}"
                        + "&bridgeEndpoint=true")

                .unmarshal().json(Spare[].class)

                .process(exchange -> {
                    Spare[] result = exchange.getIn().getBody(Spare[].class);

                    boolean next = result.length == (int) exchange.getProperty("size");
                    exchange.setProperty("hasNext", next);

                    if (next) {
                        int newPage = (int) exchange.getProperty("page") + 1;
                        exchange.setProperty("page", newPage);
                    }
                })

                .split(body())
                .bean(spareService, "sync")
                .end()

                .end();
    }
}
