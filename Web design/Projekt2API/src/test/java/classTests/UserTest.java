package classTests;

import static org.junit.Assert.*;

import org.junit.Test;

import server.User;

public class UserTest {
	
	@Test
	public void validUser() {
		assertTrue(new User("validEmail@.provider", "can be any length", "Can be empty").isValid());
	}

	@Test
	public void invalidEmailMissingAt() {
		assertFalse(new User("invaldEmail.provider", "can be any length", "Can be empty").isValid());
	}
	
	@Test
	public void invalidEmailNull() {
		assertFalse(new User(null, "can be any length", "Can be empty").isValid());
	}
}
