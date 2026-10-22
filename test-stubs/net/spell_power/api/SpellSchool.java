package net.spell_power.api;
public class SpellSchool {
 public final String name;
 SpellSchool(String name) {this.name=name;}
 public Object ownedAttribute() {return "attribute:"+name;}
}
