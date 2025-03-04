/**
 * CloudMessenger Firebase и WebSocket клиент
 * Обеспечивает работу с WebSocket для real-time сообщений и Firebase для уведомлений
 */

// Глобальные переменные
let stompClient = null;
let currentUserId = null;
let firebaseMessaging = null;
let notificationPermissionRequested = false;

// Константы 
const FIREBASE_CONFIG = {
    apiKey: "YOUR_API_KEY", // Заменить на реальные значения при настройке Firebase
    authDomain: "cloudmessenger-xxxxx.firebaseapp.com",
    databaseURL: "https://cloudmessenger-xxxxx.firebaseio.com",
    projectId: "cloudmessenger-xxxxx",
    storageBucket: "cloudmessenger-xxxxx.appspot.com",
    messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
    appId: "YOUR_APP_ID"
};

/**
 * Инициализация WebSocket соединения
 * @param {number} userId - ID текущего пользователя
 */
function initializeWebSocket(userId) {
    currentUserId = userId;
    
    // Создаем SockJS соединение
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    // Опционально отключаем логи STOMP
    stompClient.debug = null;
    
    // Подключение к WebSocket серверу
    stompClient.connect({}, function(frame) {
        console.log('Connected to WebSocket: ' + frame);
        
        // Подписываемся на личные сообщения
        stompClient.subscribe('/topic/user.' + userId, onMessageReceived);
        
        // Подписываемся на обновления статусов сообщений
        stompClient.subscribe('/topic/user.' + userId + '.status', onStatusUpdate);
        
        // Вызываем колбэк при успешном подключении
        if (typeof onConnected === 'function') {
            onConnected();
        }
        
        // Подписываемся на группы, если они есть
        if (window.userGroups && Array.isArray(window.userGroups)) {
            window.userGroups.forEach(group => {
                subscribeToGroup(group.id);
            });
        }
    }, onError);
}

/**
 * Подписка на сообщения группы
 * @param {number} groupId - ID группы
 */
function subscribeToGroup(groupId) {
    if (stompClient) {
        stompClient.subscribe('/topic/group.' + groupId, onGroupMessageReceived);
        console.log('Subscribed to group: ' + groupId);
    }
}

/**
 * Обработчик ошибок WebSocket
 */
function onError(error) {
    console.error('WebSocket error:', error);
    
    // Попытка переподключения через 5 секунд
    setTimeout(function() {
        if (currentUserId) {
            initializeWebSocket(currentUserId);
        }
    }, 5000);
}

/**
 * Обработчик получения личного сообщения
 * @param {object} payload - Полученное сообщение
 */
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    console.log('Message received:', message);
    
    // Обновляем статус сообщения на "DELIVERED"
    updateMessageStatus(message.id, 'DELIVERED');
    
    // Вызываем колбэк для отображения сообщения в UI
    if (typeof onNewMessage === 'function') {
        onNewMessage(message);
    }
    
    // Проигрываем звук уведомления
    playNotificationSound();
}

/**
 * Обработчик получения группового сообщения
 * @param {object} payload - Полученное групповое сообщение
 */
function onGroupMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    console.log('Group message received:', message);
    
    // Не отправляем обновление статуса для своих сообщений
    if (message.sender.id !== currentUserId) {
        // Обновляем статус только для чужих сообщений
        updateMessageStatus(message.id, 'DELIVERED');
    }
    
    // Вызываем колбэк для отображения сообщения в UI
    if (typeof onNewGroupMessage === 'function') {
        onNewGroupMessage(message);
    }
    
    // Проигрываем звук уведомления если это не наше сообщение
    if (message.sender.id !== currentUserId) {
        playNotificationSound();
    }
}

/**
 * Обработчик обновления статуса сообщения
 * @param {object} payload - Данные обновления статуса
 */
function onStatusUpdate(payload) {
    const update = JSON.parse(payload.body);
    console.log('Message status update:', update);
    
    // Вызываем колбэк для обновления UI
    if (typeof onMessageStatusUpdate === 'function') {
        onMessageStatusUpdate(update.messageId, update.status);
    }
}

/**
 * Отправка личного сообщения
 * @param {number} receiverId - ID получателя
 * @param {string} content - Текст сообщения
 * @param {string} mediaUrl - URL медиафайла (опционально)
 * @param {string} mediaType - Тип медиафайла (опционально)
 */
function sendMessage(receiverId, content, mediaUrl = null, mediaType = null) {
    if (!stompClient) {
        console.error('WebSocket не подключен');
        return;
    }
    
    const chatMessage = {
        receiverId: receiverId,
        content: content,
        mediaUrl: mediaUrl,
        mediaType: mediaType
    };
    
    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
}

/**
 * Отправка группового сообщения
 * @param {number} groupId - ID группы
 * @param {string} content - Текст сообщения
 * @param {string} mediaUrl - URL медиафайла (опционально)
 * @param {string} mediaType - Тип медиафайла (опционально)
 */
function sendGroupMessage(groupId, content, mediaUrl = null, mediaType = null) {
    if (!stompClient) {
        console.error('WebSocket не подключен');
        return;
    }
    
    const chatMessage = {
        content: content,
        mediaUrl: mediaUrl,
        mediaType: mediaType
    };
    
    stompClient.send('/app/chat.group.' + groupId, {}, JSON.stringify(chatMessage));
}

/**
 * Обновление статуса сообщения
 * @param {number} messageId - ID сообщения
 * @param {string} status - Новый статус (SENT, DELIVERED, READ)
 */
function updateMessageStatus(messageId, status) {
    if (!stompClient) {
        console.error('WebSocket не подключен');
        return;
    }
    
    const statusUpdate = {
        messageId: messageId,
        status: status
    };
    
    stompClient.send('/app/chat.updateStatus', {}, JSON.stringify(statusUpdate));
}

/**
 * Воспроизведение звука уведомления
 */
function playNotificationSound() {
    const audio = new Audio('/resources/sounds/notification.mp3');
    audio.play().catch(e => console.warn('Could not play notification sound', e));
}

/**
 * Инициализация Firebase для Push-уведомлений
 */
function initializeFirebase() {
    // Инициализация Firebase
    firebase.initializeApp(FIREBASE_CONFIG);
    
    // Проверяем поддержку push-уведомлений
    if ('Notification' in window && 'serviceWorker' in navigator && firebase.messaging.isSupported()) {
        // Получаем экземпляр Firebase Messaging
        firebaseMessaging = firebase.messaging();
        
        // Регистрируем Service Worker
        navigator.serviceWorker.register('/firebase-messaging-sw.js')
            .then((registration) => {
                console.log('Service Worker registered');
                
                // Устанавливаем Service Worker для Push-уведомлений
                firebaseMessaging.useServiceWorker(registration);
                
                // Запрашиваем разрешение на уведомления
                requestNotificationPermission();
                
                // Обработчик получения push-уведомлений в фоновом режиме
                firebaseMessaging.onBackgroundMessage((payload) => {
                    console.log('Background message received:', payload);
                });
            })
            .catch(err => console.error('Service Worker registration failed:', err));
    } else {
        console.warn('Push notifications are not supported in this browser');
    }
}

/**
 * Запрос разрешения на push-уведомления
 */
function requestNotificationPermission() {
    if (notificationPermissionRequested) return;
    
    Notification.requestPermission().then((permission) => {
        if (permission === 'granted') {
            console.log('Notification permission granted');
            notificationPermissionRequested = true;
            
            // Получаем FCM токен и отправляем на сервер
            getFCMToken();
        } else {
            console.warn('Notification permission denied');
        }
    });
}

/**
 * Получение FCM токена и отправка на сервер
 */
function getFCMToken() {
    firebaseMessaging.getToken().then((token) => {
        if (token) {
            console.log('FCM Token:', token);
            sendTokenToServer(token);
        } else {
            console.warn('Failed to get FCM token');
            requestNotificationPermission();
        }
    }).catch((err) => {
        console.error('Error getting FCM token:', err);
    });
    
    // Отслеживаем обновление токена
    firebaseMessaging.onTokenRefresh(() => {
        firebaseMessaging.getToken().then((refreshedToken) => {
            console.log('FCM Token refreshed');
            sendTokenToServer(refreshedToken);
        }).catch((err) => {
            console.error('Error refreshing FCM token:', err);
        });
    });
}

/**
 * Отправка FCM токена на сервер
 * @param {string} token - FCM токен
 */
function sendTokenToServer(token) {
    // Отправляем токен на сервер для сохранения в базе данных
    fetch('/api/user/fcm-token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify({ token: token })
    })
    .then(response => {
        if (response.ok) {
            console.log('FCM token saved on server');
        } else {
            console.error('Failed to save FCM token on server');
        }
    })
    .catch(error => {
        console.error('Error saving FCM token:', error);
    });
}

/**
 * Отключение от WebSocket сервера
 */
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        stompClient = null;
        console.log('Disconnected from WebSocket server');
    }
}

// Экспорт функций
window.chatClient = {
    initializeWebSocket,
    initializeFirebase,
    sendMessage,
    sendGroupMessage,
    updateMessageStatus,
    subscribeToGroup,
    disconnect
}; 