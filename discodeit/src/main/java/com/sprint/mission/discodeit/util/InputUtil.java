package com.sprint.mission.discodeit.util;

import java.util.Scanner;

public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    //정수 입력(유효성 검사 포함)
    public static int getInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("값을 입력해주세요.");
                    continue;
                }

                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }

    //범위 내 정수 입력
    public static int getIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = getInt(prompt);

            if (value < min || value > max) {
                System.out.println(min + "~" + max + " 사이의 숫자를 입력해주세요.");
                continue;
            }

            return value;
        }
    }

    //문자열 입력(필수)
    public static String getString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("빈 값을 입력할 수 없습니다.");
                continue;
            }

            return input;
        }
    }

    //문자열 입력(선택, 엔터 입력시 null반환)
    public static String getStringOrCancel(String prompt) {
        System.out.print(prompt + "(취소: 엔터): ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    // 예 아니오 확인
    public static boolean confirm(String message) {
        System.out.print(message + " (y/n): ");
        String input = scanner.nextLine().trim();
        return input.equalsIgnoreCase("y");
    }
}