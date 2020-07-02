import org.sbolstandard.core2.*;

import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URI;

public class gene_cassette_example {
    public static void main(String[] args) throws SBOLValidationException, SBOLConversionException, IOException {
        String path = "./examples/sbol_files/gene_cassette_sbol2.rdf";

        SBOLDocument doc = SBOLReader.read(path);

        ComponentDefinition gc = doc.getComponentDefinition(URI.create("http://sbolstandard.org/examples/Design"));

        for(Component comp: gc.getSortedComponents()){
            ComponentDefinition cd = doc.getComponentDefinition(comp.getDefinitionURI());
            System.out.println(cd.getDisplayId());
        }

    }

}
