package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class BattleEngine {
    private static BattleEngine instance;
    private Random random = new Random(1L);

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public void reset() {

    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        validateTeams(teamA, teamB);

        List<Combatant> a = new ArrayList<>(teamA);
        List<Combatant> b = new ArrayList<>(teamB);

        EncounterResult result = new EncounterResult();
        result.addLog("=== ENCOUNTER START ===");
        result.addLog("Team A size: " + a.size());
        result.addLog("Team B size: " + b.size());

        int rounds = 0;

        while (hasLiving(a) && hasLiving(b)) {
            rounds++;
            result.addLog("");
            result.addLog("----- Round " + rounds + " -----");

            teamAttacks(a, b, "A", "B", result);
            removeDead(b, "B", result);

            if (!hasLiving(b)) break;


            teamAttacks(b, a, "B", "A", result);
            removeDead(a, "A", result);
        }

        result.setRounds(rounds);

        String winner;
        if (hasLiving(a) && !hasLiving(b)) winner = "TEAM A";
        else if (hasLiving(b) && !hasLiving(a)) winner = "TEAM B";
        else winner = "DRAW";

        result.setWinner(winner);
        result.addLog("");
        result.addLog("=== ENCOUNTER END ===");
        result.addLog("Winner: " + winner);
        result.addLog("Rounds: " + rounds);
        result.addLog("Living A: " + countLiving(a) + " | Living B: " + countLiving(b));

        return result;
    }

    private void validateTeams(List<Combatant> teamA, List<Combatant> teamB) {
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("Teams must not be null");
        }
        if (teamA.isEmpty() || teamB.isEmpty()) {
            throw new IllegalArgumentException("Teams must not be empty");
        }
        for (Combatant c : teamA) {
            if (c == null) throw new IllegalArgumentException("Team A contains null combatant");
        }
        for (Combatant c : teamB) {
            if (c == null) throw new IllegalArgumentException("Team B contains null combatant");
        }
    }

    private void teamAttacks(List<Combatant> attackers,
                             List<Combatant> defenders,
                             String atkName,
                             String defName,
                             EncounterResult result) {

        for (Combatant attacker : attackers) {
            if (attacker == null || !attacker.isAlive()) continue;

            Combatant target = pickTarget(defenders);
            if (target == null) return;

            int damage = attacker.getAttackPower();
            if (damage < 1) damage = 1;

            if (random.nextInt(100) < 15) {
                damage *= 2;
                result.addLog(atkName + " CRIT! (" + attacker.getName() + ")");
            }

            target.takeDamage(damage);
            result.addLog(atkName + ": " + attacker.getName() + " hits " + defName + ": " + target.getName() + " for " + damage);
        }
    }

    private Combatant pickTarget(List<Combatant> defenders) {
        for (Combatant c : defenders) {
            if (c != null && c.isAlive()) return c;
        }
        return null;
    }

    private void removeDead(List<Combatant> team, String teamName, EncounterResult result) {
        Iterator<Combatant> it = team.iterator();
        while (it.hasNext()) {
            Combatant c = it.next();
            if (c == null || !c.isAlive()) {
                String name = (c == null) ? "null" : c.getName();
                result.addLog("DEAD (" + teamName + "): " + name);
                it.remove();
            }
        }
    }

    private boolean hasLiving(List<Combatant> team) {
        for (Combatant c : team) {
            if (c != null && c.isAlive()) return true;
        }
        return false;
    }

    private int countLiving(List<Combatant> team) {
        int count = 0;
        for (Combatant c : team) {
            if (c != null && c.isAlive()) count++;
        }
        return count;
    }
}
