<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Административная панель - Cloud Messenger</title>
    
    <!-- Bootstrap CSS -->
    <link href="<c:url value='/webjars/bootstrap/5.3.0/css/bootstrap.min.css'/>" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="<c:url value='/webjars/font-awesome/6.4.0/css/all.min.css'/>" rel="stylesheet">
    
    <style>
        .admin-header {
            background-color: #343a40;
            color: white;
            padding: 1rem 0;
        }
        .user-list-container {
            margin-top: 2rem;
        }
        .table-responsive {
            margin-top: 1rem;
        }
        .alert {
            margin-top: 1rem;
        }
        .navbar-brand {
            font-weight: bold;
        }
        .status-active {
            color: #28a745;
        }
        .status-inactive {
            color: #dc3545;
        }
        .user-action-btn {
            margin-right: 0.5rem;
        }
        body {
            padding-bottom: 2rem;
        }
    </style>
</head>
<body>
    <!-- Шапка -->
    <header class="admin-header">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1><i class="fas fa-cogs me-2"></i>Административная панель</h1>
                    <p class="mb-0">Cloud Messenger</p>
                </div>
                <div>
                    <span class="me-3">
                        <i class="fas fa-user-shield me-1"></i>${admin.email}
                    </span>
                    <a href="<c:url value='/logout'/>" class="btn btn-outline-light btn-sm">
                        <i class="fas fa-sign-out-alt me-1"></i>Выход
                    </a>
                </div>
            </div>
        </div>
    </header>

    <!-- Основное содержимое -->
    <div class="container user-list-container">
        <!-- Сообщения об успехе/ошибке -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <!-- Меню навигации -->
        <ul class="nav nav-tabs mb-4">
            <li class="nav-item">
                <a class="nav-link active" href="<c:url value='/admin'/>">
                    <i class="fas fa-users me-1"></i>Пользователи
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<c:url value='/messenger'/>">
                    <i class="fas fa-comment-dots me-1"></i>Вернуться в мессенджер
                </a>
            </li>
        </ul>
        
        <!-- Список пользователей -->
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="fas fa-users me-2"></i>Список пользователей
                </h5>
                <span class="badge bg-primary">${fn:length(users)} пользователей</span>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col">Email</th>
                                <th scope="col">Имя</th>
                                <th scope="col">Телефон</th>
                                <th scope="col">Статус</th>
                                <th scope="col">Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${users}" var="user">
                                <tr>
                                    <td>${user.id}</td>
                                    <td>${user.email}</td>
                                    <td>${user.name}</td>
                                    <td>${user.phoneNumber != null ? user.phoneNumber : 'Не указан'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.enabled}">
                                                <span class="status-active">
                                                    <i class="fas fa-check-circle me-1"></i>Активен
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-inactive">
                                                    <i class="fas fa-ban me-1"></i>Заблокирован
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form action="<c:url value='/admin/users/${user.id}/toggle-status'/>" method="post" style="display:inline;">
                                            <button type="submit" class="btn btn-sm ${user.enabled ? 'btn-warning' : 'btn-success'}" 
                                                    ${user.email == admin.email ? 'disabled' : ''}>
                                                <c:choose>
                                                    <c:when test="${user.enabled}">
                                                        <i class="fas fa-ban me-1"></i>Блокировать
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-check-circle me-1"></i>Разблокировать
                                                    </c:otherwise>
                                                </c:choose>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="<c:url value='/webjars/jquery/3.7.0/jquery.min.js'/>"></script>
    <script src="<c:url value='/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js'/>"></script>
    
    <script>
        $(document).ready(function() {
            // Автоматическое закрытие алертов через 5 секунд
            setTimeout(function() {
                $('.alert').alert('close');
            }, 5000);
        });
    </script>
</body>
</html> 