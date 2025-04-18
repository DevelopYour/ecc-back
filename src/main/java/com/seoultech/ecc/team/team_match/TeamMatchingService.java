package com.seoultech.ecc.team.team_match;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.sat.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class TeamMatchingService {

    @Autowired
    GenerateFormatService initService;

    // 방법A: ILP(정수선형계획): MPSolver 엔진
    public void teamMatch() {
        Loader.loadNativeLibraries(); // native 라이브러리 로드

        // 1) 데이터 준비
        List<TeamCandidateDto> teamCandidates = initService.init();

        // 2) 전체 학생 ID 추출. TODO: 추후 DB에서 추출하도록 수정하기
        Set<Integer> allPeople = new HashSet<>();
        for(TeamCandidateDto teamCandidateDto: teamCandidates){
            allPeople.addAll(teamCandidateDto.getMemberIds());
        }

        // 3) OR-Tools의 정수계획법(ILP) 기반 SCIP 최적화 solver 초기화
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Solver not available");
            return;
        }

        // 4) 이진 변수 정의 (팀의 선발 여부)
        Map<Integer, MPVariable> teamVariables = new HashMap<>();
        for (int i = 0; i < teamCandidates.size(); i++) {
            teamVariables.put(i, solver.makeIntVar(0, 1, "team_" + i)); // teamVariables.get(i) = team_i의 선택여부 값
        }

        // 5) 제약 조건: 각 학생이 포함된 팀들의 선택 변수 합 <= 1 (한 명이 여러 팀에 들어가는 상황 방지)
        for (Integer person : allPeople) {
            MPConstraint constraint = solver.makeConstraint(0, 1); // makeConstraint(0,1) = 좌변 합이 0 이상 1 이하. 즉 최대 1개 선택 가능
            for (int i = 0; i < teamCandidates.size(); i++) {
                if (teamCandidates.get(i).getMemberIds().contains(person)) {
                    constraint.setCoefficient(teamVariables.get(i), 1); // team_i * 1 을 곱해서 제약식에 넣음
                }
            }
        }

        // 6) 목적 함수 정의: 전체 포함된 인원 수 최대화
        MPObjective objective = solver.objective();
        for (int i = 0; i < teamCandidates.size(); i++) {
            objective.setCoefficient(teamVariables.get(i), teamCandidates.get(i).getMemberIds().size());
        }
        objective.setMaximization();

        // 7) 최적화 실행 및 결과 출력
        MPSolver.ResultStatus result = solver.solve();
        if (result == MPSolver.ResultStatus.OPTIMAL || result == MPSolver.ResultStatus.FEASIBLE) {
            for (int i=0; i<teamCandidates.size(); i++) {
                if (teamVariables.get(i).solutionValue() == 1) {
                    TeamCandidateDto team = teamCandidates.get(i);
                    System.out.println("시간 " + team.getTimeId() + " 팀: " + team.getMemberIds());
                }
            }
        } else {
            System.out.println("해결 불가능하거나 오류 발생");
        }
    }

    // 방법B: 다중해
    public void teamMatch1() {
        Loader.loadNativeLibraries(); // native 라이브러리 로드

        // 1) 데이터 준비
        List<TeamCandidateDto> teamCandidates = initService.init();

        CpModel model = new CpModel();

        List<BoolVar> teamVars = new ArrayList<>();
        for(int i = 0; i < teamCandidates.size(); i++){
            teamVars.add(model.newBoolVar("team_" + i));
        }

        // 2) 전체 학생 ID 추출. TODO: 추후 DB에서 추출하도록 수정하기
        Set<Integer> allPeople = new HashSet<>();
        for(TeamCandidateDto teamCandidateDto: teamCandidates){
            allPeople.addAll(teamCandidateDto.getMemberIds());
        }

        for(Integer person: allPeople){
            List<BoolVar> involvedTeams = new ArrayList<>();
            for(int i = 0; i < teamCandidates.size(); i++){
                if(teamCandidates.get(i).getMemberIds().contains(person)){
                    involvedTeams.add(teamVars.get(i));
                }
            }
            model.addAtMostOne(involvedTeams.toArray(new BoolVar[0]));
        }

        LinearExpr objective = LinearExpr.sum(
                IntStream.range(0, teamCandidates.size())
                        .mapToObj(i -> LinearExpr.term(teamVars.get(i), teamCandidates.get(i).getMemberIds().size()))
                        .toArray(LinearExpr[]::new)
        );
        model.maximize(objective);

        CpSolver solver = new CpSolver();

        class TeamSolutionPrinter extends CpSolverSolutionCallback {
            int count = 0;

            @Override
            public void onSolutionCallback(){
                System.out.println("===Solution" + (++count) + "===");
                for(int i = 0; i < teamCandidates.size(); i++){
                    if(booleanValue(teamVars.get(i))){
                        System.out.println("시간" + teamCandidates.get(i).getTimeId() + "팀" + teamCandidates.get(i).getMemberIds());
                    }
                }
                System.out.println();
            }
        }

        TeamSolutionPrinter teamSolutionPrinter = new TeamSolutionPrinter();
        solver.searchAllSolutions(model, teamSolutionPrinter);
        System.out.println("총 " + teamSolutionPrinter.count);

    }


}
