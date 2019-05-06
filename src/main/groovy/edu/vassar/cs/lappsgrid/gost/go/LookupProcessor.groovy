package edu.vassar.cs.lappsgrid.gost.go

import groovy.util.logging.Slf4j
import org.lappsgrid.serialization.lif.Contains

import static org.lappsgrid.discriminator.Discriminators.Uri
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container

/**
 *
 */
@Slf4j("logger")
class LookupProcessor extends Processor {
    LookupProcessor(Container container) {
        super(container)
    }

    void createView() {
        tagView = container.newView()
        Contains contains = tagView.addContains(Uri.LOOKUP, 'GOST', 'GO')
        contains.put("tagSet", "http://geneontology.org")
        contains.dependency(tokenView.id, Uri.TOKEN)
    }

    @Override
    Annotation createAnnotation(String tag) {
        Annotation lookup = tagView.newAnnotation()
        lookup.id = "golookup-${nextId}"
        lookup.atType = Uri.LOOKUP
        lookup.features.type = 'GO'
        return lookup
    }
}
