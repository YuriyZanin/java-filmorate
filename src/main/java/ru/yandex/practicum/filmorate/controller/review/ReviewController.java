package ru.yandex.practicum.filmorate.controller.review;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.review.ReviewService;
import ru.yandex.practicum.filmorate.storage.review.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.util.ValidationUtil;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto create(@Valid @RequestBody ReviewDto reviewDetails, BindingResult errors) {
        ValidationUtil.checkErrors(errors);
        return ReviewMapper.toReviewDto(reviewService.create(ReviewMapper.toReview(reviewDetails)));
    }

    @PutMapping
    public ReviewDto update(@Valid @RequestBody ReviewDto reviewDetails, BindingResult errors) {
        ValidationUtil.checkErrors(errors);
        return ReviewMapper.toReviewDto(reviewService.update(ReviewMapper.toReview(reviewDetails)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public ReviewDto findById(@PathVariable Long id) {
        return ReviewMapper.toReviewDto(reviewService.get(id));
    }

    @GetMapping
    public Collection<ReviewDto> findByFilmOrDefault(@RequestParam(required = false) Long filmId,
                                                     @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getByFilmOrDefault(filmId, count).stream()
                .map(ReviewMapper::toReviewDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
    }
}