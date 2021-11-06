package org.example.tp3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersistentState {

    private final List<PersistentStateUnit> state = new ArrayList<>();

    public void addNewStateUnit(PersistentStateUnit stateUnit) {
        state.add(stateUnit);
    }

    public List<PersistentStateUnit> getState() {
        return new ArrayList<>(state);
    }

    @Override
    public String toString() {
        return state.stream()
                .map(e -> e.answer)
                .collect(Collectors.joining(";\n", "[", "]"));
    }

    public static final class PersistentStateUnit {

        private String answer;

        public PersistentStateUnit(String answer) {
            this.answer = answer;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
