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
    <title>Создание группы - Cloud Messenger</title>
    
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.0/css/bootstrap.min.css'/>">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value='/webjars/font-awesome/6.4.0/css/all.min.css'/>">
    
    <style>
        body {
            background-color: #f8f9fa;
        }
        
        .create-group-container {
            margin-top: 30px;
            margin-bottom: 30px;
        }
        
        .card {
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .card-header {
            background-color: #f8f9fa;
            padding: 15px 20px;
        }
        
        .card-body {
            padding: 30px;
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

<div class="container create-group-container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <!-- Сообщения об ошибке -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show">
                    ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <div class="card">
                <div class="card-header">
                    <h4 class="mb-0"><i class="fas fa-users"></i> Создание новой группы</h4>
                </div>
                <div class="card-body">
                    <form action="<c:url value='/groups/create'/>" method="post">
                        <div class="mb-3">
                            <label for="name" class="form-label">Название группы <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" required
                                   placeholder="Введите название группы">
                            <div class="form-text">Название должно быть информативным и понятным для участников.</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Описание группы</label>
                            <textarea class="form-control" id="description" name="description" rows="3"
                                      placeholder="Опишите назначение группы"></textarea>
                            <div class="form-text">Необязательное описание группы (до 500 символов).</div>
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="<c:url value='/groups'/>" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Назад
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-check"></i> Создать группу
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            
            <div class="card mt-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="fas fa-info-circle"></i> О групповых чатах</h5>
                </div>
                <div class="card-body">
                    <p>Групповые чаты в Cloud Messenger позволяют общаться сразу с несколькими пользователями.</p>
                    
                    <h6>Возможности групповых чатов:</h6>
                    <ul>
                        <li>Обмен текстовыми сообщениями между всеми участниками группы</li>
                        <li>Отправка медиафайлов (изображений, видео, аудио и документов)</li>
                        <li>Управление участниками группы</li>
                        <li>Отслеживание статуса доставки сообщений</li>
                    </ul>
                    
                    <h6>Управление группой:</h6>
                    <ul>
                        <li>Создатель группы может добавлять и удалять участников</li>
                        <li>Любой участник может покинуть группу в любое время</li>
                        <li>Только создатель может удалить группу</li>
                    </ul>
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
        // Ограничение длины описания
        $('#description').on('input', function() {
            const maxLength = 500;
            if ($(this).val().length > maxLength) {
                $(this).val($(this).val().substring(0, maxLength));
            }
        });
        
        // Автоматическое закрытие алертов через 5 секунд
        setTimeout(function() {
            $('.alert').alert('close');
        }, 5000);
    });
</script>

</body>
</html> 