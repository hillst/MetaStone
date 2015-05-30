package edu.oregonstate.eecs.mcplan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of state transition history. A history may be saved or loaded to
 * a file.
 */
public final class History<S extends State, A> {
    private List<Node> nodes_;

    public class Node {
        private S state_;

        private A action_;

        public Node(S state, A action) {
            state_ = state;
            action_ = action;
        }

        public S getState() {
            return state_;
        }

        public A getAction() {
            return action_;
        }
    }

    public History() {
        nodes_ = new ArrayList<Node>();
    }

    public History(S state) {
        nodes_ = new ArrayList<Node>();
        nodes_.add(new Node(state, null));
    }

    public History(String filepath) throws IOException {
        load(filepath);
    }

    /**
     * Add the next action state pair in the history The action may be null if
     * it is first element in list
     * 
     * @param state
     *            is the current state
     * @param action
     *            is the action taken to end up in given state
     */
    public void add(S state, A action) {
        nodes_.add(new Node(state, action));
    }

    /**
     * Clears history data and loads state transition history from a file. TODO
     * - allow to read strings and interpret them as data.
     * 
     * @param filepath
     *            location of history file.
     */
    public void load(String filepath) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filepath));
        nodes_ = new ArrayList<Node>();
        input.close();
    }

    /**
     * Saves string representation of the History class to a file.
     * 
     * @param filepath
     *            location to save file.
     */
    public void save(String filepath) throws IOException {
        BufferedWriter output = new BufferedWriter(new FileWriter(filepath));
        output.write(toString());
        output.close();
    }

    public S getState(int index) {
        return nodes_.get(index).getState();
    }

    public A getAction(int index) {
        return nodes_.get(index).getAction();
    }

    public int getSize() {
        return nodes_.size();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Node node : nodes_) {
            if (node.getAction() != null)
                output.append(node.getAction() + "\n\n");
            output.append(node.getState() + "\n\n");
        }
        return output.toString();
    }
}
