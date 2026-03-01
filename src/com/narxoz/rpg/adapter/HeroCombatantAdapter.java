package com.narxoz.rpg.adapter;

import com.narxoz.rpg.battle.Combatant;
import com.narxoz.rpg.character.Character;
import com.narxoz.rpg.equipment.Armor;
import com.narxoz.rpg.equipment.Weapon;

public class HeroCombatantAdapter implements Combatant {
    private final Character hero;
    private int currentHealth;

    public HeroCombatantAdapter(Character hero) {
        this.hero = hero;
        this.currentHealth = hero.getHealth();
    }

    @Override
    public String getName() {
        return hero.getName();
    }

    @Override
    public int getAttackPower() {
        int power = hero.getStrength();

        Weapon w = hero.getWeapon();
        if (w != null) power += w.getDamage();
        power += hero.getIntelligence() / 4;

        return Math.max(1, power);
    }

    @Override
    public void takeDamage(int amount) {
        int dmg = Math.max(0, amount);

        Armor a = hero.getArmor();
        if (a != null) dmg = Math.max(0, dmg - a.getDefense());

        currentHealth -= dmg;
        if (currentHealth < 0) currentHealth = 0;
    }

    @Override
    public boolean isAlive() {
        return currentHealth > 0;
    }


    public int getCurrentHealth() {
        return currentHealth;
    }
}
