package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** 개인 기록 대신 가상 날짜·ID로 고정 알림 규칙과 시간대 경계를 검증한다. */
class KeepReminderPolicyTest {
    private static final LocalDate FIXTURE_DAY = LocalDate.of(2001, 10, 24);
    private static final List<KeepReminderPolicy.Item> FIXTURE_ITEMS = List.of(
            new KeepReminderPolicy.Item(101, FIXTURE_DAY, false),
            new KeepReminderPolicy.Item(102, FIXTURE_DAY.plusDays(1), false),
            new KeepReminderPolicy.Item(103, FIXTURE_DAY.plusDays(2), true),
            new KeepReminderPolicy.Item(104, FIXTURE_DAY.plusDays(3), true),
            new KeepReminderPolicy.Item(105, FIXTURE_DAY.plusDays(3), false),
            new KeepReminderPolicy.Item(106, null, true),
            new KeepReminderPolicy.Item(107, FIXTURE_DAY.minusDays(1), true));

    @Test void morningIncludesTodayAndImportantThreeDaysOnly() {
        var window = new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.MORNING);
        assertEquals(List.of(101, 104), KeepReminderPolicy.select(FIXTURE_ITEMS, window).stream().map(KeepReminderPolicy.Item::id).toList());
    }

    @Test void eveningIncludesTodayAndTomorrowOnly() {
        var window = new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.EVENING);
        assertEquals(List.of(101, 102), KeepReminderPolicy.select(FIXTURE_ITEMS, window).stream().map(KeepReminderPolicy.Item::id).toList());
    }

    @Test void localMinuteAndMidnightAreIndependentOfServerZone() {
        assertTrue(KeepReminderPolicy.window(Instant.parse("2001-10-24T00:00:59Z"), "Asia/Seoul").isPresent());
        assertTrue(KeepReminderPolicy.window(Instant.parse("2001-10-24T00:01:00Z"), "Asia/Seoul").isEmpty());
        var losAngeles = KeepReminderPolicy.window(Instant.parse("2001-10-24T02:00:00Z"), "America/Los_Angeles").orElseThrow();
        assertEquals(FIXTURE_DAY.minusDays(1), losAngeles.date());
        assertEquals(KeepReminderPolicy.Slot.EVENING, losAngeles.slot());
    }

    @Test void daylightSavingAndCountsUseDeviceTime() {
        assertTrue(KeepReminderPolicy.window(Instant.parse("2001-07-24T13:00:00Z"), "America/New_York").isPresent());
        assertTrue(KeepReminderPolicy.window(Instant.parse("2001-01-24T14:00:00Z"), "America/New_York").isPresent());
        var window = new KeepReminderPolicy.Window(FIXTURE_DAY, KeepReminderPolicy.Slot.MORNING);
        assertEquals("오늘 기한 1개 · 내일 0개 · 3일 후 1개", KeepReminderPolicy.summary(KeepReminderPolicy.select(FIXTURE_ITEMS, window), window));
    }
}