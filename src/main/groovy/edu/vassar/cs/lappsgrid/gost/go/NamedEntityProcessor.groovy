package edu.vassar.cs.lappsgrid.gost.go

import groovy.util.logging.Slf4j
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.Contains
import org.lappsgrid.vocabulary.Features
import org.lappsgrid.vocabulary.Metadata

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 *
 */
@Slf4j("logger")
class NamedEntityProcessor extends Processor {
    NamedEntityProcessor(Container container) {
        super(container)
    }

    void createView() {
        tagView = container.newView()
        Contains contains = tagView.addContains(Uri.NE, 'GOST', 'NER')
        contains.put(Metadata.NamedEntity.NAMED_ENTITY_CATEGORY_SET, "http://geneontology.org")
        contains.dependency(tokenView.id, Uri.TOKEN)

    }

    Annotation createAnnotation(String tag) {
        Annotation ne = tagView.newAnnotation("goner-${nextId}", Uri.NE)
        ne.features[Features.NamedEntity.CATEGORY] = tag
        return ne
//        Term term = index.lookup(tag)
//        if (term != null) {
//            ne.label = term.name
//        }
//        else {
//            ne.label = tag
//        }
    }
}
