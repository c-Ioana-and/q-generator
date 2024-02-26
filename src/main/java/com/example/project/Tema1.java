package com.example.project;

public class Tema1 {
	public static int parseCheckUserInfo(String[] args) {
		// metoda care verifica daca credentialele sunt corecte
		if (args.length <= 2) {
			System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
			return -1;
		}

		String user = args[1].split(" ")[1].replace("'", "");
		String pass = args[2].split(" ")[1].replace("'", "");

		int id = User.searchUser(user);
		if (id == -1 || !User.users[id].password.equals(pass)) {
			System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
			return -1;
		}
		return id;
	}

	public static void addUser (String[] args) {
		if (args.length == 1) {
			// daca nu a fost mentionat un username
			System.out.println("{ 'status' : 'error', 'message' : 'Please provide username'}");
			return;
		}
		if (args.length == 2) {
			// daca nu a fost mentionata o parola
			System.out.println("{ 'status' : 'error', 'message' : 'Please provide password'}");
		} else {
			// parsez informatiile date de la consola

			String user = args[1].split(" ")[1].replace("'", "");
			String pass = args[2].split(" ")[1].replace("'", "");

			// verific daca exista deja acel user in fisier
			if (User.searchUser(user) >= 0)
				System.out.println("{ 'status' : 'error', 'message' : 'User already exists' }");
			else User.addUser(user, pass);
		}
	}

	public static void addQuestion (String[] args, int id) {
		String text = args[3].split(" ")[0].replace("'", "");
		if (!text.equals("-text")) {
			System.out.println("{ 'status' : 'error', 'message' : 'No question text provided'}");
			return;
		}

		text = args[3].split("'")[1];
		if (text.equals("")) {
			System.out.println("{ 'status' : 'error', 'message' : 'No question text provided'}");
			return;
		}


		if (Question.searchQuestion(text) >= 0) {
			System.out.println("{ 'status' : 'error', 'message' : 'Question already exists'}");
			return;
		}

		String type = args[4].split(" ")[1].replace("'", "");
		Question q = new Question(text, type, User.users[id].nr_questions + 1);

		int i = 5, value, is_correct = 0;
		String answer_is;

		if (args.length == 5) {
			System.out.println("{ 'status' : 'error', 'message' : 'No answer provided'}");
			return;
		}

		while (args.length >= i + 2) {
			String ans_text = args[i].split(" ")[1].replace("'", "");

			if (ans_text.equals("1") || ans_text.equals("0")) {
				System.out.println("{ 'status' : 'error', 'message' : 'Answer " + i / 5 + " has no answer description'}");
				return;
			}

			// caut daca acel raspuns a fost deja adaugat
			for (int j = 0; j < q.id_ans; j++) {
				if (q.answers[j].text.equals(ans_text)) {
					System.out.println("{ 'status' : 'error', 'message' : 'Same answer provided more than once'}");
					return;
				}
			}
			// preiau valoarea de adevar a raspunsului
			answer_is = args[i + 1].split(" ")[0];

			// verific daca raspunsul are aceasta valoare precizata
			if (!answer_is.matches("-answer(.*)-is-(.*)")) {
				System.out.println("{ 'status' : 'error', 'message' : 'Answer " + i / 5 + " has no answer correct flag'}");
				return;
			}

			// retin cate raspunsuri sunt corecte, va fi relevant pt calcularea punctajului
			value = Integer.parseInt(args[i + 1].split(" ")[1].replace("'", ""));
			if (value == 1)
				is_correct++;

			if (is_correct > 1 && type.equals("single")) {
				System.out.println("{ 'status' : 'error', 'message' : 'Single correct answer question has more than one correct answer'}");
				return;
			}

			Question.Answer answer_i = q.new Answer(ans_text, value);
			q.answers[q.id_ans] = answer_i;
			q.id_ans++;

			i += 2;
		}

		// s-au oferit doar 5 argumente, deci doar un raspuns a fost dat
		if (i == 7) {
			System.out.println("{'status':'error','message':'Only one answer provided'}");
			return;
		}

		int id_q = User.users[id].nr_questions;
		User.users[id].questions[id_q] = q;
		q.correct_ans = is_correct;
		User.users[id].addQuestion(q);
		Question.addQuestion(q);
	}

	public static void getQuestionID (String[] args) {
		// cauta o intrebare in functie de textul ei
		// este cautata in intrebarile intregului sistem
		String text = args[3].split("'")[1];
		int id_q = Question.searchQuestion(text);
		if (id_q == -1) {
			System.out.println("{ 'status' : 'error', 'message' : 'Question does not exist'}");
			return;
		}
		System.out.println("{ 'status' : 'ok', 'message' : '" + Question.questions[id_q].id + "'}");
	}

	public static void addQuiz (String[] args, int id) {
		String name = (args[3].split("'"))[1].replace("'", "");
		// daca quiz-ul cu acel nume exista deja
		if (Quiz.searchQuiz(name) != -1) {
			System.out.println("{ 'status' : 'error', 'message' : 'Quizz name already exists'}");
			return;
		}

		Quiz q = new Quiz(name, User.users[id]);

		// iau fiecare intrebare si o caut in sistem
		// daca nu exista, nu o adaug
		int i = 4;
		while (i < args.length) {
			int question_id = Integer.parseInt(args[i].split(" ")[1].replace("'", ""));
			Question question = Question.searchQuestionID(question_id);
			if (question == null) {
				System.out.println("{ 'status' : 'error', 'message' : 'Question ID for question " + question_id + " does not exist'}");
				return;
			}
			q.addQuestion(question);
			i++;
		}
		Quiz.addQuiz(q);
	}

	public static void submitQuiz (String[] args, int id) {
		if (args.length < 4) {
			System.out.println("{ 'status' : 'error', 'message' : 'No quizz identifier was provided'}");
			return;
		}

		int id_quiz = Integer.parseInt(args[3].split(" ")[1].replace("'", ""));

		// n-am gasit quiz-ul
		if (Quiz.searchQuizID(id_quiz) == -1) {
			System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
			return;
		}

		// daca quiz-ul a fost facut de user-ul curent
		if (Quiz.quizzes[id_quiz - 1].author == User.users[id]) {
			System.out.println("{ 'status' : 'error', 'message' : 'You cannot answer your own quiz'}");
			return;
		}

		//deja s-a raspuns la acest quiz
		if (Quiz.quizzes[id_quiz - 1].searchUser(User.users[id]) != -1) {
			System.out.println("{ 'status' : 'error', 'message' : 'You already submitted this quizz'}");
			return;
		}

		// iau fiecare raspuns dat si verific daca este corect sau nu
		int i = 4;
		double points = 0;
		while (i < args.length) {
			int id_ans = Integer.parseInt(args[i].split(" ")[1].replace("'", ""));
			points += Quiz.quizzes[id_quiz - 1].calculatePoints(id_ans) / Quiz.quizzes[id_quiz - 1].nr_questions;
			i++;
		}
		if (points < 0)
			points = 0;
		else points *= 100;

		// pentru aproximatie
		double aux = points - Math.floor(points);
		if (aux <= 0.5)
			points = Math.floor(points);
		else points = Math.ceil(points);

		// retin rezultatul si il afisez
		Quiz.quizzes[id_quiz - 1].results[Quiz.quizzes[id_quiz - 1].nr_completions] = (int) points;
		Quiz.quizzes[id_quiz - 1].completed_by[Quiz.quizzes[id_quiz - 1].nr_completions] = User.users[id];
		Quiz.quizzes[id_quiz - 1].nr_completions++;
		System.out.println("{ 'status' : 'ok', 'message' : '" + (int)points + " points'}");
	}

	public static void deleteQuiz (String[] args, int id) {
		if (args.length < 4) {
			System.out.println("{'status':'error','message':'No quizz identifier was provided'}");
			return;
		}

		int quiz_id = Integer.parseInt(args[3].split(" ")[1].replace("'", ""));
		// quiz-ul nu exista
		if (Quiz.searchQuizID(quiz_id) == -1) {
			System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
			return;
		}

		//quiz-ul nu poate fi sters decat de catre creator
		if (User.users[id] != Quiz.quizzes[quiz_id - 1].author) {
			System.out.println("{ 'status' : 'error', 'message' : 'You are not the creator of this quiz'}");
			return;
		}

		Quiz.quizzes[quiz_id - 1].deleteQuiz();
		System.out.println("{ 'status' : 'ok', 'message' : 'Quizz deleted successfully'}");
	}

	public static void main(final String[] args) {

		if (args == null) {
			System.out.print("Hello world!");
			return;
		}
		if (args[0].equals("-cleanup-all")) {
			Quiz.cleanFiles();
			User.cleanFile();
			Question.cleanFile();

			User.users = new User[10];
			User.nr_users = 0;
			Quiz.quizzes = new Quiz[10];
			Quiz.nr_quizzes = 0;
			Question.questions = new Question[10];
			Question.nr_questions = 0;
			return;
		}
		if (args[0].equals("-create-user")) {
			addUser(args);
			return;
		}
		if (args[0].equals("-create-question")) {
			int id = parseCheckUserInfo(args);
			if (id != -1)
				addQuestion(args, id);
			else return;
		}
		if (args[0].equals("-get-question-id-by-text")) {
			int id = parseCheckUserInfo(args);
			if (id != -1)
				getQuestionID(args);
			return;
		}
		if (args[0].equals("-get-all-questions")) {
			int id = parseCheckUserInfo(args);
			if (id != -1)
				User.users[id].printQuestions();
			return;
		}
		if (args[0].equals("-create-quizz")) {
			int id = parseCheckUserInfo(args);
			if (id != -1) {
				addQuiz(args, id);
			}
			return;
		}
		if (args[0].equals("-get-quizz-by-name")) {
			int id = parseCheckUserInfo(args);
			if (id != -1) {
				String name = (args[3].split("'"))[1].replace("'", "");
				int idQ = Quiz.searchQuiz(name);
				if (idQ == -1) {
					System.out.println("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
					return;
				}
				System.out.println("{ 'status' : 'ok', 'message' : '" + idQ + "'}");
			}
			return;
		}
		if (args[0].equals("-get-quizz-details-by-id")) {
			int id = parseCheckUserInfo(args);
			if (id != -1) {
				int id_quiz = Integer.parseInt(args[3].split(" ")[1].replace("'", ""));
				Quiz.quizzes[id_quiz - 1].printQuestions();
			}
			return;
		}
		if (args[0].equals("-get-all-quizzes")) {
			int id = parseCheckUserInfo(args);
			if (id != -1)
				Quiz.showAllQuizzes(User.users[id]);
			return;
		}
		if (args[0].equals("-submit-quizz")) {
			int id = parseCheckUserInfo(args);
			if (id != -1)
				submitQuiz(args, id);
			return;
		}
		if (args[0].equals("-delete-quizz-by-id")) {
			int id = parseCheckUserInfo(args);
			if (id != -1)
				deleteQuiz(args, id);
			return;
		}
		if (args[0].equals("-get-my-solutions")) {
			int id = parseCheckUserInfo(args);
			if (id != -1) {
				Quiz.printMySolutions(User.users[id]);
			}
		}
	}
}