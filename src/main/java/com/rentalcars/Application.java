package com.rentalcars;

import com.rentalcars.data.VehicleRepository;
import com.rentalcars.data.dto.Vehicle;
import com.rentalcars.services.VehicleService;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by Sergey on 02-Jun-17.
 */
public class Application {

    static String jsonUrl = "http://www.rentalcars.com/js/vehicles.json";

    public static void main(String[] args) throws MalformedURLException {

        String url = null;
        boolean web = false;
        boolean internal = false;

        if (args.length > 0) {
            if (args[0].equals("-f")) {
                // read file from path
                url = args[1];
            }

            if (args[0].equals("-u")) {
                // read from url
                url = args[1];
                web = true;
            }
        } else {
            url = "local";
            internal = true;
        }

        try {
            VehicleRepository vehicleRepository = new VehicleRepository(url, web, internal);
            VehicleService vehicleService = new VehicleService(vehicleRepository);

            List<Vehicle> vehicles = vehicleRepository.getVehicles();
            if (vehicles == null) return;

            vehicleService.printCarsByPriceASC(vehicles);
            System.out.println("--------------------------------------------------------------------------------------");
            vehicleService.printCalculatedSpecification(vehicles);
            System.out.println("--------------------------------------------------------------------------------------");
            vehicleService.printHighestRatedSupplierDESC(vehicles);
            System.out.println("--------------------------------------------------------------------------------------");
            vehicleService.printVehiclesByBrakeDownsDESC(vehicles);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
