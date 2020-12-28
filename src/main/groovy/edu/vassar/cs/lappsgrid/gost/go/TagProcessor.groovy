package edu.vassar.cs.lappsgrid.gost.go

import groovy.util.logging.Slf4j
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.Contains

/**
 *
 */
@Slf4j("logger")
class TagProcessor extends Processor {

    static final String GOTAG = TAG + "#GO"

    TagProcessor(Container container) {
        super(container)
    }

    void createView() {
        tagView = container.newView()

        Contains contains = tagView.addContains(GOTAG, PRODUCER, 'http://geneontology.org')
        contains.put("tagSet", "http://geneontology.org")
        contains.dependency(tokenView.id, Discriminators.Uri.TOKEN)
    }

    Annotation createAnnotation(String tag) {
        Annotation tagged = tagView.newAnnotation("gotag-${nextId}".toString(), TAG)
        tagged.features.type = Discriminators.Uri.TAGS_SEM_BIO_GO
        return tagged
    }
}
