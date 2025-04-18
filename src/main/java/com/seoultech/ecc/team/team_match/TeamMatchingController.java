package com.seoultech.ecc.team.team_match;

import com.seoultech.ecc.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class TeamMatchingController {

    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamMatchingService teamMatchingService;

    @GetMapping("/team")
    @Operation(summary = "team matching - 1 answer", description = "팀 매칭 - 단일해 (정수선형계획법)")
    public String teamMatch() {
        teamMatchingService.teamMatch();
        return "success";
    }

    @GetMapping("/teams")
    @Operation(summary = "team matching - 1 answer", description = "팀 매칭 - 다중해")
    public String teamMatch1() {
        teamMatchingService.teamMatch1();
        return "success";
    }
}
