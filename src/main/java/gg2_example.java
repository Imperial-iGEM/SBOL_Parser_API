import org.sbolstandard.core2.*;

import java.io.IOException;
import java.net.URI;

public class gg2_example {
    public static void main(String[] args) throws SBOLValidationException, SBOLConversionException, IOException {
        String path = "./examples/sbol_files/gg2_sbol2.rdf";

        SBOLDocument doc = SBOLReader.read(path);

        ComponentDefinition cd = doc.getComponentDefinition(URI.create("http://sbols.org/Golden_Gate_2_tier_demo_1"));

        for(SequenceAnnotation sa: cd.getSortedSequenceAnnotations()){
            System.out.println(sa.getName());
        }
    }
}
