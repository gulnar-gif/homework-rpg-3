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
        this.random = new Random(1L);
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("Команды не могут быть null");
        }
        if (teamA.isEmpty() || teamB.isEmpty()) {
            throw new IllegalArgumentException("Команды не могут быть пустыми");
        }


        List<Combatant> a = new ArrayList<>(teamA);
        List<Combatant> b = new ArrayList<>(teamB);

        EncounterResult result = new EncounterResult();
        int rounds = 0;
        int maxRounds = 100;

        result.addLog("Битва началась!");
        result.addLog("Команда A: " + a.size() + " бойцов");
        result.addLog("Команда B: " + b.size() + " бойцов");
        result.addLog("");

        while (!a.isEmpty() && !b.isEmpty() && rounds < maxRounds) {
            rounds++;
            result.addLog("=== Раунд " + rounds + " ===");


            performAttacks(a, b, "A", "B", result);
            if (b.isEmpty()) break;


            performAttacks(b, a, "B", "A", result);

            result.addLog("");
        }

        result.setRounds(rounds);

        if (a.isEmpty() && b.isEmpty()) {
            result.setWinner("Ничья");
            result.addLog("Обе команды уничтожены. Ничья!");
        } else if (b.isEmpty()) {
            result.setWinner("Команда A");
            result.addLog("Команда A одержала победу!");
        } else if (a.isEmpty()) {
            result.setWinner("Команда B");
            result.addLog("Команда B победила");
        } else {
            result.setWinner("Ничья");
            result.addLog("Достигнуто максимальное количество раундов. Ничья!");
        }

        return result;
    }

    private void performAttacks(
            List<Combatant> attackers,
            List<Combatant> defenders,
            String attackerLabel,
            String defenderLabel,
            EncounterResult result
    ) {
        int i = 0;
        while (i < attackers.size() && !defenders.isEmpty()) {
            Combatant attacker = attackers.get(i);

            if (!attacker.isAlive()) {
                attackers.remove(i);
                continue;
            }

            int targetIndex = random.nextInt(defenders.size());
            Combatant target = defenders.get(targetIndex);

            int damage = attacker.getAttackPower();
            if (damage < 0) damage = 0;

            target.takeDamage(damage);

            result.addLog(attackerLabel + " " + attacker.getName()
                    + " атакует " + defenderLabel + " " + target.getName()
                    + " и наносит " + damage+ " урона");

            if (!target.isAlive()) {
                result.addLog(defenderLabel + " " + target.getName() + " погиб!");
                defenders.remove(targetIndex);
            }

            i++;
        }
    }
}
