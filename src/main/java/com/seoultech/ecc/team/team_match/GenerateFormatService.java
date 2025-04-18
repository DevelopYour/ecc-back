package com.seoultech.ecc.team.team_match;

import com.seoultech.ecc.team.datamodel.TimeEntity;
import com.seoultech.ecc.team.repository.ApplyStudyRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Map<학생, List<시간아이디>> -> 시간대별 가능한 3-5명 조합 전체 반환
@Service
public class GenerateFormatService {

    @Autowired
    private ApplyStudyRepository applyStudyRepository;

    private static List<List<Integer>> generateCombinations(List<Integer> students) {
        List<List<Integer>> result = new ArrayList<>();
        for (int size = 3; size <= 5; size++) {
            combineRecursive(students, 0, new ArrayList<>(), size, result);
        }
        return result;
    }

    private static void combineRecursive(List<Integer> input, int start, List<Integer> current, int size, List<List<Integer>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < input.size(); i++) {
            current.add(input.get(i));
            combineRecursive(input, i + 1, current, size, result);
            current.remove(current.size() - 1); // 완성된 조합에서 마지막 요소 제거하기. 다음 조합은 마지막 요소 뒤에꺼로 채울것
        }
    }

    // TODO: 테스트용 임시데이터 사용 중 -> 추후 아래 주석 메소드로 교체
    static Map<Integer, List<Integer>> studentTimeMap = Map.ofEntries(
            Map.entry(1, List.of(10, 11, 15)),            // A
            Map.entry(2, List.of(1, 2)),                  // B
            Map.entry(3, List.of(3, 4, 5, 6, 7, 8, 9)),    // C
            Map.entry(4, List.of(3, 4, 5)),               // D
            Map.entry(5, List.of(2, 6, 8)),               // E
            Map.entry(6, List.of(6, 7, 8, 9)),            // F
            Map.entry(7, List.of(1, 2, 3, 4)),            // G
            Map.entry(8, List.of(1, 2, 3, 4)),            // H
            Map.entry(9, List.of(2, 3, 4, 5, 6)),         // I
            Map.entry(10, List.of(3, 4, 8, 9)),           // J
            Map.entry(11, List.of(7, 9)),                 // K
            Map.entry(12, List.of(1, 6)),                 // L
            Map.entry(13, List.of(2, 3, 4, 5, 6)),        // M
            Map.entry(14, List.of(5, 6, 7, 8)),           // N
            Map.entry(15, List.of(3, 4)),                 // O
            Map.entry(16, List.of(2)),                    // P
            Map.entry(17, List.of(1)),                    // Q
            Map.entry(18, List.of(1, 2, 3)),              // R
            Map.entry(19, List.of(4, 8)),                 // S
            Map.entry(20, List.of(1, 5, 8)),              // T
            Map.entry(21, List.of(2, 4)),                 // U
            Map.entry(22, List.of(6, 8)),                 // V
            Map.entry(23, List.of(3, 9)),                 // W
            Map.entry(24, List.of(1, 3, 7, 8, 9)),        // X
            Map.entry(25, List.of(13, 14)),               // ㄱ
            Map.entry(26, List.of(2, 14)),                // ㄴ
            Map.entry(27, List.of(7, 15)),                // ㄷ
            Map.entry(28, List.of(5)),                    // ㄹ
            Map.entry(29, List.of(5))                     // ㅁ
    );

    public List<TeamCandidateDto> init() {
        Map<Integer, List<Integer>> timeToStudentsMap = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : studentTimeMap.entrySet()) { // entrySet(): Map의 전체 (key, value) 쌍 꺼내는 메소드
            Integer student = entry.getKey();
            for (int timeId : entry.getValue()) { // 학생이 신청한 모든 시간대 하나씩
                // computeIfAbsent: key(timeId)가 있으면 -> 해당 value(해당 timeId 신청자 리스트) / 없으면 -> 빈 value(여기서는 빈 리스트) 생성해서 추가 후 반환
                // add: computeIfAbsent로 얻은 리스트에 student 추가
                timeToStudentsMap.computeIfAbsent(timeId, k -> new ArrayList<>()).add(student);
            }
        }

        // 각 시간아이디별로 신청자 리스트에서 3~5명 가능조합 모두 만들어서 teamCandidates에 추가
        List<TeamCandidateDto> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> timeToStudents : timeToStudentsMap.entrySet()) {// 조합 생성
            int timeId = timeToStudents.getKey();
            List<List<Integer>> combinations = generateCombinations(timeToStudents.getValue());
            for(List<Integer> combination : combinations) {
                result.add(new TeamCandidateDto(timeId, combination));
            }
        }

        return result;
    }

//    public List<List<Integer>> init() {
//        Map<Integer, List<Integer>> timeToStudents = new HashMap<>();
//        List<TimeEntity> selectedTimes = applyStudyRepository.findAllTimeEntities();
//        for(TimeEntity timeEntity : selectedTimes) {
//            Integer timeId = timeEntity.getTimeId();
//            timeToStudents.put(timeId, applyStudyRepository.findMemberIdsByTimeId(timeId));
//        }
//        // 각 시간아이디별로 신청자 리스트에서 3~5명 가능조합 모두 만들어서 teamCandidates에 추가
//        List<List<Integer>> teamCandidates = new ArrayList<>();
//        for (Map.Entry<Integer, List<Integer>> entry : timeToStudents.entrySet()) {
//            List<Integer> students = entry.getValue();
//
//            // 조합 생성
//            List<List<Integer>> combinations = generateCombinations(students);
//            teamCandidates.addAll(combinations);
//        }
//
//        return teamCandidates;
//    }

}
