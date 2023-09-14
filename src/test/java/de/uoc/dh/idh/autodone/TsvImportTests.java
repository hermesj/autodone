package de.uoc.dh.idh.autodone;

import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import de.uoc.dh.idh.autodone.entities.MastodonPost;
import de.uoc.dh.idh.autodone.entities.MastodonUser;
import de.uoc.dh.idh.autodone.entities.PostGroup;
import de.uoc.dh.idh.autodone.repositories.PostGroupRepository;
import de.uoc.dh.idh.autodone.services.MalformedTsvException;
import de.uoc.dh.idh.autodone.services.SessionService;
import de.uoc.dh.idh.autodone.services.TsvService;
import jakarta.annotation.Resource;

@SpringBootTest
class TsvImportTests {

	
	@InjectMocks
	@Resource
	TsvService tsvs;
	
	@Mock
	private SessionService sessionService;
	
	@Mock
	private PostGroupRepository pgr;
	
	@BeforeEach
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    when(sessionService.getActiveUser()).thenReturn(new MastodonUser(null, null, null, null, null));
	    when(pgr.findByMstdIdAndMastodonuserId("0", 0)).thenReturn(new PostGroup());
	}

	@Test
	void testTSVService() throws MalformedTsvException {
		List<MastodonPost> parseTSV = tsvs.parseTSV(new File("src/test/resources/test.tsv"), "0", false, false);
		System.out.println(parseTSV);
	}

}
