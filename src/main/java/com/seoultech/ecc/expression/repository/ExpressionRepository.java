package com.seoultech.ecc.expression.repository;

import com.seoultech.ecc.expression.datamodel.ExpressionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpressionRepository extends JpaRepository<ExpressionEntity, Long> {
}
