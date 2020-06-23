package me.paper.skills.skill;

public enum SkillType {

    BLOODTHIRSTY("bloodthirsty", true),
    INFUSED("infused", true),
    PROTECTIVE("protective", true),
    HEALTHY("healthy", true),
    ROBUST("robust", true),
    HARMONY("harmony", true),
    BLEED("bleed", true),
    SHATTERED("shattered", true),
    FISHERMAN("fisherman", false),
    MINER("miner", false),
    FARMER("farmer", false),
    GRINDER("grinder", false);

    private String identifier;
    private boolean combatSkill;

    SkillType(String identifier, boolean combatSkill) {
        this.identifier = identifier;
        this.combatSkill = combatSkill;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public boolean isCombatSkill() {
        return this.combatSkill;
    }

}
