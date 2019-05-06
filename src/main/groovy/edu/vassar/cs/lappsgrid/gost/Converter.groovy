package edu.vassar.cs.lappsgrid.gost

import edu.vassar.cs.lappsgrid.gost.go.Index
import edu.vassar.cs.lappsgrid.gost.go.Processor
import edu.vassar.cs.lappsgrid.gost.go.TagProcessor
import edu.vassar.cs.lappsgrid.gost.go.Term
import groovy.util.logging.Slf4j
import org.lappsgrid.serialization.lif.*
import org.lappsgrid.vocabulary.Features
import org.lappsgrid.vocabulary.Metadata

import static org.lappsgrid.discriminator.Discriminators.*
/**
 *
 */
@Slf4j("logger")
class Converter {

    enum State { waiting, processing }

//    static final String TAG = "http://vocab.lappsgrid.org/Tag"

    Index index = new Index()


    void process(Container container, String input) {
        Processor processor = new TagProcessor(container)
        processor.process(input)
    }

//    void _process(Container container, String input) {
//        Map<String,Annotation> mweIndex = [:]
//        String text = container.text
//        View view = container.newView()
//        view.addContains(Uri.TOKEN, 'GOST', "token")
//        Contains contains = view.addContains(Uri.POS, 'GOST', "pos")
//        contains.put(Metadata.Token.POS_TAG_SET, Uri.TAGS_POS + '#claws7')
//        view.addContains(Uri.LEMMA, 'GOST', 'lemma')
//
//        View newView = null
//
//        State state = State.waiting
//        int count = 0
//        int offset = 0
//        int nerid = 0
//
////        input.eachLine { String line ->
////            if (state == State.waiting) {
////                if (line.startsWith('S_BEGIN')) {
////                    state = State.processing
////                }
////            }
////            else if (state == State.processing) {
////                if (line.startsWith('S_END')) {
////                    state = State.waiting
////                }
////                else {
////                    Annotation a = view.newAnnotation()
////                    a.atType = Uri.TOKEN
////                    a.id = "gost-${++count}"
////                    String[] parts = line.split('\t')
////                    String word = parts[0]
////                    a.features['word'] = word
////                    a.features['lemma'] = parts[1]
////                    a.features['pos'] = parts[2]
////                    // Save the list of tags for processing below.
////                    List<String> tags = parts[3].tokenize()
//////                    a.features['semtag'] = tags
//////                    a.features['mwe'] = parts[4]
////                    //a.features['tags'] = makeTags(parts[5])
////                    String mwe = parts[4]
////
////                    a.start = text.indexOf(word, offset)
////                    a.end = a.start + word.length()
////                    offset = a.end
////
////                    // Now check if we have any GO annotations
//////                    Annotation lookup = null
//////                    Annotation ne = null
////                    Set ids = [] as HashSet
////                    Annotation tagged = null
////                    tags.each { tag ->
////                        if (tag.startsWith('GO:')) {
////                            ids.add(tag)
////                            if (newView == null) {
////                                newView = container.newView()
//////                                contains = createView.addContains(Uri.LOOKUP, 'GOST', 'GO')
//////                                contains.put("tagSet", "http://geneontology.org")
//////                                contains.dependency(view.id, Uri.TOKEN)
//////                                contains = createView.addContains(Uri.NE, 'GOST', 'NER')
//////                                contains.put(Metadata.NamedEntity.NAMED_ENTITY_CATEGORY_SET, "http://geneontology.org")
//////                                contains.dependency(view.id, Uri.TOKEN)
////
////                                contains = newView.addContains(TAG, 'gost', 'semtag')
////                                contains.put("tagSet", "http://geneontology.org")
////                                contains.dependency(view.id, Uri.TOKEN)
////                            }
//////                            if (lookup == null) {
//////                                lookup = createView.newAnnotation()
//////                                lookup.id = "golookup-${nerid}"
//////                                lookup.start = a.start
//////                                lookup.end = a.end
//////                                lookup.atType = Uri.LOOKUP
//////                                lookup.features.ids = []
//////                                lookup.features.type = 'GO'
//////                                lookup.features.targets = [ "${view.id}:${a.id}".toString() ]
//////                            }
//////                            lookup.features.ids << tag
//////                            if (ne == null) {
//////                                ne = createView.newAnnotation("goner-${nerid}", Uri.NE)
//////                                ne.start = a.start
//////                                ne.end = a.end
//////                                ne.atType = Uri.NE
//////                                ne.features[Features.NamedEntity.CATEGORY] = tag
//////                                ne.features.targets = [ "${view.id}:${a.id}".toString() ]
//////                                Term term = index.lookup(tag)
//////                                if (term != null) {
//////                                    ne.label = term.name
//////                                }
//////                                else {
//////                                    ne.label = tag
//////                                }
//////                            }
////                            if (tagged == null) {
////                                if (mwe == '0') {
////                                    // this is not part of a multiword expression.
////                                    tagged = newView.newAnnotation("gotag-${nerid}".toString(), TAG)
////                                    tagged.start = a.start
////                                    tagged.end = a.end
////                                    tagged.features.mwe = '0'
////                                    tagged.features.type = TAG + "#semtag"
////                                    tagged.features.targets = [ "${view.id}:${a.id}".toString() ]
////                                }
////                                else {
////                                    logger.debug "Found a MWE ${mwe}"
////                                    List mweParts = mwe.tokenize(':')
////                                    logger.trace("There are {} mwe parts", mweParts.size())
////                                    if (mweParts.size() != 3) {
////                                        logger.debug "But it is invalid: $mwe"
////                                        tagged = newView.newAnnotation("gotag-${nerid}".toString(), TAG)
////                                        tagged.start = a.start
////                                        tagged.end = a.end
////                                        tagged.features.mwe = mwe
////                                        tagged.features.type = TAG + "#semtag"
////                                        tagged.features.targets = [ "${view.id}:${a.id}".toString() ]
////                                    }
////                                    else {
////                                        tagged = mweIndex[mweParts[0]]
////                                        if (tagged == null) {
////                                            logger.debug "Found first word of a MWE"
////                                            tagged = newView.newAnnotation("gotag-${nerid}".toString(), TAG)
////                                            tagged.start = Integer.MAX_VALUE
////                                            tagged.end = Integer.MIN_VALUE
////                                            tagged.features.mwe = [ ]
////                                            tagged.features.type = TAG + "#semtag"
////                                            tagged.features.targets = [ ]
////                                            mweIndex[mweParts[0]] = tagged
////                                        }
////                                        tagged.features.mwe << mwe
////                                        tagged.features.targets << "${view.id}:${a.id}".toString()
////                                        if (a.start < tagged.start) {
////                                            logger.debug "Adjusting start"
////                                            tagged.start = a.start
////                                        }
////                                        if (tagged.end < a.end) {
////                                            logger.debug "Adjusting end"
////                                            tagged.end = a.end
////                                        }
////                                    }
////                                }
////                            }
//////                            ne.features[Features.NamedEntity.CATEGORY] << tag
////                            ++nerid
////                        }
////                    }
////                    if (tagged != null && (tagged.features.tags == null || tagged.features.tags.size() == 0)) {
////                        Map info = makeLabel(ids)
//////                        lookup.label = info.label
//////                        lookup.features.tags = info.terms
////                        tagged.label = info.label
////                        tagged.features.tags = info.terms
////                    }
////                }
////            }
////        }
//    }

//    Map makeLabel(Set ids) {
//        logger.debug("There are {} ids", ids.size())
//        Map info = [:]
//        info.terms = []
//        for (int i = 0; i < ids.size(); ++i) {
//            String id = ids[i]
//            logger.trace("Making label for {}", id)
//            Term term = index.lookup(id)
//            info.terms.add(term)
//            if (info.label == null && term != null) {
//                info.label = term.namespace.tokenize('_')[0]
//            }
//        }
//        if (info.label == null) {
//            info.label = ids[0]
//        }
//        return info
//    }

}

