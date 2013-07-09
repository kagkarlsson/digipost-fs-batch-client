package no.bekk.java.dpostbatch.model;

public class Brev {

	public String id;
	public String foedselsnummer;
	public String brevFil;
	public String emne;
	public String kundeId;

	
	public Brev(String id, String kundeId, String foedselsnummer, String emne, String brevFil) {
		this.id = id;
		this.brevFil = brevFil;
		this.emne = emne;
		this.kundeId = kundeId;
		this.foedselsnummer = foedselsnummer;
	}

	public String getDokumentId() {
		return "dok-" + id;
	}
	
}
