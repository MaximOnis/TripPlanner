package com.example.tripplanner.utils

import java.time.LocalDate

fun isExpired(endDate: LocalDate): Boolean {
    return endDate.isBefore(LocalDate.now())
}
