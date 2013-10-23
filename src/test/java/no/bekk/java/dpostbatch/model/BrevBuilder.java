package no.bekk.java.dpostbatch.model;

public class BrevBuilder {

	private String id = "id1";
	private String kundeId = "kundeid1";
	private String foedselsnummer = "01010112345";
	private String emne = "Hej";
	private String brevFil = "file.pdf";

	private BrevBuilder() {
	}
	
	public static BrevBuilder newBrev() {
		return new BrevBuilder();
	}
	
	public Brev build() {
		return new Brev(id, kundeId, foedselsnummer, emne, brevFil);
	}

	public BrevBuilder withFile(String file) {
		this.brevFil = file;
		return this;
	}

}
