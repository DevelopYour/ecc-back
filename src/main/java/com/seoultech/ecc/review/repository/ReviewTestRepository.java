package com.seoultech.ecc.review.repository;

import com.seoultech.ecc.review.datamodel.ReviewTestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewTestRepository extends MongoRepository<ReviewTestDocument, String> {
}
