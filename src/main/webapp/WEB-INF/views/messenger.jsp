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
    <title>Cloud Messenger</title>
    
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.0/css/bootstrap.min.css'/>">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value='/webjars/font-awesome/6.4.0/css/all.min.css'/>">
    
    <style>
        body {
            background-color: #f8f9fa;
        }
        
        .chat-container {
            height: calc(100vh - 160px);
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            margin-top: 15px;
        }
        
        .contacts-list {
            height: 100%;
            overflow-y: auto;
            border-right: 1px solid #dee2e6;
        }
        
        .contact-item {
            padding: 10px 15px;
            border-bottom: 1px solid #f1f1f1;
            cursor: pointer;
        }
        
        .contact-item:hover {
            background-color: #f8f9fa;
        }
        
        .contact-item.active {
            background-color: #e9ecef;
        }
        
        .chat-area {
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        
        .chat-header {
            padding: 15px;
            border-bottom: 1px solid #dee2e6;
            background-color: #f8f9fa;
        }
        
        .messages-container {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
        }
        
        .message {
            margin-bottom: 15px;
            max-width: 80%;
        }
        
        .message-outgoing {
            align-self: flex-end;
            background-color: #0d6efd;
            color: white;
            border-radius: 15px 15px 0 15px;
            padding: 10px 15px;
            margin-left: auto;
        }
        
        .message-incoming {
            align-self: flex-start;
            background-color: #e9ecef;
            color: #212529;
            border-radius: 15px 15px 15px 0;
            padding: 10px 15px;
        }
        
        .message-time {
            font-size: 0.75rem;
            margin-top: 5px;
            text-align: right;
        }
        
        .message-status {
            font-size: 0.75rem;
            margin-top: 2px;
        }
        
        .chat-input-area {
            padding: 15px;
            border-top: 1px solid #dee2e6;
            background-color: #f8f9fa;
        }
        
        .file-preview {
            max-width: 200px;
            max-height: 150px;
            margin: 10px 0;
            border-radius: 8px;
        }
        
        .btn-file {
            position: relative;
            overflow: hidden;
        }
        
        .btn-file input[type=file] {
            position: absolute;
            top: 0;
            right: 0;
            min-width: 100%;
            min-height: 100%;
            font-size: 100px;
            text-align: right;
            filter: alpha(opacity=0);
            opacity: 0;
            outline: none;
            background: white;
            cursor: pointer;
            display: block;
        }
        
        .media-message-container {
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 8px;
        }
        
        .media-message-container.outgoing {
            background-color: #e3f2fd;
            align-self: flex-end;
            margin-left: auto;
        }
        
        .media-message-container.incoming {
            background-color: #f1f8e9;
            align-self: flex-start;
        }
        
        .media-preview {
            max-width: 250px;
            max-height: 200px;
            border-radius: 4px;
            margin-bottom: 5px;
        }
        
        .audio-player, .video-player {
            max-width: 100%;
            margin-bottom: 5px;
        }
        
        .file-info {
            display: flex;
            align-items: center;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 5px;
        }
        
        .file-icon {
            font-size: 24px;
            margin-right: 10px;
        }
        
        .file-name {
            flex-grow: 1;
            font-size: 14px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .file-size {
            font-size: 12px;
            color: #666;
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
                    <a class="nav-link active" href="#"><i class="fas fa-comment"></i> Сообщения</a>
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

<div class="container">
    <div class="row chat-container">
        <!-- Список контактов -->
        <div class="col-md-3 contacts-list p-0">
            <div class="p-3 bg-light">
                <div class="input-group">
                    <input type="text" class="form-control" placeholder="Поиск...">
                    <button class="btn btn-outline-secondary" type="button">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>
            
            <!-- Здесь список контактов (будет заполняться динамически) -->
            <div id="contacts-container">
                <div class="contact-item active" data-phone="+79123456789">
                    <div class="d-flex justify-content-between">
                        <strong>Иван Петров</strong>
                        <small>10:45</small>
                    </div>
                    <div class="text-truncate text-muted small">Последнее сообщение...</div>
                </div>
                
                <div class="contact-item" data-phone="+79234567890">
                    <div class="d-flex justify-content-between">
                        <strong>Анна Сидорова</strong>
                        <small>Вчера</small>
                    </div>
                    <div class="text-truncate text-muted small">Привет, как дела?</div>
                </div>
                
                <!-- Другие контакты -->
            </div>
        </div>
        
        <!-- Область чата -->
        <div class="col-md-9 p-0">
            <div class="chat-area">
                <!-- Заголовок чата -->
                <div class="chat-header">
                    <div class="d-flex align-items-center">
                        <div class="me-3">
                            <i class="fas fa-user-circle fs-1 text-secondary"></i>
                        </div>
                        <div>
                            <h5 class="mb-0" id="chat-recipient-name">Иван Петров</h5>
                            <div class="text-muted small" id="chat-recipient-phone">+7 (912) 345-67-89</div>
                        </div>
                    </div>
                </div>
                
                <!-- Контейнер с сообщениями -->
                <div class="messages-container" id="messages-container">
                    <!-- Примеры сообщений (будут заполняться динамически) -->
                    
                    <!-- Входящее сообщение -->
                    <div class="d-flex mb-3">
                        <div class="message message-incoming">
                            <div>Привет! Как дела?</div>
                            <div class="message-time text-muted">10:30</div>
                        </div>
                    </div>
                    
                    <!-- Исходящее сообщение -->
                    <div class="d-flex mb-3 justify-content-end">
                        <div class="message message-outgoing">
                            <div>Привет! Все хорошо, спасибо!</div>
                            <div class="message-time text-white-50">10:32</div>
                            <div class="message-status text-white-50">
                                <i class="fas fa-check-double"></i> Доставлено
                            </div>
                        </div>
                    </div>
                    
                    <!-- Входящее сообщение с изображением -->
                    <div class="d-flex mb-3">
                        <div class="media-message-container incoming">
                            <img src="https://via.placeholder.com/250x150" class="media-preview" alt="Изображение">
                            <div>Смотри какое фото!</div>
                            <div class="message-time text-muted">10:35</div>
                        </div>
                    </div>
                    
                    <!-- Исходящее сообщение с аудио -->
                    <div class="d-flex mb-3 justify-content-end">
                        <div class="media-message-container outgoing">
                            <audio controls class="audio-player">
                                <source src="#" type="audio/mpeg">
                                Ваш браузер не поддерживает аудио элемент.
                            </audio>
                            <div>Голосовое сообщение</div>
                            <div class="message-time text-muted">10:38</div>
                            <div class="message-status text-muted">
                                <i class="fas fa-check"></i> Отправлено
                            </div>
                        </div>
                    </div>
                    
                    <!-- Входящее сообщение с файлом -->
                    <div class="d-flex mb-3">
                        <div class="media-message-container incoming">
                            <div class="file-info">
                                <i class="fas fa-file-pdf file-icon text-danger"></i>
                                <div class="file-name">document.pdf</div>
                                <div class="file-size">2.5 MB</div>
                            </div>
                            <div>Вот документ, о котором я говорил</div>
                            <div class="message-time text-muted">10:40</div>
                        </div>
                    </div>
                </div>
                
                <!-- Область ввода сообщения -->
                <div class="chat-input-area">
                    <form id="message-form" enctype="multipart/form-data">
                        <div class="row">
                            <div class="col-md-12 mb-2">
                                <!-- Превью выбранного файла -->
                                <div id="file-preview-container" class="d-none">
                                    <div class="card p-2 mb-2">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div id="file-preview-info">
                                                <span id="file-preview-name"></span>
                                                <span id="file-preview-size" class="text-muted ms-2"></span>
                                            </div>
                                            <button type="button" class="btn btn-sm btn-outline-danger" id="remove-file">
                                                <i class="fas fa-times"></i>
                                            </button>
                                        </div>
                                        <img id="image-preview" class="file-preview d-none" alt="Предпросмотр">
                                    </div>
                                </div>
                                
                                <!-- Область ввода текста -->
                                <div class="input-group">
                                    <span class="input-group-text btn-file">
                                        <i class="fas fa-paperclip"></i>
                                        <input type="file" id="file-input" name="file">
                                    </span>
                                    <input type="text" class="form-control" id="message-input" placeholder="Введите сообщение..." autocomplete="off">
                                    <button class="btn btn-primary" type="submit">
                                        <i class="fas fa-paper-plane"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript -->
<script src="<c:url value='/webjars/jquery/3.7.0/jquery.min.js'/>"></script>
<script src="<c:url value='/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js'/>"></script>
<script>
    $(document).ready(function() {
        let selectedFile = null;
        const currentUser = '<sec:authentication property="principal.username" />';
        const contactsList = []; // Здесь будут контакты, загруженные с сервера
        let currentRecipient = '+79123456789'; // Телефон текущего собеседника
        
        // Обработка выбора файла
        $('#file-input').change(function() {
            const fileInput = this;
            if (fileInput.files && fileInput.files[0]) {
                selectedFile = fileInput.files[0];
                
                // Отображаем информацию о файле
                $('#file-preview-container').removeClass('d-none');
                $('#file-preview-name').text(selectedFile.name);
                $('#file-preview-size').text(formatFileSize(selectedFile.size));
                
                // Если это изображение, показываем превью
                if (selectedFile.type.startsWith('image/')) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        $('#image-preview').attr('src', e.target.result)
                            .removeClass('d-none');
                    }
                    reader.readAsDataURL(selectedFile);
                } else {
                    $('#image-preview').addClass('d-none');
                }
            }
        });
        
        // Удаление выбранного файла
        $('#remove-file').click(function() {
            $('#file-input').val('');
            selectedFile = null;
            $('#file-preview-container').addClass('d-none');
            $('#image-preview').attr('src', '').addClass('d-none');
        });
        
        // Отправка сообщения
        $('#message-form').submit(function(e) {
            e.preventDefault();
            
            const messageText = $('#message-input').val().trim();
            
            // Проверяем, есть ли текст или файл
            if (!messageText && !selectedFile) {
                return;
            }
            
            // Если есть файл, отправляем его
            if (selectedFile) {
                sendFileMessage(selectedFile, messageText);
            } else {
                // Иначе отправляем текстовое сообщение
                sendTextMessage(messageText);
            }
            
            // Очищаем поле ввода и удаляем файл
            $('#message-input').val('');
            $('#remove-file').click();
        });
        
        // Функция форматирования размера файла
        function formatFileSize(bytes) {
            if (bytes < 1024) return bytes + ' B';
            else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
            else return (bytes / 1048576).toFixed(1) + ' MB';
        }
        
        // Функция отправки текстового сообщения
        function sendTextMessage(text) {
            // Здесь должен быть AJAX запрос для отправки сообщения
            // Для примера просто добавляем сообщение в чат
            const now = new Date();
            const timeStr = now.getHours() + ':' + (now.getMinutes() < 10 ? '0' : '') + now.getMinutes();
            
            const messageHtml = `
                <div class="d-flex mb-3 justify-content-end">
                    <div class="message message-outgoing">
                        <div>${text}</div>
                        <div class="message-time text-white-50">${timeStr}</div>
                        <div class="message-status text-white-50">
                            <i class="fas fa-check"></i> Отправлено
                        </div>
                    </div>
                </div>
            `;
            
            $('#messages-container').append(messageHtml);
            scrollToBottom();
        }
        
        // Функция отправки сообщения с файлом
        function sendFileMessage(file, text) {
            // Создаем FormData для отправки файла
            const formData = new FormData();
            formData.append('file', file);
            formData.append('recipientPhone', currentRecipient);
            
            // Здесь должен быть AJAX запрос для отправки файла
            // Для примера просто добавляем сообщение в чат
            const now = new Date();
            const timeStr = now.getHours() + ':' + (now.getMinutes() < 10 ? '0' : '') + now.getMinutes();
            
            let previewHtml = '';
            
            // В зависимости от типа файла формируем превью
            if (file.type.startsWith('image/')) {
                // Для изображений
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewHtml = `<img src="${e.target.result}" class="media-preview" alt="Изображение">`;
                    appendMediaMessage(previewHtml, text, timeStr);
                };
                reader.readAsDataURL(file);
                return;
            } else if (file.type.startsWith('audio/')) {
                // Для аудио
                previewHtml = `
                    <audio controls class="audio-player">
                        <source src="#" type="audio/mpeg">
                        Ваш браузер не поддерживает аудио элемент.
                    </audio>
                `;
            } else if (file.type.startsWith('video/')) {
                // Для видео
                previewHtml = `
                    <video controls class="video-player">
                        <source src="#" type="video/mp4">
                        Ваш браузер не поддерживает видео элемент.
                    </video>
                `;
            } else {
                // Для других файлов
                let fileIcon = 'fa-file';
                let colorClass = 'text-secondary';
                
                // Определяем иконку в зависимости от типа файла
                if (file.type.includes('pdf')) {
                    fileIcon = 'fa-file-pdf';
                    colorClass = 'text-danger';
                } else if (file.type.includes('word') || file.name.endsWith('.doc') || file.name.endsWith('.docx')) {
                    fileIcon = 'fa-file-word';
                    colorClass = 'text-primary';
                } else if (file.type.includes('excel') || file.name.endsWith('.xls') || file.name.endsWith('.xlsx')) {
                    fileIcon = 'fa-file-excel';
                    colorClass = 'text-success';
                } else if (file.type.includes('zip') || file.type.includes('rar') || file.name.endsWith('.zip') || file.name.endsWith('.rar')) {
                    fileIcon = 'fa-file-archive';
                    colorClass = 'text-warning';
                }
                
                previewHtml = `
                    <div class="file-info">
                        <i class="fas ${fileIcon} file-icon ${colorClass}"></i>
                        <div class="file-name">${file.name}</div>
                        <div class="file-size">${formatFileSize(file.size)}</div>
                    </div>
                `;
            }
            
            appendMediaMessage(previewHtml, text, timeStr);
        }
        
        // Функция добавления медиа-сообщения в чат
        function appendMediaMessage(previewHtml, text, timeStr) {
            const messageHtml = `
                <div class="d-flex mb-3 justify-content-end">
                    <div class="media-message-container outgoing">
                        ${previewHtml}
                        ${text ? `<div>${text}</div>` : ''}
                        <div class="message-time text-muted">${timeStr}</div>
                        <div class="message-status text-muted">
                            <i class="fas fa-check"></i> Отправлено
                        </div>
                    </div>
                </div>
            `;
            
            $('#messages-container').append(messageHtml);
            scrollToBottom();
        }
        
        // Прокрутка до последнего сообщения
        function scrollToBottom() {
            const messagesContainer = $('#messages-container');
            messagesContainer.scrollTop(messagesContainer[0].scrollHeight);
        }
        
        // Вызов функции при загрузке страницы
        scrollToBottom();
    });
</script>

</body>
</html> 