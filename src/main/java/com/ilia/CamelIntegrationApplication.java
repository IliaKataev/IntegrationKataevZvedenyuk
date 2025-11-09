package com.ilia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ilia.model.Spare;
import com.ilia.repositories.SpareRepository;

@SpringBootApplication
public class CamelIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CamelIntegrationApplication.class, args);
    }

    // @Bean тестовый забег
    public CommandLineRunner testDb(SpareRepository repo) {
        return args -> {
            Spare spare = new Spare();
            spare.setSpareCode("S001");
            spare.setSpareName("Тормозная колодка");
            spare.setSpareDescription("Передняя тормозная колодка для авто");
            spare.setSpareType("Тормоза");
            spare.setSpareStatus("ACTIVE");
            spare.setPrice(1200);
            spare.setQuantity(50);
            spare.setUpdatedAt("2");

            repo.save(spare);

            System.out.println("Сохранили в БД: " + repo.findAll());
        };
    }
}
