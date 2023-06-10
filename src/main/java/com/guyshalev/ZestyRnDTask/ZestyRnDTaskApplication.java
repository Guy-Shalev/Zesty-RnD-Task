package com.guyshalev.ZestyRnDTask;

import com.guyshalev.ZestyRnDTask.service.impl.ETL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AutoConfiguration
public class ZestyRnDTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZestyRnDTaskApplication.class, args);

        ETL etl = new ETL();
        etl.doETLProcess();

    }

}
