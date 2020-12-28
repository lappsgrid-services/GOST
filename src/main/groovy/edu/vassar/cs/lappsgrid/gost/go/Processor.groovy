package edu.vassar.cs.lappsgrid.gost.go

import groovy.util.logging.Slf4j
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.vocabulary.Metadata

import static org.lappsgrid.discriminator.Discriminators.Uri
import org.lappsgrid.serialization.lif.Contains
import org.lappsgrid.serialization.lif.View

/**
 *
 */
@Slf4j("logger")
abstract class Processor {

    static final String TAG = Uri.SEMANTIC_TAG
    static final String USASTAG = TAG + "#USAS"
    static final String PRODUCER = "GOST"

    enum State { waiting, processing }

    Container container
    String text
    View tokenView
    View tagView

    Index index
    State state
    int count
    int offset
    int nextId

    Processor(Container container) {
        this.container = container
        this.text = container.text
        this.tokenView = container.newView()
        tokenView.addContains(Uri.TOKEN, 'GOST', "token")
        Contains contains = tokenView.addContains(Uri.POS, 'GOST', "pos")
        contains.put(Metadata.Token.POS_TAG_SET, Uri.TAGS_POS_CLAWS7)
        tokenView.addContains(Uri.LEMMA, 'GOST', 'lemma')

        createView()
        tagView.addContains(TAG, PRODUCER, "semtag")
        contains = tagView.addContains(USASTAG, PRODUCER, 'http://ucrel.lancs.ac.uk/usas/semtags.txt')
        contains.put(Metadata.SemanticTag.TAG_SET, Uri.TAGS_SEM_USAS)
        contains.dependency(tokenView.id, Uri.TOKEN)

        index = new Index()
        state = State.waiting
        count = offset = nextId = 0
    }

    Map makeLabel(Set ids) {
        Map info = [:]
        info.terms = []
        for (int i = 0; i < ids.size(); ++i) {
            String id = ids[i]
            Term term = index.lookup(id)
            info.terms.add(term)
            if (info.label == null && term != null) {
                info.label = term.namespace.tokenize('_')[0]
            }
        }
        if (info.label == null) {
            info.label = ids[0]
        }
        return info
    }

    abstract void createView()
    abstract Annotation createAnnotation(String tag)

    Annotation newAnnotation(Annotation a, String tag) {
        Annotation ann = createAnnotation(tag)
        ann.start = a.start
        ann.end = a.end
        ann.features.targets = []
        ann.features.mwe = []
        return ann
    }

    Annotation newSemanticTag(Annotation a, String tag) {
        Annotation ann = tagView.newAnnotation("semtag-" + nextId, TAG)
        ann.start = a.start
        ann.end = a.end
        ann.features.with {
            put('type', Discriminators.Uri.TAGS_SEM_USAS)
            put('targets', [])
            put('mwe', [])
        }
        ann.features.targets = []
        ann.features.mwe = []
        return ann
    }

    void process(String input) {
        Map<String, Annotation> mweIndex = [:]

        input.eachLine { String line ->
            if (state == State.waiting) {
                if (line.startsWith('S_BEGIN')) {
                    state = State.processing
                }
            }
            else if (state == State.processing) {
                if (line.startsWith('S_END')) {
                    state = State.waiting
                }
                else {
                    Annotation token = tokenView.newAnnotation()
                    token.atType = Discriminators.Uri.TOKEN
                    token.id = "gost-${++count}"
                    String[] parts = line.split('\t')
                    String word = parts[0]
                    token.features['word'] = word
                    token.features['lemma'] = parts[1]
                    token.features['pos'] = parts[2]
                    List<String> tags = parts[3].tokenize()
                    String mwe = parts[4]

                    token.start = text.indexOf(word, offset)
                    token.end = token.start + word.length()
                    offset = token.end

                    // Now check if we have any GO annotations
                    Set ids = [] as HashSet
                    Annotation tagged = null
                    tags.each { tag ->
                        ids.add(tag)
                        if (tagged == null) {
                            List exprs = mwe.tokenize(':')
                            if (exprs.size() == 3) {
                                String key = exprs[0]
                                tagged = mweIndex[key]
                                if (tagged == null) {
                                    if (tag.startsWith('GO:')) {
                                        tagged = newAnnotation(token, tag)
                                    }
                                    else {
                                        tagged = newSemanticTag(token, tag)
                                    }
                                    mweIndex[key] = tagged
                                }
                                tagged.features.mwe << word
                            }
                            else if (tag.startsWith('GO:')) {
                                tagged = newAnnotation(token, tag)
                            }
                            else {
                                tagged = newSemanticTag(token, tag)
                            }
                            tagged.features.targets << "${tokenView.id}:${token.id}".toString()
                            if (token.start < tagged.start) {
                                logger.debug "Adjusting start"
                                tagged.start = token.start
                            }
                            if (tagged.end < token.end) {
                                logger.debug "Adjusting end"
                                tagged.end = token.end
                            }
                        }
                        ++nextId
                    }
                    if (tagged != null && (tagged.features.tags == null || tagged.features.tags.size() == 0)) {
                        Map info = makeLabel(ids)
//                        lookup.label = info.label
//                        lookup.features.tags = info.terms
                        tagged.label = info.label
                        tagged.features.tags = info.terms
                    }
                }
            }
        }
    }
}
