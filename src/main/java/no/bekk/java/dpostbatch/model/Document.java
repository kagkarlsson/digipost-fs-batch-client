package no.bekk.java.dpostbatch.model;

public class Document {

	public String id;
	public String foedselsnummer;
	public String inneholdFil;
	public String emne;
	public String kundeId;

	
	public Document(String id, String kundeId, String foedselsnummer, String emne, String inneholdFil) {
		this.id = id;
		this.inneholdFil = inneholdFil;
		this.emne = emne;
		this.kundeId = kundeId;
		this.foedselsnummer = foedselsnummer;
	}

	public String getDokumentId() {
		return "dok-" + id;
	}
	
}
