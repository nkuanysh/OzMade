package com.example.ozmade.main.userHome

import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeHomeRepository @Inject constructor() : HomeRepository {
    override suspend fun getHome(): HomeResponse {
        delay(500)

        val ads = listOf(
            AdBanner("ad_1", title = "Реклама 1"),
            AdBanner("ad_2", title = "Реклама 2"),
            AdBanner("ad_3", title = "Реклама 3"),
            AdBanner("ad_4", title = "Реклама 4"),
            AdBanner("ad_5", title = "Реклама 5"),
        )

        val categories = listOf(
            Category("food", "Еда"),
            Category("clothes", "Одежда"),
            Category("art", "Искусство"),
            Category("craft", "Ремесло"),
            Category("gifts", "Подарки"),
            Category("holidays", "Праздники"),
            Category("home", "Для дома"),
        )

        val products = listOf(
            Product("1", "Домашний сыр", 2500.0, "Алматы", "Алмалинский р-н", 4.8, categoryId = "food"),
            Product("2", "Тойбастар набор", 5500.0, "Алматы", "Ауэзовский р-н", 4.6, categoryId = "holidays"),
            Product("3", "Кукла ручной работы", 12000.0, "Шымкент", "Центр", 4.9, categoryId = "craft"),
            Product("4", "Наурыз-көже", 1500.0, "Тараз", "Мкр. 12", 4.5, categoryId = "food"),
            Product("5", "Свитшот", 9900.0, "Астана", "Сарыарка", 4.3, categoryId = "clothes"),
            Product("6", "Картина (арт)", 30000.0, "Алматы", "Бостандык", 4.7, categoryId = "art"),
            Product("7", "Пельмени домашние", 2800.0, "Алматы", "Медеуский р-н", 4.4, categoryId = "food"),
            Product("8", "Букет к 8 марта", 7000.0, "Алматы", "Жетысу", 4.9, categoryId = "holidays"),
        )

        return HomeResponse(ads, categories, products)
    }
}
