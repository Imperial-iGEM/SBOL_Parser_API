import org.sbolstandard.core2.*;

import java.io.IOException;
import java.net.URI;

public class pCOLA_example {
    public static void main(String[] args) throws SBOLValidationException, SBOLConversionException, IOException {
        String path = "./examples/sbol_files/pCOLA-nodeA_pSal_LacO_LVA_sbol2.rdf";

        SBOLDocument doc = SBOLReader.read(path);

        ComponentDefinition gc = doc.getComponentDefinition(URI.create("https://benchling.com/sbol/s/seq-QfA1QZqRDazLZK6OVQmj"));

        for(Component comp: gc.getSortedComponents()){
            ComponentDefinition cd = doc.getComponentDefinition(comp.getDefinitionURI());
            System.out.println(cd.getName());
        }

    }
}
