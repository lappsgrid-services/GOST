package edu.vassar.cs.lappsgrid.gost

import edu.vassar.cs.lappsgrid.gost.go.Term
import org.lappsgrid.serialization.Serializer

/**
 *
 */
class Test {

    void run() {
        InputStream stream = this.class.getResourceAsStream('/go-index.json')
        Map<String, Term> index = Serializer.parse(stream.text, HashMap)
        println "Index contains ${index.size()} items"
        Set<String> names = new HashSet<>()
        index.each { key, term ->
            names.add(term.name.tokenize()[-1])
        }

        names.sort().each { println it }
    }

    static void main(String[] args) {
        new Test().run()
    }
}
