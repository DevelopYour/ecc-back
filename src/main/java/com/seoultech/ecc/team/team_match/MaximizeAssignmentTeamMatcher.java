package com.seoultech.ecc.team.team_match;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.seoultech.ecc.team.dto.ApplyRegularStudyDto;
import com.seoultech.ecc.team.dto.TeamMatchDto;
import com.seoultech.ecc.team.dto.TeamMatchResultDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaximizeAssignmentTeamMatcher {

    private static final int MIN_TEAM_SIZE = 3;
    private static final int MAX_TEAM_SIZE = 5;

    // 수정된 메인 팀매칭 메서드 - 이전 결과를 고려하여 필터링
    public TeamMatchResultDto optimizeTeamMatching(
            List<ApplyRegularStudyDto> applies,
            List<TeamMatchResultDto> previousResults) {

        // 학생별 시간 맵 생성
        Map<String, List<Integer>> studentTimeMap = new HashMap<>();
        Integer subjectId = null;

        for (ApplyRegularStudyDto apply : applies) {
            String memberId = apply.getMemberId().toString();
            Integer timeId = apply.getTimeId();

            if (subjectId == null) {
                subjectId = apply.getSubjectId();
            }

            studentTimeMap.computeIfAbsent(memberId, k -> new ArrayList<>()).add(timeId);
        }

        // 이전 결과에서 이미 배정된 학생-시간 조합 추출
        Set<String> assignedStudentTimeCombo = extractAssignedCombinations(previousResults);

        // 필터링된 학생-시간 맵 생성
        Map<String, List<Integer>> filteredStudentTimeMap = filterAvailableTimes(
                studentTimeMap, assignedStudentTimeCombo);

        // 빈 시간대를 가진 학생 제거
        filteredStudentTimeMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        System.out.println("\n=== 과목 " + subjectId + " 팀매칭 시작 ===");
        System.out.println("원본 학생 수: " + studentTimeMap.size());
        System.out.println("필터링 후 학생 수: " + filteredStudentTimeMap.size());

        // 팀매칭 실행
        List<List<String>> teams = optimizeTeamMatchingInternal(filteredStudentTimeMap);

        // 결과 생성
        TeamMatchResultDto result = new TeamMatchResultDto();
        result.setSubjectId(subjectId);

        // 팀 정보 생성
        List<TeamMatchDto> teamMatchDtoList = new ArrayList<>();
        for (List<String> team : teams) {
            // 팀의 공통 시간대 찾기
            Set<Integer> commonTimes = getCommonTimeSlots(team, filteredStudentTimeMap);
            Integer selectedTime = commonTimes.iterator().next(); // 첫 번째 공통 시간 선택

            TeamMatchDto teamDto = new TeamMatchDto();
            teamDto.setTimeId(selectedTime);
            teamDto.setMemberIds(team.stream().map(Integer::parseInt).collect(Collectors.toList()));
            teamMatchDtoList.add(teamDto);
        }
        result.setTeamMatchDtoList(teamMatchDtoList);

        // 실패한 학생 찾기
        Set<String> assignedStudents = teams.stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        List<Integer> failedMemberIds = studentTimeMap.keySet().stream()
                .filter(memberId -> !assignedStudents.contains(memberId))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        result.setFailedMemberIdList(failedMemberIds);

        System.out.println("생성된 팀 수: " + teams.size());
        System.out.println("실패한 학생 수: " + failedMemberIds.size());

        return result;
    }

    // 이전 결과에서 배정된 학생-시간 조합 추출
    private Set<String> extractAssignedCombinations(List<TeamMatchResultDto> previousResults) {
        Set<String> combinations = new HashSet<>();

        for (TeamMatchResultDto result : previousResults) {
            for (TeamMatchDto team : result.getTeamMatchDtoList()) {
                Integer timeId = team.getTimeId();
                for (Integer memberId : team.getMemberIds()) {
                    combinations.add(memberId + "-" + timeId);
                }
            }
        }

        return combinations;
    }

    // 이미 배정된 시간 필터링
    private Map<String, List<Integer>> filterAvailableTimes(
            Map<String, List<Integer>> originalMap,
            Set<String> assignedCombinations) {

        Map<String, List<Integer>> filteredMap = new HashMap<>();

        for (Map.Entry<String, List<Integer>> entry : originalMap.entrySet()) {
            String memberId = entry.getKey();
            List<Integer> availableTimes = new ArrayList<>();

            for (Integer timeId : entry.getValue()) {
                String combo = memberId + "-" + timeId;
                if (!assignedCombinations.contains(combo)) {
                    availableTimes.add(timeId);
                }
            }

            if (!availableTimes.isEmpty()) {
                filteredMap.put(memberId, availableTimes);
            }
        }

        return filteredMap;
    }

    // 기존의 optimizeTeamMatching 로직 (내부용으로 변경)
    private List<List<String>> optimizeTeamMatchingInternal(Map<String, List<Integer>> studentTimeMap) {
        List<List<String>> teamCandidates = generateTeamCandidates(studentTimeMap);

        if (teamCandidates.isEmpty()) {
            System.out.println("공통 시간대가 있는 팀 조합이 없습니다.");
            return new ArrayList<>();
        }

        System.out.println("생성된 팀 후보 수: " + teamCandidates.size());

        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver("SCIP");
        List<List<String>> selectedTeams = new ArrayList<>();

        if (solver == null) {
            System.out.println("Solver를 사용할 수 없습니다. 그리디 방식으로 대체합니다.");
            selectedTeams = greedyTeamSelection(teamCandidates);
        } else {

            // 변수 생성: 각 팀 후보의 선택 여부
            Map<Integer, MPVariable> teamVars = new HashMap<>();
            for (int i = 0; i < teamCandidates.size(); i++) {
                teamVars.put(i, solver.makeIntVar(0, 1, "team_" + i));
            }

            // 제약조건: 각 학생은 최대 1개 팀에만 속함
            Set<String> allStudents = studentTimeMap.keySet();
            for (String student : allStudents) {
                MPConstraint constraint = solver.makeConstraint(0, 1);
                for (int i = 0; i < teamCandidates.size(); i++) {
                    if (teamCandidates.get(i).contains(student)) {
                        constraint.setCoefficient(teamVars.get(i), 1);
                    }
                }
            }

            // 목적함수: 배정된 학생 수 최대화
            MPObjective objective = solver.objective();
            for (int i = 0; i < teamCandidates.size(); i++) {
                int teamSize = teamCandidates.get(i).size();
                // 4명 팀에 약간의 가중치 부여 (이상적인 크기)
                double weight = teamSize == 4 ? teamSize + 0.1 : teamSize;
                objective.setCoefficient(teamVars.get(i), weight);
            }
            objective.setMaximization();

            // 최적화 실행
            MPSolver.ResultStatus result = solver.solve();

            if (result == MPSolver.ResultStatus.OPTIMAL || result == MPSolver.ResultStatus.FEASIBLE) {
                for (int i = 0; i < teamCandidates.size(); i++) {
                    if (teamVars.get(i).solutionValue() == 1) {
                        selectedTeams.add(new ArrayList<>(teamCandidates.get(i)));
                    }
                }
                System.out.println("최적화 완료: " + selectedTeams.size() + "개 팀 생성");
            } else {
                System.out.println("최적화 실패. 그리디 방식으로 대체합니다.");
                selectedTeams = greedyTeamSelection(teamCandidates);
            }
        }

        return selectedTeams;
    }

    // 3-5명 팀 후보 생성
    private List<List<String>> generateTeamCandidates(Map<String, List<Integer>> studentTimeMap) {
        List<List<String>> candidates = new ArrayList<>();
        List<String> students = new ArrayList<>(studentTimeMap.keySet());

        // 3명부터 5명까지의 모든 조합 생성
        for (int size = MIN_TEAM_SIZE; size <= Math.min(MAX_TEAM_SIZE, students.size()); size++) {
            generateCombinations(students, size, 0, new ArrayList<>(), candidates, studentTimeMap);
        }

        // 공통 시간대가 많은 팀 우선 정렬
        candidates.sort((t1, t2) -> {
            int common1 = getCommonTimeSlots(t1, studentTimeMap).size();
            int common2 = getCommonTimeSlots(t2, studentTimeMap).size();
            if (common1 != common2) return Integer.compare(common2, common1);
            return Integer.compare(t2.size(), t1.size()); // 팀 크기 큰 것 우선
        });

        return candidates;
    }

    // 조합 생성 (재귀)
    private void generateCombinations(List<String> students, int targetSize, int start,
                                      List<String> current, List<List<String>> result,
                                      Map<String, List<Integer>> studentTimeMap) {
        if (current.size() == targetSize) {
            if (hasCommonTimeSlot(current, studentTimeMap)) {
                result.add(new ArrayList<>(current));
            }
            return;
        }

        for (int i = start; i < students.size(); i++) {
            current.add(students.get(i));
            generateCombinations(students, targetSize, i + 1, current, result, studentTimeMap);
            current.remove(current.size() - 1);
        }
    }

    // 공통 시간대 확인
    private boolean hasCommonTimeSlot(List<String> team, Map<String, List<Integer>> studentTimeMap) {
        if (team.isEmpty()) return false;

        Set<Integer> commonTimes = new HashSet<>(studentTimeMap.get(team.get(0)));
        for (int i = 1; i < team.size(); i++) {
            commonTimes.retainAll(studentTimeMap.get(team.get(i)));
        }
        return !commonTimes.isEmpty();
    }

    // 공통 시간대 반환
    private Set<Integer> getCommonTimeSlots(List<String> team, Map<String, List<Integer>> timeMap) {
        if (team.isEmpty()) return new HashSet<>();

        Set<Integer> common = new HashSet<>(timeMap.get(team.get(0)));
        for (String student : team) {
            common.retainAll(timeMap.get(student));
        }
        return common;
    }

    // 그리디 방식 팀 선택 (OR-Tools 실패시 대안)
    private List<List<String>> greedyTeamSelection(List<List<String>> teamCandidates) {
        List<List<String>> selectedTeams = new ArrayList<>();
        Set<String> assignedStudents = new HashSet<>();

        for (List<String> team : teamCandidates) {
            if (Collections.disjoint(team, assignedStudents)) {
                selectedTeams.add(new ArrayList<>(team));
                assignedStudents.addAll(team);
            }
        }

        return selectedTeams;
    }

    // 결과 분석 (수정된 버전)
    public void analyzeResult(List<TeamMatchResultDto> allResults) {
        System.out.println("\n=== 전체 팀 배정 결과 분석 ===");

        int totalTeams = 0;
        int totalAssigned = 0;
        Set<String> allAssignedStudents = new HashSet<>();

        for (TeamMatchResultDto result : allResults) {
            totalTeams += result.getTeamMatchDtoList().size();

            for (TeamMatchDto team : result.getTeamMatchDtoList()) {
                for (Integer memberId : team.getMemberIds()) {
                    allAssignedStudents.add(memberId.toString());
                }
            }

            System.out.println("\n과목 " + result.getSubjectId() + ":");
            System.out.println("  생성된 팀: " + result.getTeamMatchDtoList().size() + "개");
            System.out.println("  실패한 학생: " + result.getFailedMemberIdList().size() + "명");

            // 팀별 상세 정보
            for (int i = 0; i < result.getTeamMatchDtoList().size(); i++) {
                TeamMatchDto team = result.getTeamMatchDtoList().get(i);
                System.out.println("    팀 " + (i+1) + " (시간: " + team.getTimeId() + "): " +
                        team.getMemberIds());
            }
        }

        System.out.println("\n총 생성된 팀: " + totalTeams + "개");
        System.out.println("총 배정된 학생(중복 제거): " + allAssignedStudents.size() + "명");
    }
}