package com.example.sclad.models;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumHelper {

    public static List<String> getDevicesTitleList() {
        return Stream
                .of(DeviceType.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
    }

}
