package com.cloudmessenger.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cloudmessenger.model.Group;
import com.cloudmessenger.model.Message;
import com.cloudmessenger.model.User;
import com.cloudmessenger.service.FileStorageService;
import com.cloudmessenger.service.GroupService;
import com.cloudmessenger.service.MessageService;
import com.cloudmessenger.service.UserService;

/**
 * Контроллер для управления групповыми чатами
 */
@Controller
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;
    private final MessageService messageService;
    private final FileStorageService fileStorageService;
    
    @Autowired
    public GroupController(GroupService groupService, 
                           UserService userService, 
                           MessageService messageService,
                           FileStorageService fileStorageService) {
        this.groupService = groupService;
        this.userService = userService;
        this.messageService = messageService;
        this.fileStorageService = fileStorageService;
    }
    
    /**
     * Отображает страницу со списком групп пользователя
     */
    @GetMapping
    public String listGroups(Model model, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        List<Group> userGroups = groupService.findByMember(currentUser.getId());
        
        model.addAttribute("groups", userGroups);
        model.addAttribute("currentUser", currentUser);
        
        return "groups/list";
    }
    
    /**
     * Отображает страницу для создания новой группы
     */
    @GetMapping("/create")
    public String showCreateGroupForm(Model model) {
        return "groups/create";
    }
    
    /**
     * Создает новую группу
     */
    @PostMapping("/create")
    public String createGroup(@RequestParam String name,
                             @RequestParam(required = false) String description,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        
        try {
            Group group = groupService.createGroup(name, description, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Группа успешно создана");
            return "redirect:/groups/" + group.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании группы: " + e.getMessage());
            return "redirect:/groups/create";
        }
    }
    
    /**
     * Отображает страницу группового чата
     */
    @GetMapping("/{groupId}")
    public String viewGroup(@PathVariable Long groupId, 
                           Model model, 
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        // Проверяем, существует ли группа
        if (group == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Группа не найдена");
            return "redirect:/groups";
        }
        
        // Проверяем, является ли пользователь участником группы
        if (!group.isMember(currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет доступа к этой группе");
            return "redirect:/groups";
        }
        
        // Получаем последние сообщения группы
        List<Message> messages = messageService.getGroupMessages(groupId, 50, 0);
        
        model.addAttribute("group", group);
        model.addAttribute("messages", messages);
        model.addAttribute("members", group.getMembers());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isCreator", group.getCreator().getId().equals(currentUser.getId()));
        
        return "groups/view";
    }
    
    /**
     * Получает сообщения группы
     */
    @GetMapping("/{groupId}/messages")
    @ResponseBody
    public List<Message> getGroupMessages(@PathVariable Long groupId,
                                         @RequestParam(defaultValue = "20") int limit,
                                         @RequestParam(defaultValue = "0") int offset,
                                         Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        
        // Проверяем доступ пользователя к группе
        if (!groupService.isMember(groupId, currentUser.getId())) {
            return List.of(); // Возвращаем пустой список, если пользователь не имеет доступа
        }
        
        return messageService.getGroupMessages(groupId, limit, offset);
    }
    
    /**
     * Отправляет текстовое сообщение в группу
     */
    @PostMapping("/{groupId}/send/text")
    @ResponseBody
    public Message sendGroupTextMessage(@PathVariable Long groupId,
                                      @RequestParam String content,
                                      Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        // Проверяем доступ пользователя к группе
        if (group == null || !group.isMember(currentUser)) {
            throw new IllegalArgumentException("У вас нет доступа к этой группе");
        }
        
        return messageService.createGroupTextMessage(currentUser, group, content);
    }
    
    /**
     * Отправляет медиа-сообщение в группу
     */
    @PostMapping("/{groupId}/send/media")
    @ResponseBody
    public Message sendGroupMediaMessage(@PathVariable Long groupId,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(required = false) String text,
                                       Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        // Проверяем доступ пользователя к группе
        if (group == null || !group.isMember(currentUser)) {
            throw new IllegalArgumentException("У вас нет доступа к этой группе");
        }
        
        Message message = messageService.createGroupMediaMessage(currentUser, group, file);
        
        // Если есть текст, обновляем его
        if (text != null && !text.trim().isEmpty()) {
            message.setContent(text);
            messageService.updateMessageContent(message.getId(), text);
        }
        
        return message;
    }
    
    /**
     * Отображает страницу для управления участниками группы
     */
    @GetMapping("/{groupId}/members")
    public String manageGroupMembers(@PathVariable Long groupId,
                                   Model model,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        // Проверяем существование группы
        if (group == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Группа не найдена");
            return "redirect:/groups";
        }
        
        // Проверяем, является ли пользователь участником группы
        if (!group.isMember(currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет доступа к этой группе");
            return "redirect:/groups";
        }
        
        model.addAttribute("group", group);
        model.addAttribute("members", group.getMembers());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isCreator", group.getCreator().getId().equals(currentUser.getId()));
        
        return "groups/members";
    }
    
    /**
     * Добавляет пользователя в группу
     */
    @PostMapping("/{groupId}/members/add")
    public String addMember(@PathVariable Long groupId,
                          @RequestParam String email,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        // Проверяем существование группы
        if (group == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Группа не найдена");
            return "redirect:/groups";
        }
        
        // Проверяем, является ли текущий пользователь создателем группы
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Только создатель группы может добавлять участников");
            return "redirect:/groups/" + groupId + "/members";
        }
        
        // Ищем пользователя по email
        User userToAdd = userService.findByEmail(email);
        if (userToAdd == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь с email " + email + " не найден");
            return "redirect:/groups/" + groupId + "/members";
        }
        
        // Добавляем пользователя в группу
        if (groupService.addMember(groupId, userToAdd.getId())) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь " + userToAdd.getName() + " добавлен в группу");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось добавить пользователя в группу");
        }
        
        return "redirect:/groups/" + groupId + "/members";
    }
    
    /**
     * Удаляет пользователя из группы
     */
    @PostMapping("/{groupId}/members/{userId}/remove")
    public String removeMember(@PathVariable Long groupId,
                             @PathVariable Long userId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        // Проверяем существование группы
        if (group == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Группа не найдена");
            return "redirect:/groups";
        }
        
        // Проверяем, является ли текущий пользователь создателем группы
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Только создатель группы может удалять участников");
            return "redirect:/groups/" + groupId + "/members";
        }
        
        // Нельзя удалить создателя группы
        if (group.getCreator().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Невозможно удалить создателя группы");
            return "redirect:/groups/" + groupId + "/members";
        }
        
        // Удаляем пользователя из группы
        if (groupService.removeMember(groupId, userId)) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь удален из группы");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось удалить пользователя из группы");
        }
        
        return "redirect:/groups/" + groupId + "/members";
    }
    
    /**
     * Выход пользователя из группы
     */
    @PostMapping("/{groupId}/leave")
    public String leaveGroup(@PathVariable Long groupId,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        // Проверяем существование группы
        if (group == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Группа не найдена");
            return "redirect:/groups";
        }
        
        // Создатель не может покинуть группу, он может только удалить ее
        if (group.getCreator().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Создатель не может покинуть группу. Вы можете удалить группу.");
            return "redirect:/groups/" + groupId;
        }
        
        // Удаляем пользователя из группы
        if (groupService.removeMember(groupId, currentUser.getId())) {
            redirectAttributes.addFlashAttribute("successMessage", "Вы вышли из группы");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось выйти из группы");
        }
        
        return "redirect:/groups";
    }
    
    /**
     * Удаление группы
     */
    @PostMapping("/{groupId}/delete")
    public String deleteGroup(@PathVariable Long groupId,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        
        // Проверяем, является ли пользователь создателем группы
        if (!groupService.isCreator(groupId, currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Только создатель может удалить группу");
            return "redirect:/groups/" + groupId;
        }
        
        try {
            groupService.deleteGroup(groupId);
            redirectAttributes.addFlashAttribute("successMessage", "Группа успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении группы: " + e.getMessage());
        }
        
        return "redirect:/groups";
    }
    
    /**
     * Получает информацию о группе в формате JSON
     */
    @GetMapping("/{groupId}/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGroupInfo(@PathVariable Long groupId,
                                                         Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        Group group = groupService.findById(groupId);
        
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Группа не найдена"));
        }
        
        if (!group.isMember(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "У вас нет доступа к этой группе"));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", group.getId());
        response.put("name", group.getName());
        response.put("description", group.getDescription());
        response.put("creatorId", group.getCreator().getId());
        response.put("creatorName", group.getCreator().getName());
        response.put("createdAt", group.getCreatedAt());
        response.put("membersCount", group.getMembers().size());
        response.put("isCreator", group.getCreator().getId().equals(currentUser.getId()));
        
        // Информация о участниках
        List<Map<String, Object>> membersInfo = group.getMembers().stream()
            .map(member -> {
                Map<String, Object> memberInfo = new HashMap<>();
                memberInfo.put("id", member.getId());
                memberInfo.put("name", member.getName());
                memberInfo.put("email", member.getEmail());
                memberInfo.put("isCreator", member.getId().equals(group.getCreator().getId()));
                return memberInfo;
            })
            .collect(Collectors.toList());
        
        response.put("members", membersInfo);
        
        return ResponseEntity.ok(response);
    }
} 