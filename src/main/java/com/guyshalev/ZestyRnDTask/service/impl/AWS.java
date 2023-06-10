package com.guyshalev.ZestyRnDTask.service.impl;

import com.guyshalev.ZestyRnDTask.service.IAWS;
import com.guyshalev.ZestyRnDTask.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class AWS implements IAWS {
    private static final Logger LOG = LoggerFactory.getLogger(AWS.class);
    public static final String CANNOT_CONNECT_TO_AWS = "Cannot connect to AWS ECS";
    public static final String NO_INSTANCES_FOUND = "No instances where found";

    /**
     * Return a list all EC2 instances (servers) in a particular region of AWS.
     *
     * @param regionName - AWS region name
     * @return a list of AWS Instances for a given region
     */
    @Override
    public List<Instance> getInstances(String regionName) {
        // Set the desired region
        Region region = Region.of(regionName);
        DescribeInstancesResponse response = null;

        // Create an EC2 client
        try (Ec2Client ec2Client = Ec2Client.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            // Create a describe instances request
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();

            // Retrieve the described instances response
            response = ec2Client.describeInstances(request);
        } catch (Exception e) {
            LOG.error(CANNOT_CONNECT_TO_AWS + region + " Because of: "
                    + e.getMessage() + ", " + Arrays.toString(e.getStackTrace()));
        }

        List<Instance> instances = new ArrayList<>();

        if (response == null) {
            LOG.error(NO_INSTANCES_FOUND);
        } else {
            // Extract the list of instances
            instances = response.reservations().stream()
                    .flatMap(reservation -> reservation.instances().stream())
                    .toList();
        }

        return instances;
    }


    /**
     * Sort a list of AWS Instances by their launchTime
     *
     * @param instances -  a list of AWS Instances
     * @return a sorted list
     */
    @Override
    public List<Instance> sortByLaunchTime(List<Instance> instances) {
        return instances.stream()
                .sorted(Comparator.comparing(instance -> Utils.formatTime(instance.launchTime())))
                .toList();
    }
}
