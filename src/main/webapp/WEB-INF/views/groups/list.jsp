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
    <title>Групповые чаты - Cloud Messenger</title>
    
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.0/css/bootstrap.min.css'/>">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value='/webjars/font-awesome/6.4.0/css/all.min.css'/>">
    
    <style>
        body {
            background-color: #f8f9fa;
        }
        
        .groups-container {
            margin-top: 20px;
            margin-bottom: 20px;
        }
        
        .group-card {
            transition: transform 0.2s;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .group-card:hover {
            transform: translateY(-5px);
        }
        
        .group-card .card-body {
            padding: 20px;
        }
        
        .group-avatar {
            width: 50px;
            height: 50px;
            background-color: #0d6efd;
            border-radius: 50%;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            font-weight: bold;
            margin-right: 15px;
        }
        
        .group-info {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .group-name {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .group-meta {
            font-size: 12px;
            color: #6c757d;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 0;
        }
        
        .empty-state-icon {
            font-size: 64px;
            color: #d1d1d1;
            margin-bottom: 20px;
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

<div class="container groups-container">
    <div class="row">
        <div class="col-md-12 mb-4">
            <div class="d-flex justify-content-between align-items-center">
                <h2><i class="fas fa-users"></i> Мои группы</h2>
                <a href="<c:url value='/groups/create'/>" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Создать группу
                </a>
            </div>
        </div>
    </div>
    
    <!-- Сообщения об успехе/ошибке -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <!-- Список групп -->
    <div class="row">
        <c:choose>
            <c:when test="${empty groups}">
                <div class="col-md-12">
                    <div class="card">
                        <div class="card-body empty-state">
                            <div class="empty-state-icon">
                                <i class="fas fa-users"></i>
                            </div>
                            <h3>У вас пока нет групп</h3>
                            <p class="text-muted">Создайте новую группу, чтобы начать общение</p>
                            <a href="<c:url value='/groups/create'/>" class="btn btn-primary mt-3">
                                <i class="fas fa-plus"></i> Создать группу
                            </a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${groups}" var="group">
                    <div class="col-md-4">
                        <div class="card group-card">
                            <div class="card-body">
                                <div class="group-info">
                                    <div class="group-avatar">
                                        ${fn:substring(group.name, 0, 1)}
                                    </div>
                                    <div>
                                        <div class="group-name">${group.name}</div>
                                        <div class="group-meta">
                                            ${group.members.size()} участников
                                            <c:if test="${group.creator.id eq currentUser.id}">
                                                · <span class="badge bg-primary">Создатель</span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                                
                                <p class="card-text">
                                    <c:choose>
                                        <c:when test="${empty group.description}">
                                            <span class="text-muted">Без описания</span>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${group.description}" />
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                
                                <div class="d-flex justify-content-between">
                                    <a href="<c:url value='/groups/${group.id}'/>" class="btn btn-primary">
                                        <i class="fas fa-comments"></i> Открыть чат
                                    </a>
                                    
                                    <div class="dropdown">
                                        <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                            <i class="fas fa-ellipsis-v"></i>
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end">
                                            <li>
                                                <a class="dropdown-item" href="<c:url value='/groups/${group.id}/members'/>">
                                                    <i class="fas fa-users"></i> Участники
                                                </a>
                                            </li>
                                            
                                            <c:if test="${group.creator.id eq currentUser.id}">
                                                <li>
                                                    <a class="dropdown-item" href="#">
                                                        <i class="fas fa-edit"></i> Редактировать
                                                    </a>
                                                </li>
                                                <li><hr class="dropdown-divider"></li>
                                                <li>
                                                    <a class="dropdown-item text-danger delete-group" href="#" 
                                                       data-group-id="${group.id}" data-group-name="${group.name}">
                                                        <i class="fas fa-trash-alt"></i> Удалить группу
                                                    </a>
                                                </li>
                                            </c:if>
                                            
                                            <c:if test="${group.creator.id ne currentUser.id}">
                                                <li><hr class="dropdown-divider"></li>
                                                <li>
                                                    <a class="dropdown-item text-danger leave-group" href="#" 
                                                       data-group-id="${group.id}" data-group-name="${group.name}">
                                                        <i class="fas fa-sign-out-alt"></i> Покинуть группу
                                                    </a>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Модальное окно удаления группы -->
<div class="modal fade" id="deleteGroupModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Удалить группу</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Вы действительно хотите удалить группу "<span id="groupNameToDelete"></span>"?</p>
                <p class="text-danger">Это действие нельзя отменить. Вся история сообщений будет удалена.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                <form id="deleteGroupForm" method="post">
                    <button type="submit" class="btn btn-danger">Удалить</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Модальное окно выхода из группы -->
<div class="modal fade" id="leaveGroupModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Покинуть группу</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Вы действительно хотите покинуть группу "<span id="groupNameToLeave"></span>"?</p>
                <p>Вы больше не сможете видеть сообщения и участвовать в обсуждениях этой группы.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
                <form id="leaveGroupForm" method="post">
                    <button type="submit" class="btn btn-danger">Покинуть</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript -->
<script src="<c:url value='/webjars/jquery/3.7.0/jquery.min.js'/>"></script>
<script src="<c:url value='/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js'/>"></script>
<script>
    $(document).ready(function() {
        // Обработка клика по кнопке удаления группы
        $('.delete-group').click(function(e) {
            e.preventDefault();
            const groupId = $(this).data('group-id');
            const groupName = $(this).data('group-name');
            
            $('#groupNameToDelete').text(groupName);
            $('#deleteGroupForm').attr('action', '${pageContext.request.contextPath}/groups/' + groupId + '/delete');
            
            var deleteModal = new bootstrap.Modal(document.getElementById('deleteGroupModal'));
            deleteModal.show();
        });
        
        // Обработка клика по кнопке выхода из группы
        $('.leave-group').click(function(e) {
            e.preventDefault();
            const groupId = $(this).data('group-id');
            const groupName = $(this).data('group-name');
            
            $('#groupNameToLeave').text(groupName);
            $('#leaveGroupForm').attr('action', '${pageContext.request.contextPath}/groups/' + groupId + '/leave');
            
            var leaveModal = new bootstrap.Modal(document.getElementById('leaveGroupModal'));
            leaveModal.show();
        });
        
        // Автоматическое закрытие алертов через 5 секунд
        setTimeout(function() {
            $('.alert').alert('close');
        }, 5000);
    });
</script>

</body>
</html>