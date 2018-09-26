package uk.gov.hmcts.ccd.definition.store.excel.endpoint;

import io.swagger.annotations.Api;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ccd.definition.store.excel.domain.definition.model.DefinitionFileUploadMetadata;
import uk.gov.hmcts.ccd.definition.store.excel.service.ImportServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static uk.gov.hmcts.ccd.definition.store.excel.endpoint.ImportController.URI_IMPORT;

/**
 * Controller that exposes an HTTP POST endpoint for the importer, for uploading Core Case Definition data as a
 * spreadsheet to be imported.
 */
@RestController
@Api(value = URI_IMPORT)
public class ImportController {

    public static final String URI_IMPORT = "/import";
    private static final Logger LOG = LoggerFactory.getLogger(ImportController.class);

    private ImportServiceImpl importService;

    @Autowired
    public ImportController(ImportServiceImpl importService) {
        this.importService = importService;
    }

    @Transactional
    @RequestMapping(value = URI_IMPORT, method = RequestMethod.POST)
    public ResponseEntity processUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.getSize() == 0) {
            throw new IOException("No file present or file has zero length");
        } else {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(file.getInputStream(), baos);
            byte[] bytes = baos.toByteArray();

            LOG.info("Importing Definition file...");
            final DefinitionFileUploadMetadata metadata =
                importService.importFormDefinitions(new ByteArrayInputStream(bytes));

            return new ResponseEntity<>("Case Definition data successfully imported", HttpStatus.CREATED);
        }
    }
}
