package ru.yandex.practicum.utils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.time.Instant;

public class InstantAdapter extends TypeAdapter<Instant> {

    @Override
    public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
        jsonWriter.value(instant.toEpochMilli());
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        var timeAsString = jsonReader.nextString();
        var timeAsLong = Long.parseLong(timeAsString);

        return Instant.ofEpochMilli(timeAsLong);

    }

}
