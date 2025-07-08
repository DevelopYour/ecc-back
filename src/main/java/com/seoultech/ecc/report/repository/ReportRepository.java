package com.seoultech.ecc.report.repository;

import com.seoultech.ecc.report.datamodel.ReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends MongoRepository<ReportDocument, String> {
    List<ReportDocument> findByTeamIdOrderByWeekAsc(Integer teamId);
    ReportDocument getById(String reportId);
}
