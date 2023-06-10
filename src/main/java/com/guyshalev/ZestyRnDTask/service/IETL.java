package com.guyshalev.ZestyRnDTask.service;

import org.springframework.stereotype.Service;

@Service
public interface IETL {

    void doETLProcess();

    String getInstances(String region);
}
