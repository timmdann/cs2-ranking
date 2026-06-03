package pl.example.cs2.match.service;

import pl.example.cs2.match.dto.CreateMatchRequest;
import pl.example.cs2.match.dto.MatchResponse;

import java.util.List;

public interface MatchService {
    MatchResponse createMatch(CreateMatchRequest request);
    MatchResponse getMatch(Long id);
    List<MatchResponse> getAllMatches();
}
