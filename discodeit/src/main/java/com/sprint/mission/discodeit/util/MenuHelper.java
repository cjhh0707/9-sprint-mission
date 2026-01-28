package com.sprint.mission.discodeit.util;

import java.util.List;
import java.util.function.Function;

public class MenuHelper {
    //항목 선택
    public static <T> T selectFromList(
            List<T> items,
            Function<T, String> displayMapper,
            String title
    ) {
        if (items.isEmpty()) {
            System.out.println("\n 항목이 없습니다");
            return null;
        }

        System.out.println("\n" + title);
        for (int i = 0; i < items.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + displayMapper.apply(items.get(i)));
        }
        System.out.println("   0. 취소");

        int choice = InputUtil.getIntInRange("선택: ", 0, items.size());

        if (choice == 0) {
            System.out.println("취소");
            return null;
        }
        return items.get(choice-1);
    }

    //삭제 확인 메시지
    public static boolean confirmDelete(String name, int relatedCount, String relatedType) {
        System.out.println("'" + name + "' 을 삭제하면");
        if (relatedCount > 0) {
            System.out.println("관련 " + relatedType + " " + relatedCount + "개도 함께 삭제됩니다");
        }
        return InputUtil.confirm("\n 삭제하시겠습니까?");
    }

    //상세 조회 옵션
    public static <T> void viewWithDetailOption(
            List<T> items,
            Function<T, String> displayMapper,
            java.util.function.Consumer<T> detailPrinter,
            String itemName
    ) {
        if (items.isEmpty()) {
            System.out.println("\n등록된 " + itemName + "가 없습니다.");
            return;
        }

        System.out.println("\n" + itemName + " 목록");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + displayMapper.apply(items.get(i)));
        }
        System.out.println("   99. 전체 " + itemName + " 상세 조회");
        System.out.println("   0. 취소");

        int choice = InputUtil.getIntInRange("조회할 " + itemName + " 선택: ", 0, 99);

        if (choice == 0) {
            return;
        } else if (choice == 99) {
            System.out.println("\n=== 전체 " + itemName + " 상세 정보 ===");
            items.forEach(detailPrinter);
        } else if (choice <= items.size()) {
            System.out.println("\n=== " + itemName + " 상세 정보 ===");
            detailPrinter.accept(items.get(choice - 1));
        }
    }
}
