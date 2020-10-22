/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseballElimination {
    private final List<String> teams;
    private final int[] wins;
    private final int[] loses;
    private final int[] remaining;
    private final int[][] games;

    private int networkCapacity;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        if (in.isEmpty()) throw new IllegalArgumentException("in is empty");

        int teamsCount = in.readInt();

        teams = new ArrayList<String>();
        wins = new int[teamsCount];
        loses = new int[teamsCount];
        remaining = new int[teamsCount];
        games = new int[teamsCount][teamsCount];

        int teamIndex = 0;
        while (!in.isEmpty() && teamIndex < teamsCount) {
            teams.add(in.readString());
            wins[teamIndex] = in.readInt();
            loses[teamIndex] = in.readInt();
            remaining[teamIndex] = in.readInt();

            for (int i = 0; i < teamsCount; i++) {
                int gamesLeft = in.readInt();
                if (i == teamIndex) continue;
                games[teamIndex][i] = gamesLeft;
            }

            teamIndex += 1;
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return new ArrayList<>(teams);
    }

    // number of wins for given team
    public int wins(String team) {
        verifyTeam(team);

        int teamIndex = teams.indexOf(team);
        return teamIndex != -1 ? wins[teamIndex] : 0;
    }

    // number of losses for given team
    public int losses(String team) {
        verifyTeam(team);

        int teamIndex = teams.indexOf(team);
        return teamIndex != -1 ? loses[teamIndex] : 0;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        verifyTeam(team);

        int teamIndex = teams.indexOf(team);
        return teamIndex != -1 ? remaining[teamIndex] : 0;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        verifyTeam(team1);
        verifyTeam(team2);

        int firstTeamIndex = teams.indexOf(team1);
        int secondTeamIndex = teams.indexOf(team2);
        if (firstTeamIndex == -1 || secondTeamIndex == -1) return 0;
        return games[firstTeamIndex][secondTeamIndex];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        verifyTeam(team);

        if (checkTrivialElimination(team) != null) return true;
        return checkNonTrivialElimination(team);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        verifyTeam(team);

        return createCertificateOfElimination(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    private String checkTrivialElimination(String team) {
        int bestPossibleResult = wins(team) + remaining(team);
        for (String otherTeam : teams) {
            if (otherTeam.equals(team)) continue;
            int otherTeamWins = wins(otherTeam);
            if (bestPossibleResult < otherTeamWins) return otherTeam;
        }

        return null;
    }

    private boolean checkNonTrivialElimination(String team) {
        FlowNetwork flowNetwork = createFlowNetworkFor(team);
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, flowNetwork.V() - 1);
        int maxFlow = (int) fordFulkerson.value();

        return maxFlow < networkCapacity;
    }

    private FlowNetwork createFlowNetworkFor(String team) {
        int bestPossibleResult = wins(team) + remaining(team);
        int teamCount = teams.size();
        int gameNodesCount = calculateGameNodesCountFor(teamCount);

        int totalNodesCount = gameNodesCount + teamCount - 1 + 2;
        FlowNetwork flowNetwork = new FlowNetwork(totalNodesCount);
        networkCapacity = 0;
        // Set edges from t to game nodes
        int gameNodeIndex = 1;
        int teamIndex = teams.indexOf(team);
        int sinkNodeIndex = totalNodesCount - 1;
        for (int i = 0; i < teamCount; i++) {
            if (i == teamIndex) continue;
            int iTeamNode = gameNodesCount + 1 + (i < teamIndex ? i : i - 1);

            for (int j = i + 1; j < teamCount; j++) {
                if (j == teamIndex) continue;
                int capacity = games[i][j];

                FlowEdge flowEdge = new FlowEdge(0, gameNodeIndex, capacity);
                flowNetwork.addEdge(flowEdge);
                networkCapacity += capacity;

                int jTeamNode = gameNodesCount + 1 + (j < teamIndex ? j : j - 1);
                FlowEdge flowEdgeI = new FlowEdge(gameNodeIndex, iTeamNode,
                                                  Double.POSITIVE_INFINITY);
                FlowEdge flowEdgeJ = new FlowEdge(gameNodeIndex, jTeamNode,
                                                  Double.POSITIVE_INFINITY);
                flowNetwork.addEdge(flowEdgeI);
                flowNetwork.addEdge(flowEdgeJ);

                gameNodeIndex += 1;
            }

            int capacity = bestPossibleResult - wins[i];
            FlowEdge flowEdge = new FlowEdge(iTeamNode, sinkNodeIndex, capacity > 0 ? capacity : 0);
            flowNetwork.addEdge(flowEdge);
        }

        return flowNetwork;
    }

    private Iterable<String> createCertificateOfElimination(String team) {
        String trivialEliminator = checkTrivialElimination(team);
        if (trivialEliminator != null) {
            List<String> certificate = new ArrayList<>();
            certificate.add(trivialEliminator);
            return certificate;
        }

        FlowNetwork flowNetwork = createFlowNetworkFor(team);
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, flowNetwork.V() - 1);

        int teamCount = teams.size();
        int gameNodesCount = calculateGameNodesCountFor(teamCount);

        List<String> eliminatingTeams = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            boolean inCut = fordFulkerson.inCut(gameNodesCount + 1 + i);
            if (inCut) eliminatingTeams.add(getTeamAtIndex(i, team));
        }

        return eliminatingTeams.size() > 0 ? eliminatingTeams : null;
    }

    private int calculateGameNodesCountFor(int teamCount) {
        int gameNodesCount = 0;
        for (int i = 0; i < teamCount - 2; i++) {
            for (int j = i + 1; j < teamCount - 1; j++) {
                gameNodesCount += 1;
            }
        }

        return gameNodesCount;
    }

    private String getTeamAtIndex(int index, String excludingTeam) {
        List<String> excTeams = new ArrayList<String>(teams);
        excTeams.remove(excludingTeam);
        return excTeams.get(index);
    }

    // Verification

    private void verifyTeam(String team) {
        if (team == null) throw new IllegalArgumentException("Team can't be null");

        ArrayList<String> allTeams = new ArrayList<>();
        Iterator<String> iterator = teams().iterator();
        iterator.forEachRemaining(allTeams::add);
        if (!allTeams.contains(team)) throw new IllegalArgumentException("Unknown team");
    }
}
