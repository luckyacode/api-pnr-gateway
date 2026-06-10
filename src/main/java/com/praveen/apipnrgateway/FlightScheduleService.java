package com.praveen.apipnrgateway;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FlightScheduleService {
    private List<DynamicFlight> activeSchedules = new ArrayList<>();

    // Load original file and programmatically build dynamic objects
    public void generateSchedules(String csvPath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { // Skip CSV header row
                    isHeader = false;
                    continue;
                }

                // Use regex split to accurately parse strings with quotes or commas
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Construct and automatically generate scheduling variables
                DynamicFlight scheduledFlight = new DynamicFlight(columns);
                activeSchedules.add(scheduledFlight);
                if(activeSchedules.size()>10)
                    break;
            }
        }
        System.out.println("⚡ Programmatically generated " + activeSchedules.size() + " dynamic flight schedules.");
        activeSchedules.forEach(System.out::println);
    }

    // Feature Query: Search flights chronologically by departure time
    public List<DynamicFlight> getLiveSchedulesBetweenCountries(String from, String to) {
        return activeSchedules.stream()
                .filter(f -> f.getDepartureCountry().equalsIgnoreCase(from)
                        && f.getArrivalCountry().equalsIgnoreCase(to))
                .sorted(Comparator.comparing(DynamicFlight::getDepartureTime)) // Sort from early morning to night
                .collect(Collectors.toList());
    }
}