package com.example.ozmade.main.reviews

import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeReviewsRepository @Inject constructor() : ReviewsRepository {

    override suspend fun getReviews(productId: String): ReviewsResponse {
        delay(450)

        val list = when (productId) {
            "1" -> listOf(
                ReviewUi("r1", "Дархан", 5.0, "15.02.2026", "Очень вкусно! Закажу ещё."),
                ReviewUi("r2", "Фараби", 4.5, "11.02.2026", "Качество отличное, доставка быстрая."),
                ReviewUi("r3", "Байсат", 2.0, "02.02.2026", "Нормально, но ожидал больше.")
            )
                "2" -> listOf(
                    ReviewUi("r1", "Алия", 5.0, "15.02.2026", "Очень вкусно! Закажу ещё."),
                    ReviewUi("r2", "Данияр", 4.5, "11.02.2026", "Качество отличное, доставка быстрая."),
                    ReviewUi("r3", "Руслан", 3.0, "02.02.2026", "Нормально, но ожидал больше.")
                )
            else -> emptyList()
        }


        val avg = list.map { it.rating }.average()

        val summary = ReviewsSummaryUi(
            productId = productId,
            averageRating = avg,
            ratingsCount = list.size,
            reviewsCount = list.size
        )

        return ReviewsResponse(summary, list)
    }
}
