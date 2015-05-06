/*Kevin Damazyn
 *Created 5/6/15
 *test_review.java
 *
 *Takes in a file in the format question.answer and will give instant
 *feedback on whether the question was right/wrong as well as the percentage.
 *
 *Only deletes the questions that were answered correctly.
 *
 *Able to cheat by knowing any letter of the answer, but this is supposed 
 *to be a study tool so I choose using contains() method over having to 
 *put in the whole answer if it was too long.
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.io.Console;
import java.util.regex.Pattern;

class test_review {

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static HashMap<String,String> test = new HashMap<String,String>();
  public static int right = 0;
  public static int total = 0;
  public static float percent = 0;
  public static Console input = System.console();
  public static Random generator = new Random();

  public static void readFile(String fileName) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    try {
      String line = br.readLine();
      while (line != null) {
          String question = line.split("\\.")[0];
          String answer = line.split("\\.")[1];
          test.put(question,answer);
          line = br.readLine();
      } 
    } finally {
        br.close();
    }
  }

  public static void ask_questions() {
    if (input == null) {
      System.err.println("No console.");
      System.exit(1);
    }

    Object[] questions = test.keySet().toArray();
    String answer = "";
    while (!test.isEmpty()){
      String question = (String)questions[generator.nextInt(questions.length)];
      if (test.containsKey(question)){
        printQuestion(question);
        answer = input.readLine();
        if (answer.equals("q")){break;}
        checkAnswer(question,answer);
        total++;
        percent = (float)right*100/total;
        System.out.print((isDone() ? 
          "Your final score is: " : "Your current score is: "));
        printPercent();
      }
    }
  }

  public static void printQuestion(String question){
      System.out.print((isTrueFalseQuestion(question) ? 
        "(t/f) " : "") + question + ": ");
  }

  public static Boolean isTrueFalseQuestion(String question){
    return (test.get(question).toLowerCase().equals("t") 
      || test.get(question).toLowerCase().equals("f"));
  }

  public static void checkAnswer(String question, String answer){
    if (isCredibleAnswer(question,answer)) {
      test.remove(question);
      right++;
      if (!isDone()){
        System.out.println(ANSI_GREEN + "Correct!" + ANSI_RESET + 
          " You have " + ANSI_BLUE + test.size() + ANSI_RESET + " left!");
      }
    }else {
      System.out.println(ANSI_RED + "Wrong." + ANSI_RESET +
        " The correct answer is: " + ANSI_GREEN + test.get(question) + ANSI_RESET);
    }
  }

  public static Boolean isCredibleAnswer(String question, String answer) {
    return (test.get(question).toLowerCase().contains(answer.toLowerCase()) 
      && Pattern.matches("[^\\s]",answer));
  }

  public static void printPercent(){
      System.out.println(
        (percent >= 70.0 ? ANSI_GREEN : ANSI_RED) + percent + ANSI_RESET);
  }

  public static Boolean isDone() {
    return test.size() == 0;
  }

  public static void main(String[] args) {
      if(args.length != 1){
        System.out.println("usage: java test_review file.");
        System.exit(0);
      }
      String filename = args[0];
      try {
        readFile(filename);
      }catch(IOException e){
        e.printStackTrace();
      }
      ask_questions();
  }
}