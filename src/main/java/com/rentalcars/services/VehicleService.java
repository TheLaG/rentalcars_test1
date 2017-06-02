package com.rentalcars.services;

import com.rentalcars.data.VehicleRepository;
import com.rentalcars.data.dto.Vehicle;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Sergey on 02-Jun-17.
 */
public class VehicleService {

    VehicleRepository vehicleRepository;
    private static final String OUTPUT_TEMPLATE = "%-4s %s";


    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void printCarsByPriceASC(List<Vehicle> vehicles) {
        if (vehicles == null) vehicles = vehicleRepository.getVehicles();

        int[] idx = { 1 };
        vehicles.stream()
                .sorted(Comparator.comparing(Vehicle::getPrice))
                .forEach(vehicle -> printRow(idx[0]++, vehicle.getName(), vehicle.getPrice().toString()));

    }

    public void printCalculatedSpecification(List<Vehicle> vehicles) {
        if (vehicles == null) vehicles = vehicleRepository.getVehicles();

        int[] idx = { 1 };
        vehicles.stream()
                .forEach(vehicle -> printRow(idx[0]++, calculateSpecification(vehicle.getName(), vehicle.getSipp())));
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new HashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void printHighestRatedSupplierDESC(List<Vehicle> vehicles) {
        if (vehicles == null) vehicles = vehicleRepository.getVehicles();

        int[] idx = { 1 };
        vehicles.stream()
                .sorted(Comparator.comparing(Vehicle::getRating).reversed())
                .filter(distinctByKey(vehicle -> getCarType(vehicle.getSipp().substring(0,1))))
                .forEach(vehicle -> printRow(idx[0]++, vehicle.getName(),getCarType(vehicle.getSipp().substring(0,1)), vehicle.getSupplier(), vehicle.getRating().toString()));
    }

    public void printVehiclesByBrakeDownsDESC(List<Vehicle> vehicles) {
        if (vehicles == null) vehicles = vehicleRepository.getVehicles();

        int[] idx = {1};
        vehicles.stream()
                .map(vehicle -> {return new Object() {
                        String name = vehicle.getName();
                        BigDecimal score = getVehicleBrakedownScore(vehicle.getSipp());
                        BigDecimal rating = vehicle.getRating();
                        BigDecimal sum = score.add(rating);
                    };
                })
                .sorted((o1, o2) -> o2.sum.compareTo(o1.sum))
                .forEach(vehicle -> printRow(idx[0]++, vehicle.name, vehicle.score.toString(), vehicle.rating.toString(), vehicle.sum.toString()));
    }

    BigDecimal getVehicleBrakedownScore(String sipp) {
        BigDecimal score = new BigDecimal(0);
        if(sipp.substring(2,3).equals("M")) score = score.add(BigDecimal.ONE);
        else score = score.add(BigDecimal.valueOf(5));

        if (sipp.substring(3,4).equals("R")) score = score.add(BigDecimal.valueOf(2));

        return score;
    }

    String[] calculateSpecification(String name, String sipp) {
        ArrayList<String> carData = new ArrayList<>();
        carData.add(name);
        carData.add(sipp);
        carData.add(getCarType(sipp.substring(0,1)));
        carData.add(getDors(sipp.substring(1,2)));
        carData.add(getTransmission(sipp.substring(2,3)));
        carData.add("Petrol");
        carData.add(getFuelAndAirCon(sipp.substring(3,4)));

        return carData.toArray(new String[]{});
    }

    private void printRow(int counter, String... data) {
        String resultStr = Arrays.asList(data)
                .stream()
//                .collect(Collectors.joining("}-{", "{", "}"));
                .collect(Collectors.joining("-"));
        System.out.println(String.format(OUTPUT_TEMPLATE, counter+".", resultStr));
    }

    String getCarType(String letter) {
        switch (letter) {
            case "M": return "Mini";
            case "E": return "Economy";
            case "C": return "Compact";
            case "I": return "Intermediate";
            case "S": return "Standard";
            case "F": return "Full size";
            case "P": return "Premium";
            case "L": return "Luxury";
            case "X": return "Special";
            default: return "Mini";
        }
    }

    String getDors(String letter) {
        switch (letter) {
            case "B": return "2 doors";
            case "C": return "4 doors";
            case "D": return "5 doors";
            case "W": return "Estate";
            case "T": return "Convertible";
            case "F": return "SUV";
            case "P": return "Pick up";
            case "V": return "Passenger Van";
            default: return "2 doors";
        }
    }

    String getTransmission(String letter) {
        switch (letter) {
            case "M": return "Manual";
            case "A": return "Automatic";
            default: return "Manual";
        }
    }

    String getFuelAndAirCon(String letter) {
        switch (letter) {
            case "N": return "no AC";
            case "R": return "AC";
            default: return "no AC";
        }
    }
}
