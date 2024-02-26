package com.example.project;

import java.io.IOException;
import java.io.PrintWriter;

public class Question {
    String text;
    String type;
    int id;
    static Question[] questions;
    static int nr_questions = 0;
    int id_ans;
    int correct_ans;
    Answer[] answers;

    class Answer {
        int id;
        String text;
        private int correct;

        Answer(String text, int correct) {
            this.text = text;
            this.correct = correct;
        }

        @Override
        public String toString() {
            return this.id + ". " + this.text + ",";
        }

        boolean isCorrect () {
            return correct == 1;
        }
    }

    Question (String text, String type, int ID) {
        this.text = text;
        this.type = type;
        this.id = ID;
        answers = new Answer[6];
        id_ans = 0;
    }

    public static void addQuestion (Question q) {
        try (PrintWriter out = new PrintWriter("Questions.csv")) {
            out.println(q);
            questions[nr_questions] = q;
            nr_questions++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int searchQuestion (String text) {
        int i;
        for (i = 0 ; i < nr_questions ; i++)
            if (questions[i].text.equals(text))
                return i;

        return -1;
    }

    static public Question searchQuestionID (int id) {
        int i;
        for (i = 0 ; i < nr_questions ; i++)
            if (questions[i].id == id)
                return questions[i];
        return null;
    }

    @Override
    public String toString() {
        int i = 0;
        String result = "";
        result += " " + this.id + "," + this.text + "\nanswers,";

        while (i < this.id_ans) {
            result += this.answers[i].toString();
            i++;
        }

        return result;
    }

    public static void cleanFile () {
        try {
            PrintWriter writer = new PrintWriter("Questions.csv");
            writer.print("");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
