<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cloud Messenger - Вход</title>
    
    <!-- Bootstrap CSS -->
    <link href="<c:url value='/webjars/bootstrap/5.3.0/css/bootstrap.min.css'/>" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="<c:url value='/webjars/font-awesome/6.4.0/css/all.min.css'/>" rel="stylesheet">
    
    <style>
        body {
            background-color: #f8f9fa;
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .card-img {
            background: url('<c:url value="/resources/images/login-bg.jpg"/>') center/cover no-repeat;
            min-height: 400px;
            border-radius: 0.25rem 0 0 0.25rem;
        }
        .form-control:focus, .btn:focus {
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
        }
        .nav-tabs .nav-link {
            color: #6c757d;
        }
        .nav-tabs .nav-link.active {
            color: #0d6efd;
            font-weight: 500;
        }
        .alert {
            margin-bottom: 1.5rem;
        }
        .input-group-text {
            background-color: #f8f9fa;
        }
        .form-text {
            font-size: 0.8rem;
        }
        @media (max-width: 767.98px) {
            .card {
                border: none;
                background-color: transparent;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row g-0 justify-content-center">
            <div class="col-md-6 col-lg-7 d-none d-md-block">
                <div class="card-img h-100"></div>
            </div>
            <div class="col-md-6 col-lg-5">
                <div class="card shadow">
                    <div class="card-body p-5">
                        <div class="text-center mb-4">
                            <h1 class="mb-3">Cloud Messenger</h1>
                            <p class="text-muted">Общайтесь с друзьями в реальном времени</p>
                        </div>
                        
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty message}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>${message}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        
                        <ul class="nav nav-tabs nav-justified mb-4" id="authTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link ${param.register == null ? 'active' : ''}" id="login-tab" data-bs-toggle="tab" 
                                    data-bs-target="#login" type="button" role="tab" 
                                    aria-controls="login" aria-selected="${param.register == null ? 'true' : 'false'}">Вход</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link ${param.register != null ? 'active' : ''}" id="register-tab" data-bs-toggle="tab" 
                                    data-bs-target="#register" type="button" role="tab" 
                                    aria-controls="register" aria-selected="${param.register != null ? 'true' : 'false'}">Регистрация</button>
                            </li>
                        </ul>
                        
                        <div class="tab-content" id="authTabsContent">
                            <!-- Форма входа -->
                            <div class="tab-pane fade ${param.register == null ? 'show active' : ''}" id="login" role="tabpanel" aria-labelledby="login-tab">
                                <form action="<c:url value='/authenticate'/>" method="post">
                                    <div class="mb-3">
                                        <label for="loginInput" class="form-label">Email или ID пользователя</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-user"></i></span>
                                            <input type="text" class="form-control" id="loginInput" name="loginInput" 
                                                placeholder="example@mail.com или числовой ID" required>
                                        </div>
                                        <div class="form-text text-muted">
                                            Введите ваш email или ID пользователя
                                        </div>
                                    </div>
                                    
                                    <div class="mb-4">
                                        <label for="password" class="form-label">Пароль</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                            <input type="password" class="form-control" id="password" name="password" required>
                                        </div>
                                    </div>
                                    
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-primary btn-lg">
                                            <i class="fas fa-sign-in-alt me-2"></i>Войти
                                        </button>
                                    </div>
                                    
                                    <!-- CSRF токен -->
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                </form>
                            </div>
                            
                            <!-- Форма регистрации -->
                            <div class="tab-pane fade ${param.register != null ? 'show active' : ''}" id="register" role="tabpanel" aria-labelledby="register-tab">
                                <form action="<c:url value='/register/process'/>" method="post">
                                    <div class="mb-3">
                                        <label for="registerEmail" class="form-label">Email</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                            <input type="email" class="form-control" id="registerEmail" name="email" required>
                                        </div>
                                        <div class="form-text text-muted">
                                            Обязательно указывайте действительный email
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="registerName" class="form-label">Имя</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-user"></i></span>
                                            <input type="text" class="form-control" id="registerName" name="name" required>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="registerPassword" class="form-label">Пароль</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                            <input type="password" class="form-control" id="registerPassword" name="password" 
                                                pattern=".{6,}" title="Минимум 6 символов" required>
                                        </div>
                                        <div class="form-text text-muted">
                                            Пароль должен содержать не менее 6 символов
                                        </div>
                                    </div>
                                    
                                    <div class="mb-4">
                                        <label for="confirmPassword" class="form-label">Подтверждение пароля</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                        </div>
                                    </div>
                                    
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-success btn-lg">
                                            <i class="fas fa-user-plus me-2"></i>Зарегистрироваться
                                        </button>
                                    </div>
                                    
                                    <!-- CSRF токен -->
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                </form>
                            </div>
                        </div>
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
            // Проверка совпадения паролей при регистрации
            $('#registerPassword, #confirmPassword').on('keyup', function() {
                if ($('#registerPassword').val() == $('#confirmPassword').val()) {
                    $('#confirmPassword').removeClass('is-invalid').addClass('is-valid');
                } else {
                    $('#confirmPassword').removeClass('is-valid').addClass('is-invalid');
                }
            });
            
            // Проверка формы перед отправкой
            $('form').on('submit', function(e) {
                if ($(this).find('#registerPassword').length > 0) {
                    if ($('#registerPassword').val() != $('#confirmPassword').val()) {
                        e.preventDefault();
                        alert('Пароли не совпадают!');
                    }
                }
            });
            
            // Автоматическое закрытие уведомлений через 5 секунд
            setTimeout(function() {
                $('.alert').alert('close');
            }, 5000);
        });
    </script>
</body>
</html> 