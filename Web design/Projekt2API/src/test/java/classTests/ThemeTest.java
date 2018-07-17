package classTests;

import static org.junit.Assert.*;

import org.junit.Test;

import server.Theme;

public class ThemeTest {

	@Test
	public void validThemeValue() {
		assertTrue(new Theme("TestTheme",0,0).isValid());
	}
	
	@Test
	public void emptyTheme() {
		assertFalse(new Theme("",0,0).isValid());
	}
	
	@Test
	public void nullTheme() {
		assertFalse(new Theme(null,0,0).isValid());	
	}
	
	@Test
	public void lessThanThreeTheme() {
		assertFalse(new Theme("Te",0,0).isValid());
	}

}
