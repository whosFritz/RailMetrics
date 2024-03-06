package de.whosfritz.railinsights.utils;

import de.olech2412.adapter.dbadapter.model.trip.Trip;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Make your life easier with {@link TripUtil}
 * it provides some common tasks for trips
 */
public class TripUtil {

    public static LocalDateTime getDateFromTrip(Trip trip) {
        if (trip.getPlannedWhen() != null) {
            return trip.getPlannedWhen();
        } else if (trip.getWhen() != null) {
            return trip.getWhen();
        } else {
            return trip.getCreatedAt();
        }
    }

    /**
     * Removes duplicates from a list of trips
     *
     * @param trips the list of trips
     * @return a list of trips without duplicates
     */
    public static List<Trip> removeDuplicates(List<Trip> trips) {
        List<Trip> doubledTrips = new ArrayList<>();
        for (int i = 0; i < trips.size(); i++) { // loop through all trips
            for (int j = i + 1; j < trips.size(); j++) { // loop through all trips after the current trip
                if (trips.get(i).getLine().getFahrtNr().equals(trips.get(j).getLine().getFahrtNr())
                        && trips.get(i).getStop().getStopId().equals(trips.get(j).getStop().getStopId())) {
                    doubledTrips.add(trips.get(j));
                }
            }
        }

        // if there are no doubled trips, return the list
        if (doubledTrips.isEmpty()) {
            return trips;
        }

        trips = new ArrayList<>(trips); // Create a mutable copy important for removing elements because the list is unmodifiable
        trips.removeAll(doubledTrips);

        for (Trip trip : trips) {
            long stopId = trip.getStop().getStopId();
            List<Trip> tripsFromDoubledTrips = doubledTrips.stream().filter(t -> t.getStop().getStopId() == stopId).toList();
            if (!tripsFromDoubledTrips.isEmpty()) {
                // search the trip with the highest delay
                Trip tripWithHighestDelay = tripsFromDoubledTrips.stream().max(Comparator.comparingInt(Trip::getDelay)).get();
                tripWithHighestDelay.setTripId(tripWithHighestDelay.getTripId());
                tripWithHighestDelay.setPlannedWhen(trip.getPlannedWhen());

                // replace the old trip with the new trip
                trips.set(trips.indexOf(trip), tripWithHighestDelay);
            }
        }

        // replace the doubled trips with the new doubled trips

        return trips;
    }

    /**
     * Removes duplicates from a list of trips
     *
     * @param trips the list of trips
     * @return a list of trips without duplicates
     */
    public static List<Trip> removeDuplicatesForMultipleLines(List<Trip> trips) {
        List<Trip> doubledTrips = new ArrayList<>();
        for (int i = 0; i < trips.size(); i++) { // loop through all trips
            for (int j = i + 1; j < trips.size(); j++) { // loop through all trips after the current trip
                if (trips.get(i).getLine().getFahrtNr().equals(trips.get(j).getLine().getFahrtNr())
                        && trips.get(i).getPlannedWhen().equals(trips.get(j).getPlannedWhen())) {
                    doubledTrips.add(trips.get(j));
                }
            }
        }
        trips = new ArrayList<>(trips); // Create a mutable copy important for removing elements because the list is unmodifiable
        if (!doubledTrips.isEmpty()) {
            trips.removeAll(doubledTrips);
        }

        for (Trip trip : trips) {
            long stopId = trip.getStop().getStopId();
            String lineId = trip.getLine().getLineId();
            List<Trip> tripsFromDoubledTrips = doubledTrips.stream().filter(t -> t.getStop().getStopId() == stopId && t.getLine().getLineId().equals(lineId)).toList();
            if (!tripsFromDoubledTrips.isEmpty()) {
                // search the trip with the highest delay
                Trip tripWithHighestDelay = tripsFromDoubledTrips.stream().max(Comparator.comparingInt(Trip::getDelay)).get();
                tripWithHighestDelay.setTripId(tripWithHighestDelay.getTripId());
                tripWithHighestDelay.setPlannedWhen(trip.getPlannedWhen());

                // replace the old trip with the new trip
                trips.set(trips.indexOf(trip), tripWithHighestDelay);
            }
        }

        return trips;
    }

    /**
     * converts a localdate to a part of a trip id e.g. 06.03.2024 -> 6032024
     *
     * @param localDate the localdate
     * @return the part of the trip id
     */
    public static String getPartOfTripIdByLocalDate(LocalDate localDate) {
        String day = String.valueOf(localDate.getDayOfMonth());
        String month = String.valueOf(localDate.getMonthValue());
        String year = String.valueOf(localDate.getYear());

        if (month.length() == 1) month = "0" + month;

        return day + month + year;
    }

}
