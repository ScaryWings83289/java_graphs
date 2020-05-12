public class Edge<T> {
    private final T toNode;
    private Integer weight;
    Edge(T toNode, Integer weight){
        this.toNode = toNode;
        this.weight = weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }
    public int getWeight(){
        return weight;
    }
    public String getStringWeight() { return Integer.toString(weight);}
    public T getToNode(){
        return toNode;
    }
}
