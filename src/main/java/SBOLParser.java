import org.sbolstandard.core2.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class SBOLParser {
    public void generateCsv(SBOLDocument doc, String cdURI) throws SBOLValidationException, SBOLConversionException, IOException {
        ComponentDefinition cd = doc.getComponentDefinition(URI.create(cdURI));

        List<SequenceAnnotation> sequenceAnnotations = cd.getSortedSequenceAnnotations();
        List<Component> components = cd.getSortedComponents();

        if(components.isEmpty()){
            for(SequenceAnnotation sa: sequenceAnnotations){
                System.out.println(sa.getName());
            }
        }
        else{
            for(Component c: components){
                ComponentDefinition componentDefinition = doc.getComponentDefinition(c.getDefinitionURI());
                if(componentDefinition.getName()==null){
                    System.out.println(componentDefinition.getDisplayId());
                }
                else{
                    System.out.println(componentDefinition.getName());
                }
            }
        }
    }
}
