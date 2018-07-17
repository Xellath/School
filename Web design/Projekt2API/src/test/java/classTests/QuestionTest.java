package classTests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import server.Question;

public class QuestionTest {
	
	@Test
	public void validQuestion() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		list.add("fel2");
		assertTrue(new Question(null, "A Question", "Valid theme", "correctAnswer", list).isValid());
	}
	
	@Test
	public void nullQuestion() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		list.add("fel2");
System.out.println(new Question(null, null, "Valid theme", "correctAnswer", list).isValid());
		assertFalse(new Question(null, null, "Valid theme", "correctAnswer", list).isValid());
	}
	
	@Test
	public void nullTheme() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		list.add("fel2");
		assertFalse(new Question(null, "A Question", null, "correctAnswer", list).isValid());
	}
	
	@Test
	public void nullCorrectAnswer() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		list.add("fel2");
		assertFalse(new Question(null, "A Question", "Valid theme", null, list).isValid());
	}
	
	@Test
	public void nullIncorrectAnswers() {
		assertFalse(new Question(null, "A Question", "Valid theme", "correctAnswer", null).isValid());
	}
	
	@Test
	public void emptyQuestion() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		list.add("fel2");
		assertFalse(new Question(null, "", "Valid theme", "correctAnswer", list).isValid());
	}
	
	@Test
	public void emptyTheme() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		list.add("fel2");
		assertFalse(new Question(null, "A Question", "", "correctAnswer", list).isValid());
	}
	
	@Test
	public void emptyCorrectAnswer() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		list.add("fel2");
		assertFalse(new Question(null, "A Question", "Valid theme", "", list).isValid());
	}
	
	@Test
	public void lessThanTowIncorrectAnswers() {
		ArrayList<String> list = new ArrayList<>(2);
		list.add("fel1");
		assertFalse(new Question(null, "A Question", "Valid theme", "correctAnswer", list).isValid());
	}
	
	

}
