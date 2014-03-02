package no.bekk.java.dpostbatch.pack;

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import no.bekk.java.dpostbatch.model.BatchSettings;
import no.bekk.java.dpostbatch.model.Document;

import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

public class MasseutsendelseWriter {

	private static final String NAMESPACE = "http://www.digipost.no/xsd/avsender2_0";
	private boolean indent;

	public MasseutsendelseWriter(boolean indent) {
		this.indent = indent;
	}

	public void write(BatchSettings settings, DocumentProvider brevProvider, OutputStream destination) throws XMLStreamException  {
		SMOutputFactory outf = new SMOutputFactory(XMLOutputFactory.newInstance());
		SMOutputDocument doc = outf.createOutputDocument(destination);
		if (indent) {
			doc.setIndentation("\r\n                        ", 2, 2);
		}
		
		SMNamespace namespace = doc.getNamespace(NAMESPACE);
		SMOutputElement root = doc.addElement(namespace, "masseutsendelse");
		
		addSettings(root, namespace, settings);
		SMOutputElement standardDist = root.addElement(namespace, "standard-distribusjon");
		addDocuments(standardDist, namespace, brevProvider);
		
		brevProvider.reset();
		addBrev(standardDist, namespace, brevProvider);

		doc.closeRoot();
	}

	private void addSettings(SMOutputElement root, SMNamespace namespace, BatchSettings s) throws XMLStreamException {
		SMOutputElement settings = root.addElement(namespace, "jobb-innstillinger");
		settings.addElementWithCharacters(namespace, "avsender-id", s.avsenderId);
		settings.addElementWithCharacters(namespace, "jobb-id", s.jobbId);
		settings.addElementWithCharacters(namespace, "jobb-navn", s.jobbNavn);
		settings.addElementWithCharacters(namespace, "auto-godkjenn-jobb", s.autogodkjenn);
	}

	private void addDocuments(SMOutputElement standardDist, SMNamespace namespace, DocumentProvider brevProvider) throws XMLStreamException {
		SMOutputElement post = standardDist.addElement(namespace, "post");
		Document b = null;
		while ((b = brevProvider.next()) != null) {
			SMOutputElement brev = post.addElement(namespace, "dokument");
			brev.addElement(namespace, "id").addCharacters(b.getDokumentId());
			brev.addElement(namespace, "fil").addCharacters(b.inneholdFil);
			SMOutputElement innstillinger = brev.addElement(namespace, "innstillinger");
			innstillinger.addElement(namespace, "emne").addCharacters(b.emne);
			innstillinger.addElement(namespace, "sms-varsling");
			innstillinger.addElement(namespace, "autentiseringsnivaa").addCharacters("PASSORD");
			innstillinger.addElement(namespace, "sensitivitetsnivaa").addCharacters("NORMALT");
		}
	}

	private void addBrev(SMOutputElement standardDist, SMNamespace namespace, DocumentProvider brevProvider) throws XMLStreamException {
		SMOutputElement forsendelser = standardDist.addElement(namespace, "forsendelser");
		Document b = null;
		while ((b = brevProvider.next()) != null) {
			SMOutputElement brev = forsendelser.addElement(namespace, "brev");
			brev.addElement(namespace, "id").addCharacters(b.id);
			
			SMOutputElement mottaker = brev.addElement(namespace, "mottaker");
			mottaker.addElement(namespace, "kunde-id").addCharacters(b.kundeId);
			mottaker.addElement(namespace, "foedselsnummer").addCharacters(b.foedselsnummer);
			
			SMOutputElement innstillinger = brev.addElement(namespace, "innstillinger");
			innstillinger.addElement(namespace, "emne").addCharacters(b.emne);
			
			brev.addElement(namespace, "dokument").addCharacters(b.getDokumentId());
		}
	}
}
