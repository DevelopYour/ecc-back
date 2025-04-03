package com.seoultech.ecc.service.team_match;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenerateFormatService {

    private static List<List<String>> generateCombinations(List<String> students, int minSize, int maxSize) {
        List<List<String>> result = new ArrayList<>();
        for (int size = minSize; size <= maxSize; size++) {
            combineRecursive(students, 0, new ArrayList<>(), size, result);
        }
        return result;
    }

    private static void combineRecursive(List<String> input, int start, List<String> current, int size, List<List<String>> result) {
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

    public List<List<String>> init(Map<String, List<Integer>> studentTimeMap) {
        Map<Integer, List<String>> timeToStudents = new HashMap<>();
        List<List<String>> teamCandidates = new ArrayList<>();

        // 샘플데이터에서 원하는 형식으로 변환
        for (Map.Entry<String, List<Integer>> entry : studentTimeMap.entrySet()) { // entrySet(): Map의 전체 (key, value) 쌍 꺼내는 메소드
            String student = entry.getKey();
            for (int timeId : entry.getValue()) { // 학생이 신청한 모든 시간대 하나씩
                // computeIfAbsent: key(timeId)가 있으면 -> 해당 value(해당 timeId 신청자 리스트) / 없으면 -> 빈 value(여기서는 빈 리스트) 생성해서 추가 후 반환
                // add: computeIfAbsent로 얻은 리스트에 student 추가
                timeToStudents.computeIfAbsent(timeId, k -> new ArrayList<>()).add(student);
            }
        }

        System.out.println("timeToStudents: " + timeToStudents.size());

        // 각 시간아이디별로 신청자 리스트에서 3~5명 가능조합 모두 만들어서 teamCandidates에 추가
        for (Map.Entry<Integer, List<String>> entry : timeToStudents.entrySet()) {
            List<String> students = entry.getValue();

            // 조합 생성
            List<List<String>> combinations = generateCombinations(students, 3, 5);
            teamCandidates.addAll(combinations);
        }

        return teamCandidates;
    }

}
