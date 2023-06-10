package com.guyshalev.ZestyRnDTask.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guyshalev.ZestyRnDTask.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.Instance;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;


@Service
public class ETL {
    public static final String CANNOT_ACCESS_FILE = "Cannot access file in path: ";
    public static final String CANNOT_SAVE_FILE = "Cannot save file";
    public static final String CANNOT_CONVERT_FILE = "Cannot convert file to Json";
    public static final String FILE_IS_EMPTY = "File is empty";
    public static final String REGIONS_DELIMITER = ",";
    public static final String REGIONS_FILE_NAME = "regions.txt";
    public static final String FILE_SAVE_NAME = "$region_name.json";

    @Autowired
    private AWS aws;

    /**
     * List all EC2 instances (servers) in a particular region of AWS, and Save the result in JSON format to a file
     */
    public void doETLProcess() {
        List<String> regions = getRegions();
        List<Instance> instances;

        if (isNotEmpty(regions)) {
            instances = regions.stream()
                    .map(region -> aws.getInstances(region))
                    .flatMap(List::stream)
                    .toList();

            instances = aws.sortByLaunchTime(instances);
            saveToFile(instances);
        }
    }

    /**
     * Save a list of AWS instances in JSON format to a file.
     *
     * @param instances -  a list of AWS Instances
     */
    private void saveToFile(List<Instance> instances) {
        try {
            // Convert instances to JSON format
            ObjectMapper objectMapper = new ObjectMapper();
            String file = objectMapper.writeValueAsString(instances);

            // Save JSON data to a file
            FileWriter fileWriter = new FileWriter(FILE_SAVE_NAME);
            fileWriter.close();
            fileWriter.write(file);
        } catch (JsonProcessingException e) {
            System.out.println(CANNOT_CONVERT_FILE + " Because of: "
                    + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            System.out.println(CANNOT_SAVE_FILE + " Because of: "
                    + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Get a list of regions from file
     *
     * @return A list of regions from file
     */
    private List<String> getRegions() {
        Path path = Paths.get("src/main/resources/" + REGIONS_FILE_NAME);
        String file;
        List<String> regions = new ArrayList<>();

        try {
            file = Files.readAllLines(path).get(0);
            if (isBlank(file)) {
                System.out.println(FILE_IS_EMPTY);
            } else {
                //aws.getInstances(file);
                regions = Utils.splitStringByDelimiter(file, REGIONS_DELIMITER);
            }
        } catch (Exception e) {
            System.out.println(CANNOT_ACCESS_FILE + path);
        }

        return regions;
    }


}
