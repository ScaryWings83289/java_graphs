import java.lang.reflect.Array;
import java.util.*;

public class Graph<T> {
    private int node_num;
    private final Map<T, ArrayList<Edge<T>>> nodes = new LinkedHashMap<>();
    Graph(int node_num){
        this.node_num = node_num;
    }

    public int changeCapacity(int new_node_num){
        if(new_node_num < nodes.size()) return -1;
        node_num = new_node_num;
        return node_num;
    }

    public Map<T, ArrayList<Edge<T>>> getNodes(){
        return nodes;
    }

    public int getNode_num() {
        return node_num;
    }

    public boolean addNode(T newNode){
        MaxNodesReachedException noMoreNodes = new MaxNodesReachedException("Max Possible: "+node_num+"\nTried to add extra");
        if(nodes.containsKey(newNode))
            return false;
        if(nodes.size() >= node_num)
            throw noMoreNodes;

        nodes.put(newNode, new ArrayList<>());
        return true;
    }

    public boolean addEdgeUndirected(T fromNode, Edge<T> newEdge){
        if(!nodes.containsKey(fromNode))
            return false;
        if(!nodes.containsKey(newEdge.getToNode()))
            return false;

        ArrayList<Edge<T>> edge = nodes.get(fromNode);
        edge.add(newEdge); // adding edge from fromNode to toNode

        Edge<T> newEdge1 = new Edge<>(fromNode, newEdge.getWeight());
        edge = nodes.get(newEdge.getToNode());
        edge.add(newEdge1); // adding edge from toNode to fromNode, undirected graph
        return true;
    }

    public boolean addEdgeDirected(T fromNode, Edge<T> newEdge){
        if(!nodes.containsKey(fromNode))
            return false;
        if(!nodes.containsKey(newEdge.getToNode()))
            return false;

        ArrayList<Edge<T>> edge = nodes.get(fromNode);
        edge.add(newEdge); // adding edge from fromNode to toNode
        return true;
    }

    public ArrayList<T> BFS(T startNode){   // Breadth First Search Traversal
        return traversal(startNode, 1);
    }

    public ArrayList<T> DFS(T startNode){   // Depth First Search Traversal
        return traversal(startNode, 2);
    }

    private ArrayList<T> traversal(T startNode, int mode) {
        NoSuchElementException noNodeException = new NoSuchElementException("Node not found");
        if(!nodes.containsKey(startNode)) {
            throw noNodeException;
        }
        ArrayList<T> out = new ArrayList<>();
        int index = -1;
        Deque<T> queue1 = new ArrayDeque<>();
        queue1.offerLast(startNode);
        while(queue1.size() > 0){
            T temp = (mode == 1)? queue1.pollFirst(): queue1.pollLast();
            if(temp == null)
                break;
            out.add(temp);
            index++;

            ArrayList<Edge<T>> listOfConnections = nodes.get(out.get(index));
            for(Edge<T> edge: listOfConnections){
                if(out.contains(edge.getToNode()) || queue1.contains(edge.getToNode()))
                    continue;
                queue1.offerLast(edge.getToNode());
            }
            //System.out.println(out + "\t" + queue1);
        }
        return out;
    }

    public Graph<T> Prim(T startNode, boolean mode){ //mode == true -> Return directed graph else return undirected graph
        NoSuchElementException noNodeException = new NoSuchElementException("Node not found");
        if(!nodes.containsKey(startNode)) {
            throw noNodeException;
        }

        Graph<T> out = new Graph<>(node_num);
        for (T key: nodes.keySet())
            out.addNode(key);
        ArrayList<T> lightestEdge = new ArrayList<>(2);
        Set<T> nodesSet = new HashSet<>(nodes.keySet());
        Set<T> traversedSet = new HashSet<>();
        int min;

        nodesSet.remove(startNode);
        traversedSet.add(startNode);

        while(nodesSet.size() > 0){
            min = Integer.MAX_VALUE;
            for(T elem: traversedSet) {
                ArrayList<Edge<T>> edgeList = nodes.get(elem);
                for(Edge<T> edge: edgeList){
                    if(traversedSet.contains(edge.getToNode()))
                        continue;
                    if(edge.getWeight() < min){
                        min = edge.getWeight();
                        lightestEdge.clear();
                        lightestEdge.add(elem);
                        lightestEdge.add(edge.getToNode());
                    }
                }
            }

            String temp = "" + lightestEdge.get(0) + "->" + lightestEdge.get(1);
            Edge<T> edge = new Edge<>(lightestEdge.get(1), min);

            if(mode)
                out.addEdgeDirected(lightestEdge.get(0), edge);
            else
                out.addEdgeUndirected(lightestEdge.get(0), edge);

            traversedSet.add(lightestEdge.get(1));
            nodesSet.remove(lightestEdge.get(1));
        }
        return out;
    }

    public Map<T,Map<T,String>> adjacencyMatrix(){
        Map<T,Map<T,String>> mat = new LinkedHashMap<>();

        for(T key: nodes.keySet()){
            Map<T, String> line = new LinkedHashMap<>();
            for (T key1: nodes.keySet()) {
                if (key.equals(key1))
                    line.put(key1, "0");
                else
                    line.put(key1, "\u221E");
            }
            for(Edge<T> edge: nodes.get(key)) {
                line.remove(edge.getToNode());
                line.put(edge.getToNode(), edge.getStringWeight());
            }
            mat.put(key, line);
        }

        return mat;
    }

    public void printAdjacencyMatrix(Map<T,Map<T,String>> mat) {
        System.out.println("=== Adjacency Matrix ===");
        System.out.print("\t");
        for (T key: mat.keySet()){
            System.out.print(key+"\t");
        }
        System.out.println();
        for (T key: mat.keySet()){
            System.out.print(key+"\t");
            for (T key1: nodes.keySet()){
                System.out.print(mat.get(key).get(key1) + "\t");
            }
            System.out.println();
        }
    }

    public Map<T, ArrayList<String[]>> adjacencyList() {
        Map<T, ArrayList<String[]>> list = new LinkedHashMap<>();
        for(T key: nodes.keySet()){
            ArrayList<String[]> line = new ArrayList<>();
            for (Edge<T> value: nodes.get(key)) {
                String[] arr = new String[2];
                String temp = "" + value.getToNode();
                arr[0] = temp;
                arr[1] = value.getStringWeight();
                line.add(arr);
            }
            list.put(key, line);
        }

        return list;
    }

    public void printAdjacencyList(Map<T, ArrayList<String[]>> list){
        System.out.println("=== Adjacency List ===");
        for (T key: list.keySet()){
            System.out.print(key + "\t->\t");
            for (String[] arr: list.get(key)) {
                System.out.print(Arrays.toString(arr) + "\t->\t");
            }
            System.out.println();
        }
    }

    @Override
    public String toString(){
        Map<T, ArrayList<String[]>> list = adjacencyList();
        printAdjacencyList(list);

        Map<T,Map<T,String>> matrix = adjacencyMatrix();
        printAdjacencyMatrix(matrix);
        return "";
    }
}
