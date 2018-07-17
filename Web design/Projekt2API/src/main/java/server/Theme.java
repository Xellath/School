package server;

public class Theme {
	private String theme;
	private int themeId;
	private int nbrOfQuestions;


	public Theme(String theme, int themeId, int nbrOfQuestions) {
		this.themeId = themeId;
		if(theme != null)
			this.theme = theme.substring(0, 1).toUpperCase() +  theme.substring(1).toLowerCase();
		this.nbrOfQuestions = nbrOfQuestions;
	}

	public void setTheme(String theme){
		this.theme = theme;
	}

	public int getThemeId(){
		return themeId;
	}

	public String getTheme(){
		return theme;
	}

	public int getNbrOfQuestions() {
		return nbrOfQuestions;
	}
	
	public boolean isValid() {
		if(theme != null && !theme.equals("") && theme.length()>2) {
			return true;
		}
		return false;
	}
}