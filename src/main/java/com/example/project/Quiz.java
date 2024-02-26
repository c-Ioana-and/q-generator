package com.example.project;

import java.io.*;

public class Quiz {
    String name;
    User author;
    int id = 0;
    int nr_answers;
    User[] completed_by;
    int[] results;
    int nr_completions;
    Question[] questions;
    int nr_questions;

    static Quiz[] quizzes;
    static int nr_quizzes = 0;

    Quiz (String name, User user) {
        questions = new Question[10];
        completed_by = new User[10];
        results = new int[10];
        nr_completions = 0;
        nr_questions = 0;
        this.name = name;
        this.author = user;
    }
    static public void addQuiz (Quiz q) {
        try (FileWriter fw = new FileWriter("Quiz" + (nr_quizzes + 1) +".csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(q);

            quizzes[nr_quizzes] = q;
            nr_quizzes++;
            q.id = nr_quizzes;

            System.out.println("{ 'status' : 'ok', 'message' : 'Quizz added succesfully'}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int searchQuiz (String name) {
        int i;
        for (i = 0 ; i < nr_quizzes ; i++)
            if (quizzes[i].name.equals(name))
                return quizzes[i].id;

        return -1;
    }

    public static int searchQuizID (int id) {
        int i;
        for (i = 0 ; i < nr_quizzes ; i++)
            if (quizzes[i].id == id)
                return 1;

        return -1;
    }

    public void addQuestion (Question q) {
        for (int i = 0 ; i < q.id_ans ; i++) {
            nr_answers++;
            q.answers[i].id = nr_answers;
        }
        this.questions[nr_questions] = q;
        nr_questions++;
    }

    int searchUser (User user) {
        int i;
        for (i = 0 ; i < nr_completions; i++)
            if (completed_by[i].user_name.equals(user.user_name))
                return i;
        return -1;
    }

    public void deleteQuiz () {
        try {
            PrintWriter writer = new PrintWriter("Quiz" + id + ".csv");
            writer.print("");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Quiz.quizzes[id - 1] = null;

        // shift all vector elements to the left
        for (int i = id; i < nr_quizzes; i++)
            Quiz.quizzes[i - 1] = Quiz.quizzes[i];
        Quiz.nr_quizzes--;
    }

    public static void showAllQuizzes(User user) {
        int i;
        String value;
        System.out.print("{ 'status' : 'ok', 'message' : '[");

        for (i = 0 ; i < nr_quizzes ; i++) {
            if (quizzes[i].searchUser(user) == -1)
                value = "False";
            else value = "True";
            if (i != 0)
                System.out.print(", ");
            System.out.print("{\"quizz_id\" : \"" + quizzes[i].id + "\", ");
            System.out.print("\"quizz_name\" : \"" + quizzes[i].name + "\", ");
            System.out.print("\"is_completed\" : \"" + value + "\"}");
        }
        System.out.print("]'}");
    }

    public void printQuestions () {

        int i, j;
        System.out.print("{ 'status' : 'ok', 'message' : '[");
        for (i = 0 ; i < nr_questions ; i++) {
            if (i != 0)
                System.out.print(", ");
            System.out.print("{\"question-name\":\"" + questions[i].text + "\", ");
            System.out.print("\"question_index\":\"" + (i + 1) + "\", ");
            System.out.print("\"question_type\":\"" + questions[i].type + "\", \"answers\":\"[");
            for (j = 0 ; j < questions[i].id_ans; j++) {
                System.out.print("{\"answer_name\":\"" + questions[i].answers[j].text + "\", ");
                System.out.print("\"answer_id\":\"" + questions[i].answers[j].id + "\"}");
                if (j != questions[i].id_ans - 1)
                    System.out.print(", ");
            }
            System.out.print("]\"}");
            if (i == nr_questions - 1)
                System.out.print("]'}");
        }
    }

    public double calculatePoints (int id_ans) {
        double points = 0;

        // daca id-ul dat este mai mic sau egal cu id-ul ultimului
        // raspuns al intrebarii i, inseamna ca raspunsul corespunde
        // acelei intrebari
        int last_ans_id, prev_last_ans_id = 0;

        for (int i = 0; i < nr_questions; i++) {

            last_ans_id = questions[i].answers[questions[i].id_ans - 1].id;
            if (i != 0)
                prev_last_ans_id =questions[i - 1].answers[questions[i - 1].id_ans - 1].id;

            if (last_ans_id >= id_ans) {
                if (questions[i].answers[id_ans - prev_last_ans_id - 1].isCorrect())
                    points = 1f / (questions[i].correct_ans);

                else points -= 1f / (questions[i].id_ans - questions[i].correct_ans);
                break;
            }
        }
        return points;
    }

    public static void printMySolutions (User user) {
        System.out.print("{ 'status' : 'ok', 'message' : '[");
        for (int i = 0 ; i < nr_quizzes; i++) {
            int id_user = quizzes[i].searchUser(user);
            if (id_user != -1) {
                System.out.print("{\"quiz-id\" : \"" + Quiz.quizzes[i].id + "\", ");
                System.out.print("\"quiz-name\" : \"" + Quiz.quizzes[i].name + "\", ");
                System.out.print("\"score\" : \"" + Quiz.quizzes[i].results[id_user] + "\", ");
                System.out.print("\"index_in_list\" : \"" + (id_user + 1) + "\"}");
                if (i != nr_quizzes - 1)
                    System.out.print(", ");
            }
        }
        System.out.println("]'}");
    }

    public String toString() {
        int i = 0;
        String result = "";
        result += "Quiz name," + this.name + ", author," + this.author.user_name + "\nQuestion ";
        while (i < this.nr_questions) {
            result += this.questions[i].toString();
            i++;
        }
        return result;
    }

    public static void cleanFiles () {
        int i = 0;
        while (new File("Quiz" + (i + 1) +".csv").isFile()) {
            try {
                PrintWriter writer = new PrintWriter("Quiz" + (i + 1) + ".csv");
                writer.print("");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }

}
