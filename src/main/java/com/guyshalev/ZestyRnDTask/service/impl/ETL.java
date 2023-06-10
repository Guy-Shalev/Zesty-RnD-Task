package com.guyshalev.ZestyRnDTask.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guyshalev.ZestyRnDTask.service.IAWS;
import com.guyshalev.ZestyRnDTask.service.IETL;
import com.guyshalev.ZestyRnDTask.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.Instance;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * Given more time I would have separated the file reading and writing code to a separated class to handle it,
 * so it can bve reused in both functions
 */
@Service
public class ETL implements IETL {
    private static final Logger LOG = LoggerFactory.getLogger(ETL.class);

    public static final String CANNOT_ACCESS_FILE = "Cannot access file in path: ";
    public static final String CANNOT_SAVE_FILE = "Cannot save file";
    public static final String CANNOT_CONVERT_FILE = "Cannot convert file to Json";
    public static final String FILE_IS_EMPTY = "File is empty";
    public static final String REGIONS_DELIMITER = ",";
    public static final String REGIONS_FILE_NAME = "regions.txt";
    public static final String FILE_SAVE_NAME = "$region_name.json";
    public static final String FILES_PATH = "src/main/resources/";

    @Autowired
    private IAWS aws;

    /**
     * List all EC2 instances (servers) in a particular region of AWS, and Save the result in JSON format to a file
     */
    @Override
    public void doETLProcess() {
        List<String> regions = getRegions();
//        List<Instance> instances;
        Map<String, List<Instance>> regionInstances = new HashMap<>();

        if (isNotEmpty(regions)) {
            regions.forEach(region -> {
                List<Instance> instances = aws.sortByLaunchTime(aws.getInstances(region));
                regionInstances.put(region, instances);
            });
            saveToFile(regionInstances);
        }
    }

    /**
     * Save a list of AWS instances in JSON format to a file.
     *
     * @param regionInstances -  a map of AWS regions with their instances
     */
    private void saveToFile(Map<String, List<Instance>> regionInstances) {
        try {
            // Convert instances to JSON format
            ObjectMapper objectMapper = new ObjectMapper();
            String file = objectMapper.writeValueAsString(regionInstances);

            // Save JSON data to a file
            FileWriter fileWriter = new FileWriter(FILE_SAVE_NAME);
            fileWriter.close();
            fileWriter.write(file);
        } catch (JsonProcessingException e) {
            LOG.error(CANNOT_CONVERT_FILE + " Because of: "
                    + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            LOG.error(CANNOT_SAVE_FILE + " Because of: "
                    + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Get a list of regions from file
     *
     * @return A list of regions from file
     */
    private List<String> getRegions() {
        Path path = Paths.get(FILES_PATH + REGIONS_FILE_NAME);
        String file;
        List<String> regions = new ArrayList<>();

        try {
            file = Files.readAllLines(path).get(0);
            if (isBlank(file)) {
                LOG.error(FILE_IS_EMPTY);
            } else {
                //aws.getInstances(file);
                regions = Utils.splitStringByDelimiter(file, REGIONS_DELIMITER);
            }
        } catch (Exception e) {
            LOG.error(CANNOT_ACCESS_FILE + path);
        }

        return regions;
    }

    /**
     * Returns a list fo AWS instances for the given region, that was saved in a previous process
     *
     * @param region - An AWS region name
     * @return a list fo AWS instances for the given region
     */
    @Override
    public String getInstances(String region) {
        // Do to time constrains and converting the docker access to Java, this part was left unfinished
        // However, what I would have done is to call the file (and return a log error if not found),
        // convert it to an object and get the list of instances for the given region.
        return null;
    }
}
