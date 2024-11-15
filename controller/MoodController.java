package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.MoodEntry;
import model.User;
import contract.DemoFunctionalInterface;
import contract.MoodTrackable;
import database.H2DatabaseConnector;
import exception.DatabaseConnectionException;

public class MoodController implements MoodTrackable {

    private final H2DatabaseConnector h2DatabaseConnector;

    public MoodController() {
        this.h2DatabaseConnector = new H2DatabaseConnector();
        this.h2DatabaseConnector.connect();
        this.h2DatabaseConnector.createStatement();
        this.h2DatabaseConnector.createMoodEntriesTable();
    }

    @Override
    public MoodEntry createMoodEntry(MoodEntry moodEntry) {

        String sql = "insert into moodentries (moodentryid, userid, moods, date, description) values ('%s', '%s', '%s', '%s', '%s')";
        String fSql = String.format(sql, moodEntry.getMoodEntryId(), moodEntry.getUserId(), moodEntry.serializeMoods(),
                moodEntry.getDate(), moodEntry.getDescription());
        int rowsAffected = h2DatabaseConnector.executeSQLUpdate(fSql);

        return moodEntry;

    }

    @Override
    public ArrayList<MoodEntry> readMoodEntries(User user) {
        String sql = "select * from moodentries WHERE userid = '%s'";
        String fSql = String.format(sql, user.getUserId());
        ResultSet resultSet = h2DatabaseConnector.executeSQLRead(fSql);

        ArrayList<MoodEntry> entries = new ArrayList<>();

        try {
            while (resultSet.next()) {
                String moodEntryId = resultSet.getString("moodentryid");
                String userId = resultSet.getString("userid");
                String moods = resultSet.getString("moods");
                String date = resultSet.getString("date");
                String description = resultSet.getString("description");

                MoodEntry moodEntry = new MoodEntry();

                // process moods back to ArrayList
                moodEntry.deserializeMoods(moods);

                moodEntry.setMoodEntryId(String.valueOf(moodEntryId));
                moodEntry.setUserId(String.valueOf(userId));
                moodEntry.setDate(date);
                moodEntry.setDescription(description);

                entries.add(moodEntry);

            }

            return new ArrayList<>(entries); // defensive copying

        } catch (SQLException e) {
            var exception = new DatabaseConnectionException("Error reading ResultSet for query", e);
            System.err.println(exception.getMessage());
            exception.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public MoodEntry readMoodEntry(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readMoodEntry'");
    }

    @Override
    public MoodEntry updateMoodEntry(Long id, MoodEntry moodEntry) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMoodEntry'");
    }

    @Override
    public MoodEntry deleteMoodEntry(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMoodEntry'");
    }

    @Override
    public ArrayList<MoodEntry> filterMoodEntries(User user, LocalDateTime dateToFilterOn, int option) {

        if (user.getMoodHistory().size() == 0) {
            return new ArrayList<>();
        }

        // trying out a custom functional interface to do stuff
        DemoFunctionalInterface dmf = new DemoFunctionalInterface() {
            @Override
            public ArrayList<MoodEntry> doStuff(User user) {
                ArrayList<MoodEntry> history = readMoodEntries(user);
                return history;
            }
        };

        ArrayList<MoodEntry> results = dmf.doStuff(user);

        // Consumer functional interface implemented using anonymous inner class
        // Consumer<MoodEntry> consumer = (moodEntry) -> {
        // System.out.println("calling form filterMoodEntries() printing");
        // System.out.println(moodEntry.toString());
        // };
        // results.forEach(consumer::accept);

        Stream<MoodEntry> mystream = results.stream();

        // create implementation of predicate with anonymous class
        // Predicate<MoodEntry> predicate = new Predicate<MoodEntry>() {

        // @Override
        // public boolean test(MoodEntry moodEntry) {
        // return (option == 1) ?
        // LocalDateTime.parse(moodEntry.getDate()).isAfter(dateToFilterOn)
        // : LocalDateTime.parse(moodEntry.getDate()).isBefore(dateToFilterOn);

        // }

        // };

        // more concise with lambda function but we can do better
        // Predicate<MoodEntry> predicate2 = (MoodEntry moodEntry) -> {
        // return (option == 1) ?
        // LocalDateTime.parse(moodEntry.getDate()).isAfter(dateToFilterOn)
        // : LocalDateTime.parse(moodEntry.getDate()).isBefore(dateToFilterOn);

        // };

        // doing the same thing with lambda function passed to filter
        // mystream = mystream
        // .filter((moodEntry) -> (option == 1) ?
        // LocalDateTime.parse(moodEntry.getDate()).isAfter(dateToFilterOn)
        // : LocalDateTime.parse(moodEntry.getDate()).isBefore(dateToFilterOn));

        // mystream = mystream.filter(predicate);

        // List<MoodEntry> collectedList = mystream.collect(Collectors.toList());

        // return new ArrayList<>(collectedList);

        return new ArrayList<>(results.stream()
                .filter((moodEntry) -> (option == 1) ? LocalDateTime.parse(moodEntry.getDate()).isAfter(dateToFilterOn)
                        : LocalDateTime.parse(moodEntry.getDate()).isBefore(dateToFilterOn))
                .collect(Collectors.toList()));

    }

}
