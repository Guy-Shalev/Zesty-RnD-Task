package com.guyshalev.ZestyRnDTask.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.Instance;

import java.util.List;

@Service
public interface IAWS {

    List<Instance> getInstances(String regionName);

    List<Instance> sortByLaunchTime(List<Instance> instances);

}
