package com.seoultech.ecc.controller;

import com.seoultech.ecc.service.TeamService;
import com.seoultech.ecc.service.team_match.TeamMatchingService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class TeamMatchingController {

    static Map<String, List<Integer>> studentTimeMap = Map.ofEntries(
            Map.entry("A", List.of(10, 11, 15)),
            Map.entry("B", List.of(1, 2)),
            Map.entry("C", List.of(3, 4, 5, 6, 7, 8, 9)),
            Map.entry("D", List.of(3, 4, 5)),
            Map.entry("E", List.of(2, 6, 8)),
            Map.entry("F", List.of(6, 7, 8, 9)),
            Map.entry("G", List.of(1, 2, 3, 4)),
            Map.entry("H", List.of(1, 2, 3, 4)),
            Map.entry("I", List.of(2, 3, 4, 5, 6)),
            Map.entry("J", List.of(3, 4, 8, 9)),
            Map.entry("K", List.of(7, 9)),
            Map.entry("L", List.of(1, 6)),
            Map.entry("M", List.of(2, 3, 4, 5, 6)),
            Map.entry("N", List.of(5, 6, 7, 8)),
            Map.entry("O", List.of(3, 4)),
            Map.entry("P", List.of(2)),
            Map.entry("Q", List.of(1)),
            Map.entry("R", List.of(1, 2, 3)),
            Map.entry("S", List.of(4, 8)),
            Map.entry("T", List.of(1, 5, 8)),
            Map.entry("U", List.of(2, 4)),
            Map.entry("V", List.of(6, 8)),
            Map.entry("W", List.of(3, 9)),
            Map.entry("X", List.of(1, 3, 7, 8, 9)),
            Map.entry("ㄱ", List.of(13, 14)),
            Map.entry("ㄴ", List.of(2, 14)),
            Map.entry("ㄷ", List.of(7, 15)),
            Map.entry("ㄹ", List.of(5)),
            Map.entry("ㅁ", List.of(5))
    );

    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamMatchingService teamMatchingService;

    @GetMapping("/team")
    @Operation(summary = "team matching - 1 answer", description = "팀 매칭 - 단일해 (정수선형계획법)")
    public String teamMatch() {
        teamMatchingService.teamMatch(studentTimeMap);
        return "팀 매칭 완료!";
    }

    @GetMapping("/teams")
    @Operation(summary = "team matching - 1 answer", description = "팀 매칭 - 단일해 (정수선형계획법)")
    public String teamMatch2() {
        teamMatchingService.teamMatch(studentTimeMap);
        return "팀 매칭 완료!";
    }
}
