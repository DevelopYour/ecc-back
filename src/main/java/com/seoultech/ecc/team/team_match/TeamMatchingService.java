package com.seoultech.ecc.team.team_match;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.seoultech.ecc.team.datamodel.ApplyRegularStudyEntity;
import com.seoultech.ecc.team.dto.ApplyRegularStudyDto;
import com.seoultech.ecc.team.dto.TeamMatchDto;
import com.seoultech.ecc.team.dto.TeamMatchResultDto;
import com.seoultech.ecc.team.repository.ApplyRegularStudyRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamMatchingService {

    @Autowired
    ApplyRegularStudyRepository applyRegularStudyRepository;

    @Autowired
    GenerateFormatService initService;

    public List<ApplyRegularStudyDto> showApply(){
        return applyRegularStudyRepository.findAll().stream().map(ApplyRegularStudyDto::fromEntity).collect(Collectors.toList());
    }

    // 방법A: ILP(정수선형계획): MPSolver 엔진
    public void tempTeamMatch(Map<String, List<Integer>> studentTimeMap) {
        Loader.loadNativeLibraries(); // native 라이브러리 로드

        List<List<String>> teamCandidates = initService.init(studentTimeMap);
        Set<String> allPeople = new HashSet<>(); // 전체 인원 추출. Set는 중복을 자동으로 제거함!
        for (List<String> team : teamCandidates) {
            allPeople.addAll(team);
        }

        MPSolver solver = MPSolver.createSolver("SCIP"); // SCIP: OR-Tools에서 사용 가능한 대표 solver
        if (solver == null) System.out.println("Solver not available");

        // 이진 변수 생성 (팀 선택 여부)
        Map<Integer, MPVariable> teamVariables = new HashMap<>();
        for (int i = 0; i < teamCandidates.size(); i++) {
            teamVariables.put(i, solver.makeIntVar(0, 1, "team_" + i)); // teamVariables.get(i) = team_i의 선택여부 값
        }

        // 제약 조건: 한 사람은 하나의 팀에만 속하도록
        // 모든 사람에 대해 constraint 생성.
        // 그 사람이 속한 팀 후보를 찾아서 아이디
        for (String person : allPeople) {
            MPConstraint constraint = solver.makeConstraint(0, 1); // makeConstraint(0,1) = 좌변 합이 0 이상 1 이하. 즉 최대 1개 선택 가능
            for (int i = 0; i < teamCandidates.size(); i++) {
                if (teamCandidates.get(i).contains(person)) {
                    constraint.setCoefficient(teamVariables.get(i), 1); // team_i * 1 을 곱해서 제약식에 넣음
                }
            }
        }

        // 목적 함수: 전체 포함된 인원 수 최대화
        MPObjective objective = solver.objective();
        for (int i = 0; i < teamCandidates.size(); i++) {
            int teamSize = teamCandidates.get(i).size();
            objective.setCoefficient(teamVariables.get(i), teamSize);
        }
        objective.setMaximization();

        // 최적화 실행 및 결과 출력
        MPSolver.ResultStatus result = solver.solve();

        if (result == MPSolver.ResultStatus.OPTIMAL || result == MPSolver.ResultStatus.FEASIBLE) {
            for (int i = 0; i < teamCandidates.size(); i++) {
                if (teamVariables.get(i).solutionValue() == 1) {
                    System.out.println("팀" + i + ": " + teamCandidates.get(i));
                }
            }
        } else {
            System.out.println("해결 불가능하거나 오류 발생");
        }
    }

    @Autowired
    MaximizeAssignmentTeamMatcher teamMatcher;

    public List<TeamMatchResultDto> teamMatch() {
        List<TeamMatchResultDto> result = new ArrayList<>();

        // 과목 1
        List<ApplyRegularStudyDto> applies1 = applyRegularStudyRepository
                .findAllBySubject_SubjectId(1L)
                .stream()
                .map(ApplyRegularStudyDto::fromEntity)
                .toList();
        TeamMatchResultDto result1 = teamMatcher.optimizeTeamMatching(applies1, result);
        result.add(result1);

        // 과목 2 - 이전 결과(result)를 전달하여 필터링
        List<ApplyRegularStudyDto> applies2 = applyRegularStudyRepository
                .findAllBySubject_SubjectId(2L)
                .stream()
                .map(ApplyRegularStudyDto::fromEntity)
                .toList();
        TeamMatchResultDto result2 = teamMatcher.optimizeTeamMatching(applies2, result);
        result.add(result2);

        // 과목 3 - 이전 결과들(result)을 전달하여 필터링
        List<ApplyRegularStudyDto> applies3 = applyRegularStudyRepository
                .findAllBySubject_SubjectId(3L)
                .stream()
                .map(ApplyRegularStudyDto::fromEntity)
                .toList();
        TeamMatchResultDto result3 = teamMatcher.optimizeTeamMatching(applies3, result);
        result.add(result3);

        // 과목 4 - 이전 결과들(result)을 전달하여 필터링
        List<ApplyRegularStudyDto> applies4 = applyRegularStudyRepository
                .findAllBySubject_SubjectId(4L)
                .stream()
                .map(ApplyRegularStudyDto::fromEntity)
                .toList();
        TeamMatchResultDto result4 = teamMatcher.optimizeTeamMatching(applies4, result);
        result.add(result4);

        // 과목 5 - 이전 결과들(result)을 전달하여 필터링
        List<ApplyRegularStudyDto> applies5 = applyRegularStudyRepository
                .findAllBySubject_SubjectId(5L)
                .stream()
                .map(ApplyRegularStudyDto::fromEntity)
                .toList();
        TeamMatchResultDto result5 = teamMatcher.optimizeTeamMatching(applies5, result);
        result.add(result5);

        // 전체 결과 요약 출력
        System.out.println("\n=== 전체 팀매칭 완료 ===");
        int totalTeams = result.stream().mapToInt(r -> r.getTeamMatchDtoList().size()).sum();
        int totalFailed = result.stream().mapToInt(r -> r.getFailedMemberIdList().size()).sum();
        System.out.println("총 생성된 팀: " + totalTeams + "개");
        System.out.println("총 실패한 학생: " + totalFailed + "명");

        // 과목별 결과 요약
        for (TeamMatchResultDto teamResult : result) {
            System.out.println("과목 " + teamResult.getSubjectId() +
                    ": " + teamResult.getTeamMatchDtoList().size() + "개 팀, " +
                    teamResult.getFailedMemberIdList().size() + "명 실패");
        }

        return result;
    }


}
