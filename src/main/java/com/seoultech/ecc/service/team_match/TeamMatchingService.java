package com.seoultech.ecc.service.team_match;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamMatchingService {

    @Autowired
    GenerateFormatService initService;

    // 방법A: ILP(정수선형계획): MPSolver 엔진
    public void teamMatch(Map<String, List<Integer>> studentTimeMap) {
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

    
}
