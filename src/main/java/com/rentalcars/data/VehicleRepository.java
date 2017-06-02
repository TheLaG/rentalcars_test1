package com.rentalcars.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcars.data.dto.Vehicle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Sergey on 02-Jun-17.
 */
public class VehicleRepository {

    private static String VEHICLE_URL;
    private static boolean WEB;
    private static boolean INTERNAL;
    ObjectMapper objectMapper = null;

    public VehicleRepository(String url, boolean web, boolean internal) throws MalformedURLException {
        VEHICLE_URL = url;
        WEB = web;
        INTERNAL = internal;
    }

    public List<Vehicle> getVehicles() {
        try {
            if(VEHICLE_URL == null) return null;

            BufferedReader reader = null;

            if(WEB) reader = new BufferedReader(new InputStreamReader(new URL(VEHICLE_URL).openStream()));
            else if (INTERNAL) reader = new BufferedReader(new InputStreamReader(VehicleRepository.class.getClassLoader().getResourceAsStream("vehicles.json")));
            else reader =  new BufferedReader(new FileReader(VEHICLE_URL));

            String json = reader.lines().collect(Collectors.joining());

            if (objectMapper == null) objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(json).path("Search").path("VehicleList");

            if(node.isMissingNode()) {
                System.out.println("Could not find VehicleList...");
                return null;
            }

            return StreamSupport.stream(node.spliterator(), false)
                    .collect(Collectors.toList())
                    .stream()
                    .map(this::jsonNodeToVehicle)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Vehicle jsonNodeToVehicle(JsonNode vehicleNode) {
        try {
            return objectMapper.treeToValue(vehicleNode, Vehicle.class);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
