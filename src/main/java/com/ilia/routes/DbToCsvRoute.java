package com.ilia.routes;

import com.ilia.model.Spare;
import com.ilia.repositories.SpareRepository;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class DbToCsvRoute extends RouteBuilder {

    private final SpareRepository spareRepository;

    public DbToCsvRoute(SpareRepository spareRepository) {
        this.spareRepository = spareRepository;
    }

    @Override
    public void configure() throws Exception {
        from("timer:generateCsv?repeatCount=1") // выполняем один раз при запуске
                .process(exchange -> {
                    List<Spare> spares = spareRepository.findAll();

                    if (spares.isEmpty()) {
                        System.out.println("Нет данных в БД, CSV не создан.");
                        return;
                    }

                    StringBuilder csv = new StringBuilder();

                    for (Spare s : spares) {
                        csv.append(String.format("%s;%s;%s;%s;%s;%s;%s;%s\n",
                                s.getSpareCode(),
                                s.getSpareName(),
                                s.getSpareDescription(),
                                s.getSpareType(),
                                s.getSpareStatus(),
                                s.getPrice(),
                                s.getQuantity(),
                                s.getUpdatedAt() != null ? s.getUpdatedAt() : ""));
                    }

                    String filePath = "spares_report.csv";
                    try (OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream(filePath, false), StandardCharsets.UTF_8)) {
                        writer.write(csv.toString());
                    }

                    System.out.println("CSV-файл успешно создан: " + filePath);
                });
    }
}
