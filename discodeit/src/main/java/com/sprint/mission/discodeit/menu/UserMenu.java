package com.sprint.mission.discodeit.menu;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.DisplayHelper;
import com.sprint.mission.discodeit.util.InputUtil;
import com.sprint.mission.discodeit.util.MenuHelper;

public class UserMenu {
    private final UserService userService;
    private final MessageService messageService;

    public UserMenu(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    public void show() {
        while (true) {
            try {
                System.out.println("\n사용자 관리");
                System.out.println("1) 사용자 조회");
                System.out.println("2) 사용자 수정");
                System.out.println("3) 사용자 삭제");
                System.out.println("0) 뒤로가기");

                int choice = InputUtil.getIntInRange("선택: ", 0, 3);

                switch (choice) {
                    case 1:
                        viewUsers();
                        break;
                    case 2:
                        updateUser();
                        break;
                    case 3:
                        deleteUser();
                        break;
                    case 0:
                        return;
                }
            } catch (Exception e) {
                DisplayHelper.showError("오류 발생: " + e.getMessage());
            }
        }
    }

    // 사용자 조회
    private void viewUsers() {
        MenuHelper.viewWithDetailOption(
                userService.findAll(),
                user -> user.getDisplayName() + " (" + user.getEmail() + ")",
                DisplayHelper::printUserDetail,
                "사용자"
        );
    }

    // 사용자 수정
    private void updateUser() {
        User user = MenuHelper.selectFromList(
                userService.findAll(),
                User::getDisplayName,
                "수정할 사용자 선택"
        );

        if (user == null) return;

        // 필드별 수정 루프
        while (true) {
            DisplayHelper.showEditingHeader(user.getDisplayName(), "님");
            System.out.println("1. 이름 (현재: " + user.getDisplayName() + ")");
            System.out.println("2. 이메일 (현재: " + user.getEmail() + ")");
            System.out.println("3. 전화번호 (현재: " + user.getPhoneNumber() + ")");
            System.out.println("4. 상태 (현재: " + user.getStatus() + ")");
            System.out.println("0. 수정 완료");

            int fieldChoice = InputUtil.getIntInRange("선택: ", 0, 4);

            if (fieldChoice == 0) {
                DisplayHelper.showEditComplete("사용자");
                break;
            }

            String input = getFieldInput(fieldChoice);
            if (input != null) {
                updateUserField(user, fieldChoice, input);
                user = userService.findById(user.getId()); // 갱신
            }
        }
    }

    private String getFieldInput(int fieldChoice) {
        String prompt;
        switch (fieldChoice) {
            case 1:
                prompt = "새 이름: ";
                break;
            case 2:
                prompt = "새 이메일: ";
                break;
            case 3:
                prompt = "새 전화번호: ";
                break;
            case 4:
                prompt = "새 상태: ";
                break;
            default:
                prompt = null;
                break;
        }
        return prompt != null ? InputUtil.getStringOrCancel(prompt) : null;
    }

    private void updateUserField(User user, int fieldChoice, String newValue) {
        switch (fieldChoice) {
            case 1:
                userService.update(user.getId(), newValue, user.getEmail(),
                        user.getPhoneNumber(), user.getStatus());
                break;
            case 2:
                userService.update(user.getId(), user.getDisplayName(), newValue,
                        user.getPhoneNumber(), user.getStatus());
                break;
            case 3:
                userService.update(user.getId(), user.getDisplayName(), user.getEmail(),
                        newValue, user.getStatus());
                break;
            case 4:
                userService.update(user.getId(), user.getDisplayName(), user.getEmail(),
                        user.getPhoneNumber(), newValue);
                break;
        }
    }

    // 사용자 삭제
    private void deleteUser() {
        User user = MenuHelper.selectFromList(
                userService.findAll(),
                User::getDisplayName,
                "삭제할 사용자 선택"
        );

        if (user == null) return;

        int messageCount = messageService.findByAuthorId(user.getId()).size();

        if (MenuHelper.confirmDelete(user.getDisplayName(), messageCount, "메시지")) {
            int deletedMessages = messageService.deleteByAuthorId(user.getId());
            userService.delete(user.getId());
            DisplayHelper.showDeleteSuccess("사용자", deletedMessages, "메시지");
        } else {
            DisplayHelper.showCancelled();
        }
    }
}