package edu.vassar.cs.lappsgrid.gost

import edu.vassar.cs.lappsgrid.gost.go.Processor
import edu.vassar.cs.lappsgrid.gost.go.TagProcessor
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.lappsgrid.api.WebService
import org.lappsgrid.metadata.ServiceMetadata
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.Contains
import org.lappsgrid.serialization.lif.View

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 *
 */
class ServiceTests {

    static final String EXPECTED = "Machine learning is not a biological process."

    WebService service

    @Before
    void setup() {
        service = new GostService()
    }

    @After
    void cleanup() {
        service = null
    }

    @Test
    void metadata() {
        String json = service.getMetadata()
        Data data = Serializer.parse(json)
        assert Uri.META == data.discriminator
        ServiceMetadata md = new ServiceMetadata((Map) data.payload)
        assert Version.version == md.getVersion()
        assert "http://www.lappsgrid.org" == md.getVendor()
    }

    @Test
    void testError() {
        String expected = "expected input should be returned unchanged."
        Data data = new Data(Uri.ERROR, expected)
        String json = service.execute(data.asJson())
        data = Serializer.parse(json)
        assert Uri.ERROR == data.discriminator
        assert expected == data.payload.toString()
    }

    @Test
    void invalidDiscriminator() {
        Data data = new Data(Uri.XML, "<text>This is invalid</text>")
        String json = service.execute(data.asJson())
        data = Serializer.parse(json)
        assert Uri.ERROR == data.discriminator
        println data.payload.toString()
    }

    @Test
    void text() {
        Data data = new Data(Uri.TEXT, EXPECTED)
        String json = service.execute(data.asJson())
        println json
        data = Serializer.parse(json)
        assert Uri.LIF == data.discriminator

        Container container = new Container((Map) data.payload)
        assert 'en' == container.language
        assert EXPECTED == container.text

        assert 2 == container.views.size()
        println data.asPrettyJson()
    }

    @Test
    void multiword() {
        String expected = "Invoke an immune response."
        Data data = new Data(Uri.TEXT, expected)
        String json = service.execute(data.asJson())
        println json
        data = Serializer.parse(json)
        assert Uri.LIF == data.discriminator

        Container container = new Container((Map) data.payload)
        assert 'en' == container.language
        assert expected == container.text

        assert 2 == container.views.size()

        View view = container.views[0]
        assert 3 == view.metadata.contains.size()
        assert view.contains(Uri.TOKEN)
        assert view.contains(Uri.POS)
        assert view.contains(Uri.LEMMA)

        view = container.views[1]
        assert 4 == view.annotations.size()
        assert view.getContains(Processor.TAG)
        println data.asPrettyJson()
    }

    @Test
    void lookup() {
        String expected = "Invoke an immune response."
        Data data = new Data(Uri.TEXT, expected)
        data.setParameter("type", "lookup")
        String json = service.execute(data.asJson())

        data = Serializer.parse(json)
        assert Uri.LIF == data.discriminator

        Container container = new Container((Map) data.payload)
        assert 2 == container.views.size()

        View view = container.views[1]
        assert view.contains(Uri.LOOKUP)
    }

    @Test
    void tag() {
        String expected = "Invoke an immune response."
        Data data = new Data(Uri.TEXT, expected)
        data.setParameter("type", "tag")
        String json = service.execute(data.asJson())

        data = Serializer.parse(json)
        assert Uri.LIF == data.discriminator

        Container container = new Container((Map) data.payload)
        assert 2 == container.views.size()

        View view = container.views[1]
        assert view.contains(TagProcessor.GOTAG)
    }

    @Test
    void ne() {
        String expected = "Invoke an immune response."
        Data data = new Data(Uri.TEXT, expected)
        data.setParameter("type", "ne")
        String json = service.execute(data.asJson())

        data = Serializer.parse(json)
        assert Uri.LIF == data.discriminator

        Container container = new Container((Map) data.payload)
        assert 2 == container.views.size()

        View view = container.views[1]
        assert view.contains(Uri.NE)
    }
}
