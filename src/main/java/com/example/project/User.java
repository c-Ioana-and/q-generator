package com.example.project;

import java.io.*;

public class User {

    String user_name;
    String password;

    int nr_questions;
    Question[] questions;

    static User[] users;
    static int nr_users = 0;
    User (String user, String pass) {
        user_name = user;
        password = pass;
        questions = new Question[10];
        nr_questions = 0;
    }

    public static void cleanFile () {
        try {
            PrintWriter writer = new PrintWriter("Users.csv");
            writer.print("");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void addUser (String user_name, String password) {
        try (FileWriter fw = new FileWriter("Users.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(user_name + "," + password);

            users[nr_users] = new User(user_name, password);
            nr_users++;

            System.out.println("{ 'status' : 'ok', 'message' : 'User created successfully'}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static int searchUser (String user_name) {
        int i;
        for (i = 0 ; i < nr_users ; i++)
            if (users[i].user_name.equals(user_name))
                return i;

        return -1;
    }

    public void printQuestions () {

        int i;
        System.out.print("{ 'status' : 'ok', 'message' : '[");

        for (i = 0 ; i < nr_questions ; i++) {
            if (i != 0)
                System.out.print(", ");
            System.out.print("{\"question_id\" : \"" + (i + 1) + "\", ");
            System.out.print("\"question_name\" : \"" + questions[i].text + "\"}");

            if (i == nr_questions - 1)
                System.out.print("]'}");
        }
    }

    public void addQuestion (Question q) {
        this.questions[nr_questions] = q;
        nr_questions++;
        System.out.println("{'status':'ok','message':'Question added successfully'}");
    }
}
