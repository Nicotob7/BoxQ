package cl.tobar.boxq.model;

public class Box {
    String id;
    String name, repe, weight, mod;
    public Box(){}

    public Box(String id, String name, String weight, String repe, String mod){
        this.id = id;
        this.name = name;
        this.repe = repe;
        this.weight = weight;
        this.mod = mod;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepe() {
        return repe;
    }

    public void setRepe(String repe) {
        this.repe = repe;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMod(){
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }
}
