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
            "        SELECT r.*,\n" +
                    "           u.id as user_id, \n" +
                    "           u.name as user_name, \n" +
                    "           u.birthday, \n" +
                    "           u.login, \n" +
                    "           u.email, \n" +
                    "           array_agg(fr.friend_id) as friends_ids, \n" +
                    "           (CASE WHEN likes_count IS NOT NULL THEN likes_count ELSE 0 END - \n" +
                    "           CASE WHEN dislikes_count IS NOT NULL THEN dislikes_count ELSE 0 END) AS useful, \n" +
                    "           f.name AS film_name,\n" +
                    "           f.release_date,\n" +
                    "           f.duration,\n" +
                    "           f.description,\n" +
                    "           f.rating_id,\n" +
                    "           rt.name AS rating_name,\n" +
                    "           array_agg(genre_id) AS genre_ids,\n" +
                    "           array_agg(g.name) AS genre_names,\n" +
                    "           array_agg(fwlu.who_liked_user_id) AS who_liked_users_ids,\n" +
                    "           array_agg(d.id) AS director_ids,\n" +
                    "           array_agg(d.name) AS director_names\n" +
                    "FROM reviews r \n" +
                    "INNER JOIN users u ON u.id = r.user_id\n" +
                    "LEFT JOIN friendships fr ON fr.user_id = u.id\n" +
                    "INNER JOIN films f ON f.id = r.film_id\n" +
                    "INNER JOIN ratings rt ON rt.id = f.rating_id\n" +
                    "LEFT JOIN film_genres fg ON fg.film_id = f.id\n" +
                    "LEFT JOIN genres g ON g.id = fg.genre_id\n" +
                    "LEFT JOIN film_who_liked_users fwlu ON fwlu.film_id = f.id\n" +
                    "LEFT JOIN film_directors fd ON fd.film_id = f.id\n" +
                    "LEFT JOIN directors d ON d.id = fd.director_id\n" +
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
            log.info("Отзыв пользователя {} создан с id {}", review.getUser().getId(), reviewId);
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
