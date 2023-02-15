package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.mapper.ReviewMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    public static final String REVIEWS_QUERY =
            "SELECT r.*,\n" +
                    "(CASE WHEN likes_count IS NOT NULL THEN likes_count ELSE 0 END - \n" +
                    "CASE WHEN dislikes_count IS NOT NULL THEN dislikes_count ELSE 0 END) AS useful \n" +
                    "FROM reviews r\n" +
                    "LEFT JOIN (SELECT lr.review_id, COUNT(lr.user_id) AS likes_count\n" +
                    "           FROM review_ratings lr WHERE lr.is_like = true\n" +
                    "           GROUP BY lr.review_id) AS likes ON likes.review_id = r.id\n" +
                    "LEFT JOIN (SELECT dr.review_id, COUNT(dr.user_id) AS dislikes_count\n" +
                    "           FROM review_ratings dr WHERE dr.is_like = false\n" +
                    "           GROUP BY dr.review_id) AS dislikes ON dislikes.review_id = r.id\n" +
                    "GROUP BY r.id\n" +
                    "ORDER BY useful DESC\n";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        try {
            Long reviewId = simpleJdbcInsert.executeAndReturnKey(reviewToParameters(review)).longValue();
            log.info("Отзыв пользователя {} создан с id {}", review.getUser().getName(), reviewId);
            review.setId(reviewId);
            return review;
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    @Override
    public Review update(Review review) {
        String query = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(query,
                review.getContent(),
                review.getIsPositive(),
                review.getId());

        if (updatedRows == 0) {
            return null;
        }
        log.info("Отзыв {} обновлен", review.getId());
        return get(review.getId()).orElse(null);
    }

    @Override
    public boolean delete(Long id) {
        return jdbcTemplate.update("DELETE FROM reviews WHERE id = ?", id) == 1;
    }

    @Override
    public Optional<Review> get(Long id) {
        String query = REVIEWS_QUERY.replace("GROUP BY r.id", "WHERE r.id = ? GROUP BY r.id\n");
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
            return ReviewMapper.makeReviewList(rowSet).stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> getAllWithLimit(Integer count) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(REVIEWS_QUERY + " LIMIT ?", count);
        return ReviewMapper.makeReviewList(rowSet);
    }

    @Override
    public Collection<Review> getByFilm(Long filmId, Integer count) {
        String query = REVIEWS_QUERY.replace("GROUP BY r.id", "WHERE r.film_id = ? GROUP BY r.id\n");
        query += "LIMIT ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, filmId, count);
        return ReviewMapper.makeReviewList(rowSet);
    }

    @Override
    public boolean addLike(Long id, Long userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("review_ratings");
        try {
            return simpleJdbcInsert.execute(likesToParameters(id, userId, true)) == 1;
        } catch (DuplicateKeyException e) {
            log.warn("Лайк пользователя {} к отзыву {} был добавлен ранее", userId, id);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public boolean addDislike(Long id, Long userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("review_ratings");
        try {
            return simpleJdbcInsert.execute(likesToParameters(id, userId, false)) == 1;
        } catch (DuplicateKeyException e) {
            log.warn("Дизлайк пользователя {} к отзыву {} был добавлен ранее", userId, id);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public boolean removeLike(Long id, Long userId) {
        return jdbcTemplate.update(
                "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ? AND is_like = true", id, userId) == 1;
    }

    @Override
    public boolean removeDislike(Long id, Long userId) {
        return jdbcTemplate.update(
                "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ? AND is_like = false", id, userId) == 1;
    }

    private Map<String, Object> reviewToParameters(Review review) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", review.getId());
        parameters.put("user_id", review.getUser().getId());
        parameters.put("film_id", review.getFilm().getId());
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        return parameters;
    }

    private Map<String, Object> likesToParameters(Long reviewId, Long userId, boolean value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", userId);
        parameters.put("review_id", reviewId);
        parameters.put("is_like", value);
        return parameters;
    }
}
