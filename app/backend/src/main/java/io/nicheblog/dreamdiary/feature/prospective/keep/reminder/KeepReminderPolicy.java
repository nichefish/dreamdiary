package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import java.time.*;
import java.util.List;
import java.util.Optional;

/** 기기 로컬 날짜·아침/저녁과 현재 Keep 상태로 고정 리마인드 대상을 판정한다. */
public final class KeepReminderPolicy {
    private KeepReminderPolicy() {}

    /** 서버 발송 슬롯. 분 단위 판정으로 지난 시점을 소급하지 않는다. */
    public enum Slot { MORNING, EVENING }

    /** 현재 기기에서 발송 가능한 로컬 날짜·슬롯. */
    public record Window(LocalDate date, Slot slot) {}

    /** 캐시 없이 조회한 발송 대상 후보. 개인 본문·제목을 전달하지 않는다. */
    public record Item(int id, LocalDate due, boolean important) {}

    /** IANA 시간대를 적용한 09:00 또는 19:00 분에만 슬롯을 반환한다. */
    public static Optional<Window> window(Instant now, String zone) {
        ZonedDateTime local = now.atZone(ZoneId.of(zone));
        if (local.getMinute() != 0) return Optional.empty();
        if (local.getHour() == 9) return Optional.of(new Window(local.toLocalDate(), Slot.MORNING));
        if (local.getHour() == 19) return Optional.of(new Window(local.toLocalDate(), Slot.EVENING));
        return Optional.empty();
    }

    /** 아침은 당일·중요 D-3, 저녁은 당일·D-1을 선택한다. */
    public static List<Item> select(List<Item> items, Window window) {
        return items.stream().filter(item -> item.due() != null && (
                item.due().equals(window.date())
                || window.slot() == Slot.MORNING && item.important() && item.due().equals(window.date().plusDays(3))
                || window.slot() == Slot.EVENING && item.due().equals(window.date().plusDays(1))
        )).toList();
    }

    /** 잠금 화면 요약은 개인 제목 없이 상대 날짜별 개수만 표시한다. */
    public static String summary(List<Item> items, Window window) {
        long today = items.stream().filter(i -> i.due().equals(window.date())).count();
        long tomorrow = items.stream().filter(i -> i.due().equals(window.date().plusDays(1))).count();
        long three = items.stream().filter(i -> i.due().equals(window.date().plusDays(3))).count();
        return "오늘 기한 " + today + "개 · 내일 " + tomorrow + "개 · 3일 후 " + three + "개";
    }
}