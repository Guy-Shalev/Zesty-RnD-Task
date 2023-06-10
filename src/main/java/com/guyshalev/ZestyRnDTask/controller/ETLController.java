package com.guyshalev.ZestyRnDTask.controller;

import com.guyshalev.ZestyRnDTask.service.IETL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ETL")
public class ETLController {

    @Autowired
    private IETL etl;

    @GetMapping("/instances/{region}")
    public String getInstances(@PathVariable("region") String region) {
        return etl.getInstances(region);
    }


}
