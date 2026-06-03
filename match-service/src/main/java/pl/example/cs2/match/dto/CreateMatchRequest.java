package pl.example.cs2.match.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateMatchRequest {

    @NotNull
    @Size(min = 1, max = 5)
    private List<Long> winnerTeamPlayerIds;

    @NotNull
    @Size(min = 1, max = 5)
    private List<Long> loserTeamPlayerIds;

    public CreateMatchRequest() {}

    public List<Long> getWinnerTeamPlayerIds() { return winnerTeamPlayerIds; }
    public void setWinnerTeamPlayerIds(List<Long> winnerTeamPlayerIds) { this.winnerTeamPlayerIds = winnerTeamPlayerIds; }
    public List<Long> getLoserTeamPlayerIds() { return loserTeamPlayerIds; }
    public void setLoserTeamPlayerIds(List<Long> loserTeamPlayerIds) { this.loserTeamPlayerIds = loserTeamPlayerIds; }
}
