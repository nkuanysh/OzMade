Front:

Login

Register -
Buyer - with number -> SMS
Seller - with number and electronic identity card
For buyer:
Main:
list of products as cards with photo like button, cost of that product
Number of views for each product and rating review
Type of product: food, handmade…



Profile:
Edit:
Number
Email
Address
My orders
Become seller
Favourite:
List of cards

Chat:
List of chats:
Details page:
description
Report
View contacts - whatsapp number applink
Comments
Address
Add to favorites
For Seller:
Main:
List of selling products
Delete
Add:
Type
Picture
Detail
Address


Profile:
Edit
Profile picture
Number
Email
Address


	Details page:
Edit everything
Chats:
List of chats with customers



Backend:
Front:
Login → POST /auth/login
Register
Buyer (with number -> SMS) → POST /auth/register/buyer (request OTP) & POST /auth/verify-otp
Seller (number + electronic ID) → POST /auth/register/seller (multi-part form data for ID upload)

For Buyer:
Main:
List of products as cards → GET /products (returns photo, like_status, cost)
Number of views and rating/review → GET /products/{id}/stats
Type of product → GET /categories
Profile:
Edit (Number, Email, Address) → PATCH /user/profile
My orders → GET /orders
Become seller → POST /user/upgrade
Favourite:
List of cards → GET /user/favorites
Chat:
List of chats → GET /chats
Chat details → GET /chats/{chat_id}/messages
Details page:
Description, Comments, Address → GET /products/{id}
Report → POST /products/{id}/report
View contacts (WhatsApp link) → GET /products/{id}/contact
Add to favorites → POST /user/favorites/{id}

For Seller:
Main:
List of selling products → GET /seller/products
Delete → DELETE /seller/products/{id}
Add:
Type, Picture, Detail, Address → POST /seller/products
Profile:
Edit (Picture, Number, Email, Address) → PATCH /seller/profile
Details page:
Edit everything → PUT /seller/products/{id}
Chats:
List of chats with customers → GET /seller/chats




Архитектура пользователей и ролей в приложении OzMade
1. Общая концепция
   В приложении используется единая модель пользователя.
   Разделение на покупателя и продавца не происходит при регистрации, а определяется действиями пользователя.
   Один аккаунт может одновременно выполнять роль покупателя и продавца.
   Это снижает порог входа и упрощает пользовательский опыт.

2. Состояния пользователя
   2.1 Гость (неавторизованный пользователь)
   Пользователь без аккаунта может:
   просматривать главную страницу


смотреть каталог продуктов


открывать карточки товаров (ограниченно)


Ограничения:
нельзя видеть контакты продавца


нельзя писать в чат


нельзя оставлять отзывы


При попытке взаимодействия система перенаправляет на экран входа.

2.2 Авторизованный пользователь (по умолчанию — покупатель)
После входа или регистрации пользователь получает статус покупателя.
Функции покупателя:
просмотр каталога


фильтрация по регионам


просмотр карточек товаров


просмотр рейтинга и отзывов


отправка запроса продавцу


участие в чате


подтверждение получения товара


оставление отзывов



3. Регистрация и вход
   3.1 Процесс аутентификации
   Ввод номера телефона


Подтверждение через SMS-код (OTP)


Создание пароля


Вход в систему


Регистрация по номеру телефона используется как базовый уровень идентификации пользователя.

4. Переход в роль продавца
   4.1 Кнопка «Стать продавцом»
   Располагается на странице Профиля пользователя.

4.2 Регистрация продавца (Seller Onboarding)
При нажатии «Стать продавцом» пользователь переходит на экран регистрации продавца и заполняет:
имя / название


регион и город


типы продуктов


условия доставки:


самовывоз


доставка по городу


межгород


контактная информация


(опционально) документы для верификации


После отправки:
статус продавца = pending (на проверке)


Пока заявка не одобрена:
пользователь остаётся покупателем


доступ к экрану продавца закрыт



5. Модерация и активация продавца
   После ручной проверки администратором:
   статус меняется на approved


пользователю открывается режим продавца


появляется возможность создавать карточки товаров



6. Режим продавца
   В режиме продавца доступны:
   создание и управление карточками товаров


чат с покупателями


просмотр заявок и заказов


управление доставкой


просмотр отзывов и рейтинга


статус верификации продавца



7. Переключение между режимами
   Переключение осуществляется через профиль:
   «Переключиться в режим продавца»


«Переключиться в режим покупателя»


Важно:
это не новая регистрация


статус продавца сохраняется


повторная модерация не требуется


Переключение меняет интерфейс и доступные функции, но не аккаунт.

8. Логическая модель данных (упрощённо)
   User
- id
- phone
- password
- isSeller
- sellerStatus (none / pending / approved)
- currentMode (buyer / seller)

SellerProfile
- userId
- region
- deliveryOptions
- verificationStatus

Product
- sellerId
- title
- description
- price
- media
- availability

OrderRequest
- buyerId
- sellerId
- productId
- status


9. Почему эта архитектура правильная
   снижает барьер входа


исключает дубли аккаунтов


поддерживает рост продавцов


масштабируется


соответствует реальным маркетплейсам


подходит для MVP и дипломной работы



10. Короткое резюме
    В OzMade используется единый аккаунт с динамическими ролями. Пользователь по умолчанию является покупателем и может в любой момент подать заявку на роль продавца через профиль. После одобрения он свободно переключается между режимами покупателя и продавца без повторной регистрации.

