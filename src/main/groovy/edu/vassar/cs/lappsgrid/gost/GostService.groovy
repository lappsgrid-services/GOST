package edu.vassar.cs.lappsgrid.gost

import edu.vassar.cs.lappsgrid.gost.go.LookupProcessor
import edu.vassar.cs.lappsgrid.gost.go.NamedEntityProcessor
import edu.vassar.cs.lappsgrid.gost.go.Processor
import edu.vassar.cs.lappsgrid.gost.go.TagProcessor
import edu.vassar.cs.lappsgrid.gost.http.BuilderClient
import edu.vassar.cs.lappsgrid.gost.http.HttpClient
import edu.vassar.cs.lappsgrid.gost.http.Response
import groovy.util.logging.Slf4j
import org.lappsgrid.api.WebService
import org.lappsgrid.metadata.ServiceMetadata
import org.lappsgrid.metadata.ServiceMetadataBuilder
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 *
 */
@Slf4j("logger")
class GostService implements WebService {
    private String metadata = null
    private Processor converter //= new Converter()

    @Override
    String execute(String input) {
        logger.info("execute() called.")
        Data data = Serializer.parse(input)

        if (Uri.ERROR == data.discriminator) {
            logger.debug("Input data in an error object.")
            return input
        }

        Container container
        if (Uri.TEXT == data.discriminator) {
            logger.trace("Input is text")
            container = new Container()
            container.language = 'en'
            container.text = data.payload.toString()
        }
        else if (Uri.LIF == data.discriminator) {
            logger.trace("Input is LIF")
            container = new Container((Map) data.payload)
        }
        else {
            logger.info("Invalid input discriminator: {}", data.discriminator)
            data.payload = "Invalid input discriminator type: ${data.discriminator}".toString()
            data.discriminator = Uri.ERROR
            return data.asPrettyJson()
        }

        logger.trace("Posting to the GOST service.")
        HttpClient gost = new BuilderClient()
        Response response = gost.post(container.text)
        if (response.status != 200) {
            logger.warn("Error response from the GOST services: {}", response.message)
            data.discriminator = Uri.ERROR
            data.payload = response.message
            return data.asPrettyJson()
        }
        logger.trace("Converting response")
        Object type = data.getParameter("type")
        if (type == null) {
            logger.debug("Using default TagProcessor")
            converter = new TagProcessor(container)
        }
        else {
            switch(type.toString()) {
                case "tag":
                    logger.debug("Using the TagProcessor")
                    converter = new TagProcessor(container)
                    break;
                case "ne":
                    logger.debug("Using the NamedEntityProcessolr")
                    converter = new NamedEntityProcessor(container)
                    break;
                case "lookup":
                    logger.debug("Using the LookupProcessor")
                    converter = new LookupProcessor(container)
                    break
                default:
                    logger.warn("Unrecognized type {}, using the TagProcessor")
                    converter = new TagProcessor(container)
                    break;
            }
        }
        converter.process(response.body)
        logger.trace("Returning the result.")
        return new Data(Uri.LIF, container).asPrettyJson()
    }

    @Override
    String getMetadata() {
        if (metadata == null) {
            init()
        }
        return metadata
    }

    synchronized void init() {
        if (metadata != null) {
            return
        }

        String versionString = Version.getVersion()
        InputStream stream = this.class.getResourceAsStream("/build")
        if (stream != null) {
            versionString += " Build ${stream.text.trim()}"
        }

        ServiceMetadata md = new ServiceMetadataBuilder()
            .name('GOST')
            .description('Gene Ontology Semantic Tagger')
            .vendor("http://www.lappsgrid.org")
            .version(versionString)
            .license(Uri.CC_BY_NC_SA)
            .licenseDesc('GOST is licensed under the `Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License <http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_US>`_')
            .requireFormats(Uri.TEXT, Uri.LIF)
            .requireEncoding('UTF-8')
            .requireLanguage('en')
            .produces(Uri.TOKEN, Uri.POS, Uri.LEMMA, Uri.LOOKUP, Uri.NE, TagProcessor.TAG)
            .produceEncoding('UTF-8')
            .produceLanguage('en')
            .build()

        metadata = new Data(Uri.META, md).asPrettyJson()
    }
}
