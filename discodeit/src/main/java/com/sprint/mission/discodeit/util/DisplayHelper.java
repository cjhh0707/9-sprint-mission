package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public class DisplayHelper {

    // 구분선
    private static final String SEPARATOR = "─────────────────────";

    //성공 메시지
    public static void showSuccess(String message) {
        System.out.println("\n" + message);
    }

    //에러 메시지
    public static void showError(String message) {
        System.out.println("\n" + message);
    }

    //취소 메시지
    public static void showCancelled() {
        System.out.println("\n취소되었습니다.");
    }

    //삭제 성공 메시지
    public static void showDeleteSuccess(String itemType, int relatedCount, String relatedType) {
        System.out.println("\n" + itemType + "이(가) 삭제되었습니다.");
        if (relatedCount > 0) {
            System.out.println("관련 " + relatedType + " " + relatedCount + "개도 함께 삭제되었습니다.");
        }
    }

    //사용자 상세 정보
    public static void printUserDetail(User user) {
        System.out.println(SEPARATOR);
        System.out.println("이름: " + user.getDisplayName());
        System.out.println("이메일: " + user.getEmail());
        System.out.println("전화번호: " + user.getPhoneNumber());
        System.out.println("상태: " + user.getStatus());
        System.out.println("생성일: " + user.getCreatedAt());
        System.out.println("수정일: " + user.getUpdatedAt());
    }

    //채널 상세 정보
    public static void printChannelDetail(Channel channel) {
        System.out.println(SEPARATOR);
        System.out.println("채널명: " + channel.getChannelName());
        System.out.println("설명: " + channel.getDescription());
        System.out.println("생성일: " + channel.getCreatedAt());
        System.out.println("수정일: " + channel.getUpdatedAt());
    }

    //수정 중
    public static void showEditingHeader(String name, String itemType) {
        System.out.println("\n[ " + name + " " + itemType + " 수정 중 ]");
    }

    //수정 완료 메시지
    public static void showEditComplete(String itemType) {
        System.out.println("\n✅ " + itemType + " 정보 수정이 완료되었습니다.");
    }
}