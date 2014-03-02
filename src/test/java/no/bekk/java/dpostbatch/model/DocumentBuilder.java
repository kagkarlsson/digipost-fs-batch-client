package no.bekk.java.dpostbatch.model;

public class DocumentBuilder {

	private String id = "id1";
	private String kundeId = "kundeid1";
	private String foedselsnummer = "01010112345";
	private String emne = "Hej";
	private String brevFil = "file.pdf";

	private DocumentBuilder() {
	}
	
	public static DocumentBuilder newDocument() {
		return new DocumentBuilder();
	}
	
	public Document build() {
		return new Document(id, kundeId, foedselsnummer, emne, brevFil);
	}

	public DocumentBuilder withFile(String file) {
		this.brevFil = file;
		return this;
	}

}
