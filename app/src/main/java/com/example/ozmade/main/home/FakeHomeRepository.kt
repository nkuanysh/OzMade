package com.example.ozmade.main.home

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
            Product("1", "Домашний сыр", 2500, "Алматы", "Алмалинский р-н", 4.8),
            Product("2", "Тойбастар набор", 5500, "Алматы", "Ауэзовский р-н", 4.6),
            Product("3", "Кукла ручной работы", 12000, "Шымкент", "Центр", 4.9),
            Product("4", "Наурыз-көже", 1500, "Тараз", "Мкр. 12", 4.5),
            Product("5", "Свитшот", 9900, "Астана", "Сарыарка", 4.3),
            Product("6", "Картина (арт)", 30000, "Алматы", "Бостандык", 4.7),
            Product("7", "Пельмени домашние", 2800, "Алматы", "Медеуский р-н", 4.4),
            Product("8", "Букет к 8 марта", 7000, "Алматы", "Жетысу", 4.9),
        )

        return HomeResponse(ads, categories, products)
    }
}
