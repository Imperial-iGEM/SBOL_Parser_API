import org.sbolstandard.core2.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

public class SBOLParser {
    public void generateCsv(SBOLDocument doc) throws SBOLValidationException, SBOLConversionException, IOException {

        Set<ComponentDefinition> componentDefs = doc.getRootComponentDefinitions();
        Integer numberOfRootCDs = componentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfRootCDs+" Root Component Definition(s).");

        for(ComponentDefinition cd: doc.getRootComponentDefinitions()) {

            String cdName = cd.getName();
            if(cdName == null){
                cdName = cd.getDisplayId();
            }

            System.out.println("Comoponent Definition: "+cdName);

            List<SequenceAnnotation> sequenceAnnotations = cd.getSortedSequenceAnnotations();
            List<Component> components = cd.getSortedComponents();

            if (components.isEmpty()) {
                for (SequenceAnnotation sa : sequenceAnnotations) {
                    System.out.println(sa.getName());
                }
            } else {
                for (Component c : components) {
                    ComponentDefinition componentDefinition = doc.getComponentDefinition(c.getDefinitionURI());
                    if (componentDefinition.getName() == null) {
                        System.out.println(componentDefinition.getDisplayId());
                    } else {
                        System.out.println(componentDefinition.getName());
                    }
                }
            }
        }
    }
}
