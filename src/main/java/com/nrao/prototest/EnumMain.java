package com.nrao.prototest;

import example.enumerations.EnumExample;

public class EnumMain {
    public static void main(String[] args) {
        System.out.println("Enum Example!");
        EnumExample.EnumMessage enumMessage = EnumExample.EnumMessage.newBuilder()
                .setDayOfTheWeek(EnumExample.DayOfTheWeek.FRIDAY).build();
        System.out.println(enumMessage);
    }
}
