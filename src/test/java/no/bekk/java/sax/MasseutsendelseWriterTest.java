package no.bekk.java.sax;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.codehaus.stax2.validation.XMLValidationSchema;
import org.junit.Test;

import com.google.common.io.CharStreams;

public class MasseutsendelseWriterTest {

	@Test
	public void writeMasseutsendelse() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Brev b1 = new Brev("id1", "fil1.pdf", "emne", "kunde-id1", "01010112345");
		Brev b2 = new Brev("id2", "fil2.pdf", "emne", "kunde-id2", "02020212345");
		new MasseutsendelseWriter(true).write(new Settings("1000", "jobb-id", "jobb-navn", "false"),
				new TestableBrevProvider(b1, b2), out);

		String expected = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("/masseutsendelse.xml")));
		
		assertEquals(expected.replaceAll("\\s", ""), out.toString().replaceAll("\\s", ""));
		System.out.println(out.toString());
		assertTrue(validateXml(new ByteArrayInputStream(out.toByteArray())));
	}
	
	private boolean validateXml(InputStream xml) {
		try {
			XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xml);

			SchemaFactory factory = SchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
			Schema schema = factory.newSchema(new Source[] {
					new StreamSource(getClass().getResourceAsStream("/xsd/masseutsendelse.xsd"))
			});
			
			javax.xml.validation.Validator validator = schema.newValidator();
			validator.validate(new StAXSource(reader));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static class TestableBrevProvider implements BrevProvider {

		private Brev[] brev;
		int counter = 0;

		public TestableBrevProvider(Brev... brev) {
			this.brev = brev;
		}

		public Brev nextBrev() {
			if (brev.length > counter) {
				return brev[counter++];
			} else {
				return null;
			}
		}

		public BrevProvider reset() {
			counter = 0;
			return this;
		}

	}

}
