package com.ilia.routes;

import com.ilia.model.Spare;
import com.ilia.repositories.SpareRepository;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Component
public class CmsToDbRoute extends RouteBuilder {

    private final SpareRepository spareRepository;

    public CmsToDbRoute(SpareRepository spareRepository) {
        this.spareRepository = spareRepository;
    }

    @Override
    public void configure() throws Exception {

        from("timer:fetchCms?period=60000")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        int page = 0;
                        int size = 10;
                        boolean hasNext = true;

                        RestTemplate restTemplate = new RestTemplate();

                        while (hasNext) {
                            String url = "http://212.237.219.35:8080/students/7/cms/spares?page=" + page + "&size="
                                    + size;
                            ResponseEntity<Spare[]> response = restTemplate.getForEntity(url, Spare[].class);

                            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                                Spare[] spares = response.getBody();

                                if (spares.length == 0) {
                                    hasNext = false;
                                    break;
                                }

                                Arrays.stream(spares).forEach(spareFromCms -> {
                                    // есть ли уже в БД
                                    spareRepository.findById(spareFromCms.getSpareCode()).ifPresentOrElse(existing -> {
                                        // и потом обновляем поля
                                        existing.setSpareName(spareFromCms.getSpareName());
                                        existing.setSpareDescription(spareFromCms.getSpareDescription());
                                        existing.setSpareType(spareFromCms.getSpareType());
                                        existing.setSpareStatus(spareFromCms.getSpareStatus());
                                        existing.setPrice(spareFromCms.getPrice());
                                        existing.setQuantity(spareFromCms.getQuantity());
                                        existing.setUpdatedAt(spareFromCms.getUpdatedAt());
                                        existing.setLastSeenAt(spareFromCms.getLastSeenAt());

                                        spareRepository.save(existing);
                                    }, () -> {
                                        // если  все нормально, то новая запись
                                        spareFromCms.setLastSeenAt(spareFromCms.getLastSeenAt());
                                        spareRepository.save(spareFromCms);
                                    });
                                });

                                // тут если меньше чем размер страницы, значит больше страниц нет
                                if (spares.length < size) {
                                    hasNext = false;
                                } else {
                                    page++;
                                }
                            } else {
                                hasNext = false; // остановка при ошибке
                            }
                        }
                    }
                });
    }
}
