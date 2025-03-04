/**
 * Firebase Cloud Messaging Service Worker
 * Обрабатывает push-уведомления для CloudMessenger
 */

// Импортируем Firebase скрипты
importScripts('https://www.gstatic.com/firebasejs/9.2.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.2.0/firebase-messaging-compat.js');

// Конфигурация Firebase (должна соответствовать конфигурации в firebase-client.js)
const firebaseConfig = {
    apiKey: "YOUR_API_KEY", // Заменить на реальные значения при настройке Firebase
    authDomain: "cloudmessenger-xxxxx.firebaseapp.com",
    databaseURL: "https://cloudmessenger-xxxxx.firebaseio.com",
    projectId: "cloudmessenger-xxxxx",
    storageBucket: "cloudmessenger-xxxxx.appspot.com",
    messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
    appId: "YOUR_APP_ID"
};

// Инициализируем Firebase
firebase.initializeApp(firebaseConfig);

// Инициализируем Firebase Cloud Messaging
const messaging = firebase.messaging();

/**
 * Обработчик входящих FCM сообщений в фоновом режиме
 */
messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    
    // Данные уведомления
    const notificationTitle = payload.notification.title || 'Cloud Messenger';
    const notificationOptions = {
        body: payload.notification.body || 'У вас новое сообщение',
        icon: '/resources/images/logo.png',
        badge: '/resources/images/badge.png',
        tag: payload.data && payload.data.messageId ? 'message-' + payload.data.messageId : 'cloudmessenger',
        data: payload.data || {},
        // Вибрация для уведомления
        vibrate: [100, 50, 100],
        // Действия при нажатии на уведомление
        actions: [
            {
                action: 'reply',
                title: 'Ответить',
                icon: '/resources/images/reply.png'
            },
            {
                action: 'close',
                title: 'Закрыть',
                icon: '/resources/images/close.png'
            }
        ]
    };
    
    // Показываем уведомление
    return self.registration.showNotification(notificationTitle, notificationOptions);
});

/**
 * Обработчик нажатия на уведомление
 */
self.addEventListener('notificationclick', (event) => {
    console.log('[firebase-messaging-sw.js] Notification click', event);
    
    // Закрываем уведомление
    event.notification.close();
    
    // Обрабатываем нажатия на специальные действия
    if (event.action === 'reply') {
        // Действия при нажатии "Ответить"
        // Можно открыть окно для ответа, но это требует дополнительного кода
        console.log('User wants to reply');
    } else if (event.action === 'close') {
        // Действия при нажатии "Закрыть"
        // Просто закрываем уведомление (уже сделано выше)
        return;
    } else {
        // Основное действие при нажатии на уведомление - открываем приложение
        // Получаем данные из уведомления
        const messageData = event.notification.data;
        let url = '/messages';
        
        // Если есть ID сообщения или отправителя, открываем конкретный чат
        if (messageData && messageData.senderId) {
            url = '/messages?user=' + messageData.senderId;
        } else if (messageData && messageData.groupId) {
            url = '/groups/' + messageData.groupId;
        }
        
        // Открываем чат или приложение
        const promiseChain = clients.matchAll({
            type: 'window',
            includeUncontrolled: true
        }).then((windowClients) => {
            // Проверяем, открыто ли приложение
            for (let i = 0; i < windowClients.length; i++) {
                const client = windowClients[i];
                // Если приложение открыто, фокусируемся на нем и переходим на нужную страницу
                if ('focus' in client) {
                    client.focus();
                    client.navigate(url);
                    return;
                }
            }
            
            // Если приложение не открыто, открываем его
            if (clients.openWindow) {
                return clients.openWindow(url);
            }
        });
        
        event.waitUntil(promiseChain);
    }
});

/**
 * Обработчик закрытия уведомления пользователем
 */
self.addEventListener('notificationclose', (event) => {
    console.log('[firebase-messaging-sw.js] Notification closed', event);
});

/**
 * Обработчик push-сообщений
 */
self.addEventListener('push', (event) => {
    console.log('[firebase-messaging-sw.js] Push received', event);
    
    // Если сообщение не содержит данных, показываем заглушку
    if (!event.data) {
        console.log('Push event but no data');
        return;
    }
    
    // Получаем данные из push-сообщения
    const data = event.data.json();
    
    // Показываем уведомление
    const title = data.notification.title || 'Cloud Messenger';
    const options = {
        body: data.notification.body || 'У вас новое сообщение',
        icon: '/resources/images/logo.png',
        badge: '/resources/images/badge.png',
        data: data.data || {}
    };
    
    event.waitUntil(self.registration.showNotification(title, options));
}); 