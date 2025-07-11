package com.seoultech.ecc.admin.service;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.seoultech.ecc.admin.dto.AssignedTeamDto;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import com.seoultech.ecc.team.repository.TimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamAssignmentOptimizer {

    private final TimeRepository timeRepository;

    private static final int MIN_TEAM_SIZE = 3;
    private static final int MAX_TEAM_SIZE = 5;
    private static final int MIN_TIME_GAP_HOURS = 2;

    static {
        Loader.loadNativeLibraries();
    }

    public List<AssignedTeamDto> optimizeTeamAssignment(
            List<MemberSimpleDto> members,
            Map<Integer, List<Integer>> memberSubjectMap, // memberId -> List<subjectId>
            Map<Integer, List<Integer>> memberTimeMap,    // memberId -> List<timeId>
            Map<Integer, Integer> timeHourMap,           // timeId -> hour (0-23)
            Map<Integer, SubjectEntity> subjectEntityMap) { // subjectId -> SubjectEntity

        // Create solver
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new RuntimeException("Could not create solver SCIP");
        }

        // Prepare data structures
        List<Integer> memberIds = new ArrayList<>(memberSubjectMap.keySet());
        Set<Integer> allSubjects = new HashSet<>();
        Set<Integer> allTimes = new HashSet<>();

        for (List<Integer> subjects : memberSubjectMap.values()) {
            allSubjects.addAll(subjects);
        }
        for (List<Integer> times : memberTimeMap.values()) {
            allTimes.addAll(times);
        }

        List<Integer> subjectList = new ArrayList<>(allSubjects);
        List<Integer> timeList = new ArrayList<>(allTimes);

        // Decision variables
        // x[m][s][t] = 1 if member m is assigned to subject s at time t
        Map<String, MPVariable> x = new HashMap<>();

        // y[s][t] = 1 if there is a team for subject s at time t
        Map<String, MPVariable> y = new HashMap<>();

        // Create variables
        for (Integer memberId : memberIds) {
            List<Integer> memberSubjects = memberSubjectMap.get(memberId);
            List<Integer> memberTimes = memberTimeMap.get(memberId);

            for (Integer subjectId : memberSubjects) {
                for (Integer timeId : memberTimes) {
                    String key = memberId + "_" + subjectId + "_" + timeId;
                    x.put(key, solver.makeIntVar(0, 1, "x_" + key));
                }
            }
        }

        for (Integer subjectId : subjectList) {
            for (Integer timeId : timeList) {
                String key = subjectId + "_" + timeId;
                y.put(key, solver.makeIntVar(0, 1, "y_" + key));
            }
        }

        // Constraints

        // 1. Each member can be assigned to at most one time slot per subject
        for (Integer memberId : memberIds) {
            List<Integer> memberSubjects = memberSubjectMap.get(memberId);

            for (Integer subjectId : memberSubjects) {
                MPConstraint constraint = solver.makeConstraint(0, 1);

                List<Integer> memberTimes = memberTimeMap.get(memberId);
                for (Integer timeId : memberTimes) {
                    String key = memberId + "_" + subjectId + "_" + timeId;
                    if (x.containsKey(key)) {
                        constraint.setCoefficient(x.get(key), 1);
                    }
                }
            }
        }

        // 2. Team size constraints (3-5 members per team)
        for (Integer subjectId : subjectList) {
            for (Integer timeId : timeList) {
                String yKey = subjectId + "_" + timeId;
                if (!y.containsKey(yKey)) continue;

                // Lower bound: if y[s][t] = 1, then sum(x[m][s][t]) >= 3
                MPConstraint lowerBound = solver.makeConstraint(0, Double.POSITIVE_INFINITY);
                for (Integer memberId : memberIds) {
                    String xKey = memberId + "_" + subjectId + "_" + timeId;
                    if (x.containsKey(xKey)) {
                        lowerBound.setCoefficient(x.get(xKey), 1);
                    }
                }
                lowerBound.setCoefficient(y.get(yKey), -MIN_TEAM_SIZE);

                // Upper bound: sum(x[m][s][t]) <= 5 * y[s][t]
                MPConstraint upperBound = solver.makeConstraint(Double.NEGATIVE_INFINITY, 0);
                for (Integer memberId : memberIds) {
                    String xKey = memberId + "_" + subjectId + "_" + timeId;
                    if (x.containsKey(xKey)) {
                        upperBound.setCoefficient(x.get(xKey), 1);
                    }
                }
                upperBound.setCoefficient(y.get(yKey), -MAX_TEAM_SIZE);
            }
        }

        // 3. If any member is assigned to (s,t), then y[s][t] must be 1
        for (Integer subjectId : subjectList) {
            for (Integer timeId : timeList) {
                String yKey = subjectId + "_" + timeId;
                if (!y.containsKey(yKey)) continue;

                for (Integer memberId : memberIds) {
                    String xKey = memberId + "_" + subjectId + "_" + timeId;
                    if (x.containsKey(xKey)) {
                        MPConstraint constraint = solver.makeConstraint(0, Double.POSITIVE_INFINITY);
                        constraint.setCoefficient(y.get(yKey), 1);
                        constraint.setCoefficient(x.get(xKey), -1);
                    }
                }
            }
        }

        // 4. Time gap constraint for members with multiple subjects
        for (Integer memberId : memberIds) {
            List<Integer> memberSubjects = memberSubjectMap.get(memberId);
            if (memberSubjects.size() < 2) continue;

            List<Integer> memberTimes = memberTimeMap.get(memberId);

            // For each pair of subjects
            for (int i = 0; i < memberSubjects.size(); i++) {
                for (int j = i + 1; j < memberSubjects.size(); j++) {
                    Integer subject1 = memberSubjects.get(i);
                    Integer subject2 = memberSubjects.get(j);

                    // For each pair of times
                    for (Integer time1 : memberTimes) {
                        for (Integer time2 : memberTimes) {
                            int hourWithDay1 = timeHourMap.get(time1);
                            int hourWithDay2 = timeHourMap.get(time2);

                            // Extract day and hour
                            int day1 = hourWithDay1 / 24;
                            int hour1 = hourWithDay1 % 24;
                            int day2 = hourWithDay2 / 24;
                            int hour2 = hourWithDay2 % 24;

                            // Calculate time difference
                            boolean tooClose = false;
                            if (day1 == day2) {
                                // Same day: check hour difference
                                if (Math.abs(hour1 - hour2) < MIN_TIME_GAP_HOURS) {
                                    tooClose = true;
                                }
                            }
                            // Different days are always OK (>= 2 hours apart)

                            if (tooClose) {
                                // Cannot assign both
                                MPConstraint constraint = solver.makeConstraint(0, 1);

                                String key1 = memberId + "_" + subject1 + "_" + time1;
                                String key2 = memberId + "_" + subject2 + "_" + time2;

                                if (x.containsKey(key1)) {
                                    constraint.setCoefficient(x.get(key1), 1);
                                }
                                if (x.containsKey(key2)) {
                                    constraint.setCoefficient(x.get(key2), 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Objective: Maximize the number of assignments
        MPObjective objective = solver.objective();
        for (MPVariable var : x.values()) {
            objective.setCoefficient(var, 1);
        }
        objective.setMaximization();

        // Solve
        MPSolver.ResultStatus resultStatus = solver.solve();

        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
            log.warn("The problem does not have an optimal solution!");
            return new ArrayList<>();
        }

        log.info("Solution found!");
        log.info("Objective value = " + objective.value());

        // Extract solution
        Map<String, List<MemberSimpleDto>> teamAssignments = new HashMap<>();
        Map<Integer, MemberSimpleDto> memberMap = members.stream()
                .collect(Collectors.toMap(MemberSimpleDto::getId, m -> m));

        // Create time entity map for easy lookup
        Map<Integer, TimeEntity> timeEntityMap = timeRepository.findAll().stream()
                .collect(Collectors.toMap(TimeEntity::getId, t -> t));

        for (Map.Entry<String, MPVariable> entry : x.entrySet()) {
            if (entry.getValue().solutionValue() > 0.5) {
                String[] parts = entry.getKey().split("_");
                Integer memberId = Integer.parseInt(parts[0]);
                Integer subjectId = Integer.parseInt(parts[1]);
                Integer timeId = Integer.parseInt(parts[2]);

                String teamKey = subjectId + "_" + timeId;
                teamAssignments.computeIfAbsent(teamKey, k -> new ArrayList<>())
                        .add(memberMap.get(memberId));
            }
        }

        // Convert to DTOs
        List<AssignedTeamDto> result = new ArrayList<>();
        for (Map.Entry<String, List<MemberSimpleDto>> entry : teamAssignments.entrySet()) {
            String[] parts = entry.getKey().split("_");
            Integer subjectId = Integer.parseInt(parts[0]);
            Integer timeId = Integer.parseInt(parts[1]);

            TimeEntity timeEntity = timeEntityMap.get(timeId);
            SubjectEntity subjectEntity = subjectEntityMap.get(subjectId);

            AssignedTeamDto dto = AssignedTeamDto.builder()
                    .members(entry.getValue())
                    .subjectId(subjectId)
                    .subjectName(subjectEntity.getName())
                    .timeId(timeId)
                    .day(timeEntity.getDay())
                    .startTime(timeEntity.getStartTime())
                    .build();

            result.add(dto);
        }

        return result;
    }
}