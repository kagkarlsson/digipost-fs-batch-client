package no.bekk.java.dpostbatch.task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import no.bekk.java.dpostbatch.model.Batch;
import no.bekk.java.dpostbatch.model.BatchBuilder;
import no.bekk.java.dpostbatch.model.SettingsProviderMock;
import no.bekk.java.dpostbatch.sftp.SftpReceipt;
import no.bekk.java.dpostbatch.task.receipt.CheckForReceiptTask;
import no.bekk.java.dpostbatch.task.receipt.NewReceiptHandler;
import no.bekk.java.dpostbatch.transfer.SftpAccount;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;

public class CheckForReceiptTaskTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	@Mock
	private NewReceiptHandler newReceiptHandler;
	@Mock
	private SftpAccount sftpAccount;
	private SettingsProviderMock settingsProvider;
	private Path batchesDir;
	private CheckForReceiptTask task;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		settingsProvider = new SettingsProviderMock(tempFolder.getRoot().toPath());
		batchesDir = settingsProvider.getBatchesDirectory();
		
		task = new CheckForReceiptTask(settingsProvider, sftpAccount, 
				newReceiptHandler);
	}

	@Test
	public void shouldNotifyHandlerWhenNewReceipt() throws IOException {
		BatchBuilder.newBatch(batchesDir).medUploadedFilename("min-mottakersplitt").awaitReceipt().build();
		when(sftpAccount.listReceipts())
			.thenReturn(Sets.newHashSet(SftpReceipt.fromReceiptName("min-mottakersplitt.resultat-20130101-123456789.zip")));
		task.run();
		verify(newReceiptHandler, times(1)).handle((Batch) any(), (SftpReceipt) any());
	}

}
