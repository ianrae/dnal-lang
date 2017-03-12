package org.dnal.other;

public class StringTrail {
    private String trail = "";
    
    public void add(String s) {
        if (trail.isEmpty()) {
            trail = s;
        } else {
            trail += ";" + s;
        }
    }
    
    public String getTrail() {
        return trail;
    }

}
