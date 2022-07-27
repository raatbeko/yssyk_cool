package com.example.yssyk_cool.service.impl;

import com.example.yssyk_cool.dto.review.request.ReviewRequest;
import com.example.yssyk_cool.dto.review.response.ReviewResponse;
import com.example.yssyk_cool.entity.Complex;
import com.example.yssyk_cool.entity.Review;
import com.example.yssyk_cool.exception.NotFoundException;
import com.example.yssyk_cool.mapper.ReviewMapper;
import com.example.yssyk_cool.repository.ComplexRepository;
import com.example.yssyk_cool.repository.ReviewRepository;
import com.example.yssyk_cool.repository.UserRepository;
import com.example.yssyk_cool.service.ComplexService;
import com.example.yssyk_cool.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewServiceImpl implements ReviewService {

    final ComplexRepository complexRepository;

    final ReviewRepository reviewRepository;

    final UserRepository userRepository;

    @Override
    public ReviewResponse save(ReviewRequest t) {

        Review review = reviewRepository.save(Review.builder()
                .review(t.getReview())
                .grade(t.getGrade())
                .user(userRepository.findById(t.getUserId()).orElseThrow(() -> new NotFoundException("not found user", HttpStatus.NOT_FOUND)))
                .build());

        Complex complex = complexRepository.findById(t.getComplexId()).orElseThrow();
        complex.getReviews().add(review);
        complexRepository.save(complex);

        return ReviewMapper.INSTANCE.toReviewResponse(review);
    }

    @Override
    public ReviewResponse findById(Long id) {

        return ReviewMapper.INSTANCE.toReviewResponse(reviewRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<ReviewResponse> getAll() {
        return ReviewMapper.INSTANCE.toReviewResponse(reviewRepository.findAll());
    }

    @Override
    public List<ReviewResponse> getAllByComplexId(Complex complex) {

        return complex.getReviews().stream()
                .filter(review -> review.getDeletedAt() == null)
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .grade(review.getGrade())
                        .review(review.getReview()).build()).collect(Collectors.toList());
    }

    @Override
    public ReviewResponse delete(Long id) {

        Review review = reviewRepository.findById(id).orElseThrow();
        review.setDeletedAt(LocalDateTime.now());
        reviewRepository.save(review);

        return ReviewMapper.INSTANCE.toReviewResponse(review);
    }

}

