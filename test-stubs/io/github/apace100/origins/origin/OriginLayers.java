package io.github.apace100.origins.origin;
import net.minecraft.class_2960;
import java.util.*;
public class OriginLayers {
 private static final Map<class_2960,OriginLayer> layers=new HashMap<>();
 public static OriginLayer getLayer(class_2960 id){
  if(!layers.containsKey(id)) throw new IllegalArgumentException("Could not get layer from id '"+id+"', as it doesn't exist!");
  return layers.get(id);
 }
 public static int size(){return layers.size();}
 public static void clear(){layers.clear();}
 public static void add(OriginLayer layer){layers.put(layer.getIdentifier(),layer);}
}
