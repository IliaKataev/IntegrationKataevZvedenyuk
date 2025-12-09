package com.ilia.routes;

import com.ilia.model.Spare;
import com.ilia.repositories.SpareRepository;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbToCsvRoute extends RouteBuilder {

    private final SpareRepository repository;

    public DbToCsvRoute(SpareRepository repository) {
        this.repository = repository;
    }

    @Override
    public void configure() {

        CsvDataFormat csv = new CsvDataFormat();
        csv.setDelimiter(';');
        csv.setUseMaps(false);

        from("timer:exportCsv?repeatCount=1")
                .process(exchange -> {
                    List<Spare> spares = repository.findAll();

                    List<List<String>> csvData = spares.stream().map(spare -> List.of(
                            spare.getSpareCode() != null ? spare.getSpareCode() : "",
                            spare.getSpareName() != null ? spare.getSpareName() : "",
                            spare.getSpareDescription() != null ? spare.getSpareDescription() : "",
                            spare.getSpareType() != null ? spare.getSpareType() : "",
                            spare.getSpareStatus() != null ? spare.getSpareStatus() : "",
                            spare.getPrice() != null ? spare.getPrice().toString() : "0",
                            String.valueOf(spare.getQuantity()),
                            spare.getUpdatedAt() != null ? spare.getUpdatedAt() : "")).toList();

                    exchange.getIn().setBody(csvData);
                })
                .marshal(csv)
                .setHeader("CamelFileName", constant("spares_report.csv"))
                .to("file:output?fileExist=Override");
    }
}
