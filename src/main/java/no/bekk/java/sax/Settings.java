package no.bekk.java.sax;

public class Settings {

	public final String avsenderId;
	public final String jobbId;
	public final String jobbNavn;
	public final String autogodkjenn;

	public Settings(String avsenderId, String jobbId, String jobbNavn, String autogodkjenn) {
		this.avsenderId = avsenderId;
		this.jobbId = jobbId;
		this.jobbNavn = jobbNavn;
		this.autogodkjenn = autogodkjenn;
	}
	
}
