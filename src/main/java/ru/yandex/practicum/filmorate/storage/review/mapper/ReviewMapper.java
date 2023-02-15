package ru.yandex.practicum.filmorate.storage.review.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;

import java.util.LinkedList;
import java.util.List;

public class ReviewMapper {
    public static List<Review> makeReviewList(SqlRowSet rs) {
        final List<Review> reviews = new LinkedList<>();
        while (rs.next()) {
            reviews.add(makeReview(rs));
        }
        return reviews;
    }

    public static Review makeReview(SqlRowSet rs) {
        return Review.builder()
                .id(rs.getLong("ID"))
                .user(UserMapper.makeUser(rs))
                .film(FilmMapper.makeFilm(rs))
                .content(rs.getString("CONTENT"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .useful(rs.getInt("USEFUL"))
                .build();
    }

    public static Review toReview(ReviewDto reviewDto) {
        return Review.builder()
                .id(reviewDto.getReviewId())
                .user(User.builder().id(reviewDto.getUserId()).build())
                .film(Film.builder().id(reviewDto.getFilmId()).build())
                .content(reviewDto.getContent())
                .isPositive(reviewDto.getIsPositive())
                .useful(reviewDto.getUseful())
                .build();
    }

    public static ReviewDto toReviewDto(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .filmId(review.getFilm().getId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .useful(review.getUseful())
                .build();
    }
}
