package edu.vassar.cs.lappsgrid.gost.go

import org.lappsgrid.serialization.Serializer

/**
 *
 */
class Index {

    final static String TERM = '[Term]'
    final static String ID = 'id: '
    final static String NAME = 'name: '
    final static String NAMESPACE = 'namespace: '

    final int ID_OFFSET = ID.length()
    final int NAME_OFFSET = NAME.length()
    final int NAMESPACE_OFFSET = NAMESPACE.length()

    Map<String,Term> index //= new HashMap<>()

    Index() {
        load()
    }

    Term lookup(String id) {
        String key = id
        int dot = key.indexOf('.')
        if (dot > 0) {
            key = key.substring(0, dot)
        }
        Term t = index.get(key)
        if (t == null) {
            t = new Term()
            t.name = id
            t.namespace = id
        }
        t.id = id
        return t
    }

    private void load() {
        InputStream stream = this.class.getResourceAsStream('/go-index.json')
        if (stream == null) {
            return
        }
        index = Serializer.parse(stream.text, HashMap)
    }

    void parse() {
        InputStream stream = this.class.getResourceAsStream('/go.obo')
        if (stream == null) {
            println "Unable log load the ontology."
            return
        }
        index = new HashMap<>()
        Term term = null
        stream.eachLine { String line ->
            if (TERM == line) {
                if (term != null) {
                    index.put(term.id, term)
                }
                term = new Term()
            }
            else if (line.startsWith(ID)) {
                if (term == null) {
                    throw new IOException("ID found outside a term!")
                }
                term.id = line.substring(ID_OFFSET)
            }
            else if (line.startsWith(NAME))  {
                if (term == null) {
                    throw new IOException("ID found outside a term!")
                }
                term.name = line.substring(NAME_OFFSET)
            }
            else if (line.startsWith(NAMESPACE)) {
                if (term == null) {
                    throw new IOException("ID found outside a term!")
                }
                term.namespace = line.substring(NAMESPACE_OFFSET)
            }
        }

        File outfile = new File("go-index.json")
        outfile.text = Serializer.toPrettyJson(index)
        println "Wrote ${outfile.path}"
    }

    static void main(String[] args) {
        new Index().test()
    }
}
