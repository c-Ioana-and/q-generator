## Genetator de chestionare

# Scop
Acomodarea cu Java, conceptele programării orientate obiect (în cazul acestui proiect, cu encapsularea), folosirea comenzilor de I/O.

# Implementare

1. [Tema1](src/main/java/com/example/project/Tema1.java), in care parsez textul dat prin consola. Toate metodele care se afla deasupra main-ulu (_parseCheckUserInfo, addUser, addQuestion, getQuestionID, addQuiz, submitQuiz, deleteQuiz_) corespund comenzilor care necesita o abordare mai riguroasa :)
2. [User](src/main/java/com/example/project/Tema1.java), care retine datele fiecarui user: username, parola, un vector de intrebari create de acel user (_Question[] questions_). De asemenea, clasa contine un vector si o variabila statica pentru a retine userii creati, precum si numarul de useri existenti. Username-urile si parolele corespunzatoare sunt stocate in fisierul Users.csv. Aceasta clasa contine urmatoarele metode:
	- _cleanFile_ - sterge fisierul Users.csv;
	- _addUser_ - creeaza fisierul Users.csv (in cazul in care nu exista) si adauga user-ul in vectorul static users;
	- _searchUser_ - cauta un user in vectorul static users dupa username;
	- _searchQuestion_ - cauta o intrebare dupa textul ei;
	- _printQuestions_ - afiseaza toate intrebarile create de un user;
	- _addQuestion_ - adauga o intrebare in vectorul questions si in fisierul Questions.csv.

3. [Question (si Answer)](src/main/java/com/example/project/Tema1.java), in care retin textul, tipul, id-ul, un vector de raspunsuri (_Answer[] answers_), numarul de raspunsuri si numarul de raspunsuri corecte. Nu era neaparat ca Answer sa fie o clasa interna, insa am ales sa o implementez asa din motive logice (nu pot avea un raspuns fara o intrebare). Aceste clase contin doar metodele:
	- toString (suprascriere pentru afisarea fiecarui tip de obiect);
	- cleanFile - pentru a sterge fisierul Question.csv;
	- isCorrect - pentru a verifica daca un raspuns este corect (este un getter. Variabila _correct_ a unui raspuns este privata, arata daca acel raspuns este corect sau nu)

4. [Quiz](src/main/java/com/example/project/Tema1.java), in care retin numele quiz-ului, autorul (userul care a creat quiz-ul), id-ul, numarul total de raspunsuri pentru toate intrebarile, un vector care retine userii care au completat deja quiz-ul si un vector de intrebari adaugate la quiz. Pentru a retine toate quiz-urile create, am folosit un vector static (_static Quiz[] quizzes_). Aceasta clasa contine metodele:
	- Quiz - constructor
	- addQuiz - adauga un quiz la vectorul static quizzes si creeaza un fisier de tipul "QuizX.csv", unde X este id-ul quiz-ului;
	- searchQuiz - cauta un quiz in vectorul static dupa numele lui;
	- searchQuizID - cauta un quiz in vectorul static dupa ID-ul sau;
	- searchQuestionID - cauta o intrebare dupa ID-ul ei;
	- addQuestion - adauga o intrebare la vectorul questions al instantei curente si creste numarul total de raspunsuri; 
	- searchUser - verifica daca un user a completat deja quiz-ul curent (afiseaza un numar pozitiv daca da, -1 in caz contrar);
	- deleteQuiz - elimina quiz-ul din vectorul static si sterge fisierul acestuia
	- showAllQuizzes - afiseaza toate quiz-urile din vectorul quizzez;
	- printQuestions - afiseaza toate intrebarile dintr-un quiz;
	- calculatePoints - calculeaza cate puncte ia un user pentru un anumit raspuns;
	- printMySolutions - afiseaza solutiile trimise de user-ul dat ca parametru;
	- toString, cleanFiles, asemanator cu metodele din clasa Question.



