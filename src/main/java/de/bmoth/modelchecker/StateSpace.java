package de.bmoth.modelchecker;

import java.util.*;

public class StateSpace {
    private final Set<Vertex> vertices;
    private final Map<Vertex, Set<Vertex>> edges;
    private final Map<Vertex, StateSpaceNode> vertexToSpaceStateNode;

    private int index;
    private Queue<Vertex> unseen;
    private Deque<Vertex> stack;

    public StateSpace(Set<StateSpaceNode> spaceStateRoot) {
        vertices = new LinkedHashSet<>();
        edges = new HashMap<>();

        // helper map to allow outputting of SpaceStateNodes after completion of tarjan
        vertexToSpaceStateNode = new HashMap<>();

        // temporary helper map to prevent duplicate generation of same vertex
        Map<StateSpaceNode, Vertex> spaceStateNodeToVertex = new HashMap<>();

        // breadth-first generation of vertices
        Queue<StateSpaceNode> queue = new ArrayDeque<>();
        Set<StateSpaceNode> visited = new HashSet<>();

        // init queue
        for (StateSpaceNode root : spaceStateRoot) {
            queue.add(root);
            visited.add(root);
        }

        // process queue ...
        while (!queue.isEmpty()) {
            StateSpaceNode node = queue.poll();
            Vertex vertex = generateUniqueVertex(node, spaceStateNodeToVertex);

            // ... store vertex
            vertices.add(vertex);

            // ... process successors
            Set<Vertex> successors = new LinkedHashSet<>();
            for (StateSpaceNode successorNode : node.getSuccessors()) {
                if (!visited.contains(successorNode)) {
                    queue.add(successorNode);
                    visited.add(successorNode);
                }

                Vertex successor = generateUniqueVertex(successorNode, spaceStateNodeToVertex);
                successors.add(successor);
            }

            // ... store edges
            edges.put(vertex, successors);
        }
    }

    public List<List<StateSpaceNode>> getStronglyConnectedComponents() {
        List<List<StateSpaceNode>> sccList = new ArrayList<>();

        index = 0;
        unseen = new ArrayDeque<>(vertices);
        stack = new ArrayDeque<>();

        while (!unseen.isEmpty()) {
            Vertex v0 = unseen.poll();
            tarjan(v0, sccList);
        }

        return sccList;
    }

    private void tarjan(Vertex current, List<List<StateSpaceNode>> sccList) {
        current.init(index);
        index++;

        stack.push(current);
        unseen.remove(current);

        for (Vertex successor : edges.get(current)) {
            if (unseen.contains(successor)) {
                tarjan(successor, sccList);
                current.adjustLowLink(successor.lowLink);
            } else if (stack.contains(successor)) {
                current.adjustLowLink(successor.index);
            }
        }

        // if root of scc
        if (current.isSscRoot()) {
            List<StateSpaceNode> currentScc = new ArrayList<>();

            Vertex top;
            do {
                top = stack.pop();
                currentScc.add(vertexToSpaceStateNode.get(top));
            }
            while (current != top);
            sccList.add(currentScc);
        }
    }

    private Vertex generateUniqueVertex(StateSpaceNode node, Map<StateSpaceNode, Vertex> spaceStateNodeToVertex) {
        Vertex vertex;
        if (spaceStateNodeToVertex.containsKey(node)) {
            vertex = spaceStateNodeToVertex.get(node);
        } else {
            vertex = new Vertex(node.getState());
            spaceStateNodeToVertex.put(node, vertex);
            vertexToSpaceStateNode.put(vertex, node);
        }
        return vertex;
    }

    static class Vertex {
        int index;
        int lowLink;
        State state;

        Vertex(State state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return state.toString() + " (" + lowLink + "," + index + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Vertex)) {
                return false;
            }
            if (this == obj) {
                return true;
            }

            Vertex that = (Vertex) obj;
            return this.state.equals(that.state);
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }

        void init(int index) {
            this.index = index;
            this.lowLink = index;
        }

        void adjustLowLink(int lowLink) {
            this.lowLink = Math.min(this.lowLink, lowLink);
        }

        boolean isSscRoot() {
            return index == lowLink;
        }
    }
}
