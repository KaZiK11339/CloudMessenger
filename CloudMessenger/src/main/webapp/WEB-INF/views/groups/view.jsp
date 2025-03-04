<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${group.name} - Cloud Messenger</title>
    
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.0/css/bootstrap.min.css'/>">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value='/webjars/font-awesome/6.4.0/css/all.min.css'/>">
    
    <style>
        body {
            background-color: #f8f9fa;
            height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .content-container {
            flex: 1;
            display: flex;
            flex-direction: column;
            padding: 20px 0;
        }
        
        .chat-container {
            flex: 1;
            display: flex;
            overflow: hidden;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            background-color: #fff;
        }
        
        .group-info-panel {
            width: 300px;
            background-color: #f8f9fa;
            border-right: 1px solid #e9ecef;
            display: flex;
            flex-direction: column;
        }
        
        .group-header {
            padding: 20px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .group-avatar {
            width: 60px;
            height: 60px;
            background-color: #0d6efd;
            border-radius: 50%;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 30px;
            font-weight: bold;
            margin-right: 15px;
        }
        
        .group-name {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .members-list {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
        }
        
        .member-item {
            display: flex;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #f1f1f1;
        }
        
        .member-avatar {
            width: 40px;
            height: 40px;
            background-color: #6c757d;
            border-radius: 50%;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 16px;
            font-weight: bold;
            margin-right: 10px;
        }
        
        .member-name {
            font-size: 14px;
            font-weight: 500;
        }
        
        .member-role {
            font-size: 12px;
            color: #6c757d;
        }
        
        .chat-area {
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }
        
        .chat-header {
            padding: 15px 20px;
            border-bottom: 1px solid #e9ecef;
            background-color: #fff;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        
        .messages-container {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background-color: #f8f9fa;
        }
        
        .message {
            margin-bottom: 15px;
            max-width: 80%;
        }
        
        .message.outgoing {
            margin-left: auto;
        }
        
        .message-content {
            padding: 10px 15px;
            border-radius: 18px;
            position: relative;
            word-wrap: break-word;
        }
        
        .message.incoming .message-content {
            background-color: #f1f1f1;
            color: #212529;
            border-bottom-left-radius: 5px;
        }
        
        .message.outgoing .message-content {
            background-color: #0d6efd;
            color: white;
            border-bottom-right-radius: 5px;
        }
        
        .message-meta {
            display: flex;
            align-items: center;
            margin-top: 5px;
            font-size: 12px;
            color: #6c757d;
        }
        
        .message.outgoing .message-meta {
            justify-content: flex-end;
        }
        
        .message-time {
            margin-right: 5px;
        }
        
        .message-status {
            margin-left: 5px;
        }
        
        .message-media {
            max-width: 100%;
            border-radius: 10px;
            margin-bottom: 5px;
        }
        
        .message-media img {
            max-width: 100%;
            border-radius: 8px;
        }
        
        .message-media audio, 
        .message-media video {
            max-width: 100%;
            border-radius: 8px;
        }
        
        .message-file {
            display: flex;
            align-items: center;
            background-color: rgba(0,0,0,0.05);
            padding: 8px;
            border-radius: 8px;
            margin-bottom: 5px;
        }
        
        .message-file-icon {
            font-size: 24px;
            margin-right: 10px;
        }
        
        .message-file-info {
            flex: 1;
            overflow: hidden;
        }
        
        .message-file-name {
            font-weight: 500;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .message-file-size {
            font-size: 12px;
            color: #6c757d;
        }
        
        .message-file-download {
            margin-left: 10px;
        }
        
        .input-area {
            padding: 15px;
            background-color: #fff;
            border-top: 1px solid #e9ecef;
        }
        
        .message-form {
            display: flex;
            align-items: center;
        }
        
        .message-input {
            flex: 1;
            border-radius: 24px;
            padding: 10px 15px;
            resize: none;
            overflow-y: auto;
            max-height: 100px;
        }
        
        .message-form .btn-attach {
            background-color: transparent;
            border: none;
            color: #6c757d;
            font-size: 20px;
            padding: 0 15px;
            cursor: pointer;
        }
        
        .message-form .btn-send {
            border-radius: 50%;
            width: 44px;
            height: 44px;
            padding: 0;
            margin-left: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .file-preview {
            margin-bottom: 10px;
            padding: 10px;
            background-color: #f1f1f1;
            border-radius: 8px;
            display: none;
        }
        
        .file-preview img {
            max-height: 150px;
            border-radius: 8px;
        }
        
        .typing-indicator {
            font-size: 12px;
            color: #6c757d;
            padding: 5px 15px;
            display: none;
        }
        
        .emoji-button {
            color: #6c757d;
            padding: 0 15px;
            background: none;
            border: none;
            font-size: 20px;
            cursor: pointer;
        }
        
        .sender-name {
            font-size: 12px;
            font-weight: bold;
            margin-bottom: 3px;
            color: #495057;
        }
        
        .message.outgoing .sender-name {
            text-align: right;
        }
        
        @media (max-width: 768px) {
            .group-info-panel {
                display: none;
            }
            
            .chat-header .mobile-info-toggle {
                display: block;
            }
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="#">
            <i class="fas fa-cloud"></i> Cloud Messenger
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/messenger'/>"><i class="fas fa-comment"></i> Сообщения</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="<c:url value='/groups'/>"><i class="fas fa-users"></i> Группы</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-address-book"></i> Контакты</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-cog"></i> Настройки</a>
                </li>
            </ul>
            <div class="d-flex">
                <span class="navbar-text me-3">
                    <sec:authentication property="principal.username" />
                </span>
                <a href="<c:url value='/logout'/>" class="btn btn-outline-light">
                    <i class="fas fa-sign-out-alt"></i> Выход
                </a>
            </div>
        </div>
    </div>
</nav>

<div class="container content-container">
    <!-- Сообщения об успехе/ошибке -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show mb-3">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show mb-3">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <div class="chat-container">
        <!-- Панель информации о группе и участниках -->
        <div class="group-info-panel">
            <div class="group-header">
                <div class="d-flex align-items-center mb-3">
                    <div class="group-avatar">
                        ${fn:substring(group.name, 0, 1)}
                    </div>
                    <div>
                        <div class="group-name">${group.name}</div>
                        <div class="text-muted">
                            ${group.members.size()} участников
                        </div>
                    </div>
                </div>
                
                <div>
                    <c:choose>
                        <c:when test="${empty group.description}">
                            <p class="text-muted">Без описания</p>
                        </c:when>
                        <c:otherwise>
                            <p>${group.description}</p>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div class="d-flex justify-content-between mt-3">
                    <a href="<c:url value='/groups/${group.id}/members'/>" class="btn btn-outline-secondary btn-sm">
                        <i class="fas fa-users"></i> Управление участниками
                    </a>
                    
                    <c:if test="${isCreator}">
                        <button class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#editGroupModal">
                            <i class="fas fa-edit"></i>
                        </button>
                    </c:if>
                </div>
            </div>
            
            <div class="members-list">
                <h6 class="mb-3">Участники группы:</h6>
                
                <c:forEach items="${members}" var="member">
                    <div class="member-item">
                        <div class="member-avatar">
                            ${fn:substring(member.username, 0, 1)}
                        </div>
                        <div>
                            <div class="member-name">${member.username}</div>
                            <div class="member-role">
                                <c:choose>
                                    <c:when test="${member.id eq group.creator.id}">
                                        Создатель
                                    </c:when>
                                    <c:when test="${member.id eq currentUser.id}">
                                        Вы
                                    </c:when>
                                    <c:otherwise>
                                        Участник
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        
        <!-- Область чата -->
        <div class="chat-area">
            <div class="chat-header">
                <div class="d-flex align-items-center">
                    <button class="btn btn-sm btn-outline-secondary me-2 d-md-none mobile-info-toggle">
                        <i class="fas fa-info-circle"></i>
                    </button>
                    
                    <div class="group-avatar" style="width: 40px; height: 40px; font-size: 20px;">
                        ${fn:substring(group.name, 0, 1)}
                    </div>
                    
                    <div class="ms-2">
                        <div class="fw-bold">${group.name}</div>
                        <div class="text-muted small">${group.members.size()} участников</div>
                    </div>
                </div>
                
                <div>
                    <a href="<c:url value='/groups'/>" class="btn btn-outline-secondary btn-sm">
                        <i class="fas fa-arrow-left"></i> К списку групп
                    </a>
                </div>
            </div>
            
            <div id="messagesContainer" class="messages-container">
                <c:if test="${empty messages}">
                    <div class="text-center text-muted py-5">
                        <i class="fas fa-comments fa-3x mb-3"></i>
                        <p>В этой группе пока нет сообщений.<br>Напишите первое сообщение!</p>
                    </div>
                </c:if>
                
                <c:forEach items="${messages}" var="message">
                    <div class="message ${message.sender.id eq currentUser.id ? 'outgoing' : 'incoming'}">
                        <c:if test="${message.sender.id ne currentUser.id}">
                            <div class="sender-name">${message.sender.username}</div>
                        </c:if>
                        
                        <c:choose>
                            <%-- Для изображений --%>
                            <c:when test="${message.messageType eq 'IMAGE'}">
                                <div class="message-media">
                                    <img src="<c:url value='/file/${message.filePath}'/>" alt="Изображение">
                                </div>
                            </c:when>
                            
                            <%-- Для видео --%>
                            <c:when test="${message.messageType eq 'VIDEO'}">
                                <div class="message-media">
                                    <video controls>
                                        <source src="<c:url value='/file/${message.filePath}'/>" type="${message.mimeType}">
                                        Ваш браузер не поддерживает видео.
                                    </video>
                                </div>
                            </c:when>
                            
                            <%-- Для аудио --%>
                            <c:when test="${message.messageType eq 'AUDIO'}">
                                <div class="message-media">
                                    <audio controls>
                                        <source src="<c:url value='/file/${message.filePath}'/>" type="${message.mimeType}">
                                        Ваш браузер не поддерживает аудио.
                                    </audio>
                                </div>
                            </c:when>
                            
                            <%-- Для файлов --%>
                            <c:when test="${message.messageType eq 'FILE'}">
                                <div class="message-file">
                                    <div class="message-file-icon">
                                        <i class="fas fa-file"></i>
                                    </div>
                                    <div class="message-file-info">
                                        <div class="message-file-name">${message.fileName}</div>
                                        <div class="message-file-size">
                                            <c:choose>
                                                <c:when test="${message.fileSize < 1024}">
                                                    ${message.fileSize} B
                                                </c:when>
                                                <c:when test="${message.fileSize < 1048576}">
                                                    <fmt:formatNumber value="${message.fileSize / 1024}" maxFractionDigits="1"/> KB
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:formatNumber value="${message.fileSize / 1048576}" maxFractionDigits="1"/> MB
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <a href="<c:url value='/file/${message.filePath}'/>" class="message-file-download" download="${message.fileName}">
                                        <i class="fas fa-download"></i>
                                    </a>
                                </div>
                            </c:when>
                        </c:choose>
                        
                        <c:if test="${not empty message.content}">
                            <div class="message-content">${message.content}</div>
                        </c:if>
                        
                        <div class="message-meta">
                            <span class="message-time">
                                <fmt:formatDate value="${message.timestamp}" pattern="HH:mm" />
                            </span>
                            
                            <c:if test="${message.sender.id eq currentUser.id}">
                                <span class="message-status">
                                    <c:choose>
                                        <c:when test="${message.delivered}">
                                            <i class="fas fa-check-double" title="Доставлено"></i>
                                        </c:when>
                                        <c:when test="${message.sent}">
                                            <i class="fas fa-check" title="Отправлено"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-clock" title="Отправляется"></i>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <div class="typing-indicator" id="typingIndicator">
                <i class="fas fa-keyboard me-1"></i> <span id="typingUserName"></span> печатает...
            </div>
            
            <div class="input-area">
                <div class="file-preview" id="filePreview">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div id="filePreviewName"></div>
                        <button type="button" class="btn-close" id="cancelFileButton"></button>
                    </div>
                    <div id="filePreviewContent"></div>
                </div>
                
                <form id="messageForm" class="message-form">
                    <input type="file" id="fileInput" style="display: none;" />
                    <button type="button" class="btn-attach" id="attachButton">
                        <i class="fas fa-paperclip"></i>
                    </button>
                    
                    <button type="button" class="emoji-button" id="emojiButton">
                        <i class="far fa-smile"></i>
                    </button>
                    
                    <textarea id="messageInput" class="form-control message-input" placeholder="Введите сообщение..." rows="1"></textarea>
                    
                    <button type="submit" class="btn btn-primary btn-send">
                        <i class="fas fa-paper-plane"></i>
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Модальное окно редактирования группы -->
<c:if test="${isCreator}">
    <div class="modal fade" id="editGroupModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Редактирование группы</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="<c:url value='/groups/${group.id}/edit'/>" method="post">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="editName" class="form-label">Название группы</label>
                            <input type="text" class="form-control" id="editName" name="name" value="${group.name}" required>
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Описание</label>
                            <textarea class="form-control" id="editDescription" name="description" rows="3">${group.description}</textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                        <button type="submit" class="btn btn-primary">Сохранить</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<!-- JavaScript -->
<script src="<c:url value='/webjars/jquery/3.7.0/jquery.min.js'/>"></script>
<script src="<c:url value='/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js'/>"></script>
<script>
    $(document).ready(function() {
        const groupId = ${group.id};
        const currentUserId = ${currentUser.id};
        let lastMessageTimestamp = Date.now();
        let currentFile = null;
        
        // Прокрутка к последнему сообщению при загрузке
        scrollToBottom();
        
        // Обновление сообщений
        function loadMessages() {
            const offset = 0;
            const limit = 50;
            
            $.get('${pageContext.request.contextPath}/groups/' + groupId + '/messages', {
                limit: limit,
                offset: offset
            }, function(messages) {
                updateMessages(messages);
            });
        }
        
        // Обновление сообщений каждые 5 секунд
        const messageInterval = setInterval(loadMessages, 5000);
        
        // Отображение сообщений
        function updateMessages(messages) {
            if (messages && messages.length > 0) {
                const messagesContainer = $('#messagesContainer');
                const wasEmpty = messagesContainer.children().length === 0;
                let newMessages = false;
                
                messages.forEach(function(message) {
                    if (new Date(message.timestamp).getTime() > lastMessageTimestamp) {
                        const messageElement = createMessageElement(message);
                        messagesContainer.append(messageElement);
                        newMessages = true;
                    }
                });
                
                if (newMessages || wasEmpty) {
                    scrollToBottom();
                }
                
                if (messages.length > 0) {
                    lastMessageTimestamp = new Date(messages[0].timestamp).getTime();
                }
            }
        }
        
        // Создание элемента сообщения
        function createMessageElement(message) {
            const isOutgoing = message.sender.id === currentUserId;
            const messageClass = isOutgoing ? 'outgoing' : 'incoming';
            
            let messageHtml = '<div class="message ' + messageClass + '">';
            
            // Имя отправителя для входящих сообщений
            if (!isOutgoing) {
                messageHtml += '<div class="sender-name">' + message.sender.username + '</div>';
            }
            
            // Медиаконтент
            if (message.messageType === 'IMAGE') {
                messageHtml += '<div class="message-media">' +
                               '<img src="${pageContext.request.contextPath}/file/' + message.filePath + '" alt="Изображение">' +
                               '</div>';
            } else if (message.messageType === 'VIDEO') {
                messageHtml += '<div class="message-media">' +
                               '<video controls>' +
                               '<source src="${pageContext.request.contextPath}/file/' + message.filePath + '" type="' + message.mimeType + '">' +
                               'Ваш браузер не поддерживает видео.' +
                               '</video>' +
                               '</div>';
            } else if (message.messageType === 'AUDIO') {
                messageHtml += '<div class="message-media">' +
                               '<audio controls>' +
                               '<source src="${pageContext.request.contextPath}/file/' + message.filePath + '" type="' + message.mimeType + '">' +
                               'Ваш браузер не поддерживает аудио.' +
                               '</audio>' +
                               '</div>';
            } else if (message.messageType === 'FILE') {
                const fileSize = formatFileSize(message.fileSize);
                
                messageHtml += '<div class="message-file">' +
                               '<div class="message-file-icon"><i class="fas fa-file"></i></div>' +
                               '<div class="message-file-info">' +
                               '<div class="message-file-name">' + message.fileName + '</div>' +
                               '<div class="message-file-size">' + fileSize + '</div>' +
                               '</div>' +
                               '<a href="${pageContext.request.contextPath}/file/' + message.filePath + '" class="message-file-download" download="' + message.fileName + '">' +
                               '<i class="fas fa-download"></i>' +
                               '</a>' +
                               '</div>';
            }
            
            // Текстовое содержимое
            if (message.content) {
                messageHtml += '<div class="message-content">' + message.content + '</div>';
            }
            
            // Метаданные сообщения
            const time = new Date(message.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
            
            messageHtml += '<div class="message-meta">' +
                           '<span class="message-time">' + time + '</span>';
            
            if (isOutgoing) {
                messageHtml += '<span class="message-status">';
                
                if (message.delivered) {
                    messageHtml += '<i class="fas fa-check-double" title="Доставлено"></i>';
                } else if (message.sent) {
                    messageHtml += '<i class="fas fa-check" title="Отправлено"></i>';
                } else {
                    messageHtml += '<i class="fas fa-clock" title="Отправляется"></i>';
                }
                
                messageHtml += '</span>';
            }
            
            messageHtml += '</div></div>';
            
            return messageHtml;
        }
        
        // Форматирование размера файла
        function formatFileSize(size) {
            if (size < 1024) {
                return size + ' B';
            } else if (size < 1048576) {
                return (size / 1024).toFixed(1) + ' KB';
            } else {
                return (size / 1048576).toFixed(1) + ' MB';
            }
        }
        
        // Прокрутка чата вниз
        function scrollToBottom() {
            const messagesContainer = document.getElementById('messagesContainer');
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
        
        // Отправка текстового сообщения
        $('#messageForm').submit(function(e) {
            e.preventDefault();
            
            const messageText = $('#messageInput').val().trim();
            
            if (!messageText && !currentFile) {
                return;
            }
            
            if (currentFile) {
                sendMediaMessage(messageText);
            } else {
                sendTextMessage(messageText);
            }
        });
        
        // Отправка текстового сообщения
        function sendTextMessage(content) {
            $.post('${pageContext.request.contextPath}/groups/' + groupId + '/send/text', {
                content: content
            }, function(message) {
                $('#messageInput').val('');
                
                // Добавляем сообщение в контейнер
                const messageElement = createMessageElement(message);
                $('#messagesContainer').append(messageElement);
                
                scrollToBottom();
            }).fail(function(error) {
                console.error('Ошибка при отправке сообщения:', error);
                alert('Не удалось отправить сообщение. Пожалуйста, попробуйте еще раз.');
            });
        }
        
        // Отправка медиафайла
        function sendMediaMessage(text) {
            const formData = new FormData();
            formData.append('file', currentFile);
            
            if (text) {
                formData.append('text', text);
            }
            
            $.ajax({
                url: '${pageContext.request.contextPath}/groups/' + groupId + '/send/media',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(message) {
                    $('#messageInput').val('');
                    clearFilePreview();
                    
                    // Добавляем сообщение в контейнер
                    const messageElement = createMessageElement(message);
                    $('#messagesContainer').append(messageElement);
                    
                    scrollToBottom();
                },
                error: function(error) {
                    console.error('Ошибка при отправке медиафайла:', error);
                    alert('Не удалось отправить файл. Пожалуйста, попробуйте еще раз.');
                }
            });
        }
        
        // Обработка прикрепления файла
        $('#attachButton').click(function() {
            $('#fileInput').click();
        });
        
        // Обработка выбора файла
        $('#fileInput').change(function(e) {
            const file = e.target.files[0];
            
            if (file) {
                currentFile = file;
                const filePreview = $('#filePreview');
                const filePreviewContent = $('#filePreviewContent');
                const filePreviewName = $('#filePreviewName');
                
                filePreviewName.text(file.name);
                filePreviewContent.empty();
                
                if (file.type.startsWith('image/')) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        filePreviewContent.html('<img src="' + e.target.result + '" alt="Предпросмотр">');
                    };
                    reader.readAsDataURL(file);
                } else if (file.type.startsWith('video/')) {
                    filePreviewContent.html('<i class="fas fa-video fa-3x text-primary"></i>');
                } else if (file.type.startsWith('audio/')) {
                    filePreviewContent.html('<i class="fas fa-music fa-3x text-primary"></i>');
                } else {
                    filePreviewContent.html('<i class="fas fa-file fa-3x text-primary"></i>');
                }
                
                filePreview.show();
            }
        });
        
        // Отмена выбора файла
        $('#cancelFileButton').click(function() {
            clearFilePreview();
        });
        
        // Очистка превью файла
        function clearFilePreview() {
            currentFile = null;
            $('#fileInput').val('');
            $('#filePreview').hide();
            $('#filePreviewContent').empty();
            $('#filePreviewName').text('');
        }
        
        // Автоматическое увеличение высоты текстового поля
        $('#messageInput').on('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
        
        // Отображение информации о группе на мобильных устройствах
        $('.mobile-info-toggle').click(function() {
            $('.group-info-panel').toggle();
        });
        
        // Автоматическое закрытие алертов
        setTimeout(function() {
            $('.alert').alert('close');
        }, 5000);
        
        // Очистка интервалов при уходе со страницы
        $(window).on('beforeunload', function() {
            clearInterval(messageInterval);
        });
    });
</script>

</body>
</html> 