package no.bekk.java.sax;

public class Brev {

	public String id;
	public String foedselsnummer;
	public String brevFil;
	public String emne;
	public String kundeId;

	
	public Brev(String id, String brevFil, String emne, String kundeId, String foedselsnummer) {
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
