import com.sun.org.apache.xpath.internal.operations.Bool;
import org.sbolstandard.core2.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class SBOLParser {
    public void generateCsv(SBOLDocument doc) throws SBOLValidationException, SBOLConversionException, IOException {

        //Obtain Root Component Definitions
        Set<ComponentDefinition> componentDefs = doc.getRootComponentDefinitions();

        //Display number of Root Component Definitions
        Integer numberOfRootCDs = componentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfRootCDs+" Root Component Definition(s).");

        //Import linkers
        SBOLDocument linkers = SBOLReader.read("examples/sbol_files/linker_parts.xml");
        doc.createCopy(linkers);

        //Import dummy backbone
        SBOLDocument dummyBackbone = SBOLReader.read("examples/sbol_files/dummyBackbone.xml");
        doc.createCopy(dummyBackbone);

        //Iterate through each Component Definition in the set componentDefs
        for(ComponentDefinition cd: componentDefs) {

            //Get name of Component Definition
            String cdName = cd.getName();
            //If Component Definition is unnamed, get Display ID
            if(cdName == null){
                cdName = cd.getDisplayId();
            }

            System.out.println("Component Definition: "+cdName);

            //Get list of sorted Sequence Annotations and Components
            List<SequenceAnnotation> sequenceAnnotations = cd.getSortedSequenceAnnotations();
            List<Component> components = cd.getSortedComponents();
            //Get first component (definition) in design
            Component firstComponent = components.get(0);

            //Check if Component Definition contains plasmid
            boolean containsPlasmid = false;
            for(Component c: components){
                Set<URI> roles = c.getDefinition().getRoles();
                if(roles.contains(URI.create("http://identifiers.org/so/SO:0000755"))){
                    System.out.println("This Component Definition contains plasmid vector: "+c.getDefinition().getName());
                    containsPlasmid = true;
                }
            }
            //If Component Definition does not contain plasmid, insert dummy plasmid, linkers and display message
            if(!containsPlasmid){
                //Insert LMS, backbone, LMP
                Component LMS = cd.createComponent("LMS",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/LMS"));
                Component backbone = cd.createComponent("dummyBackbone",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/dummyBackbone"));
                Component LMP = cd.createComponent("LMP",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/LMP"));
                cd.createSequenceConstraint("Constraint1",RestrictionType.PRECEDES,LMS.getIdentity(),backbone.getIdentity());
                cd.createSequenceConstraint("Constraint2",RestrictionType.PRECEDES,backbone.getIdentity(),LMP.getIdentity());
                cd.createSequenceConstraint("Constraint3",RestrictionType.PRECEDES,LMP.getIdentity(),firstComponent.getIdentity());
                System.out.println("This Component Definition does not contain a plasmid vector. A dummy vector will be inserted.");
                //Insert linkers between parts - to refactor to abstract insertion of linkers between parts as a function
                //Currently inserting preset linkers - need to be able to either 1. Seek user input 2. Derive linker from context
                Iterator<Component> iterator = components.iterator();
                while(iterator.hasNext()){
                    Component c = iterator.next();
                    int index = components.indexOf(c);
                    if(iterator.hasNext()){ //Why can't a while loop be used? Throws error sbol-10202
                        Component c_next = components.get(index+1);
                        Component linker = cd.createComponent("L"+(index+1), AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/L"+(index+1)));
                        linker.setName(linker.getDisplayId());
                        cd.createSequenceConstraint("Constraint"+index+3,RestrictionType.PRECEDES,c.getIdentity(),linker.getIdentity());
                        cd.createSequenceConstraint("Constraint"+index+4,RestrictionType.PRECEDES,linker.getIdentity(),c_next.getIdentity());
                    }
                    else{
                        break;
                    }
                }
            }
            //If component has plasmid, surround plasmid with LMS and LMP linkers, then insert linkers between parts
            else{
                Iterator<Component> iterator = components.iterator();
                int linker_index = 1;
                Component LMS = cd.createComponent("LMS",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/LMS"));
                Component LMP = cd.createComponent("LMP",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/LMP"));
                while(iterator.hasNext()){
                    Component c = iterator.next();
                    int index = components.indexOf(c);
                    //Check if first component is a plasmid
                    //If first component is a plasmid, surround plasmid with LMS and LMP
                    if(index==0&&c.getDefinition().getRoles().contains(URI.create("http://identifiers.org/so/SO:0000755"))){
                        cd.createSequenceConstraint("ConstraintLMS",RestrictionType.PRECEDES,LMS.getIdentity(),c.getIdentity());
                        cd.createSequenceConstraint("ConstraintBackbone",RestrictionType.PRECEDES,c.getIdentity(),LMP.getIdentity());
                        cd.createSequenceConstraint("ConstraintLMP",RestrictionType.PRECEDES,LMP.getIdentity(),components.get(index+1).getIdentity());
                    }
                    else{
                        if(iterator.hasNext()){
                            //Check if next component is a plasmid
                            if (components.get(index+1).getDefinition().getRoles().contains(URI.create("http://identifiers.org/so/SO:0000755"))){
                                cd.createSequenceConstraint("ConstraintLMS",RestrictionType.PRECEDES,LMS.getIdentity(),components.get(index+1).getIdentity());
                                cd.createSequenceConstraint("ConstraintBackbone",RestrictionType.PRECEDES,components.get(index+1).getIdentity(),LMP.getIdentity());
                                cd.createSequenceConstraint("ConstraintLMP",RestrictionType.PRECEDES,LMP.getIdentity(),components.get(index+2).getIdentity());
                            }
                            else{
                                Component c_next = components.get(index+1);
                                Component linker = cd.createComponent("L"+linker_index, AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/L"+linker_index));
                                linker.setName(linker.getDisplayId());
                                cd.createSequenceConstraint("Constraint"+linker_index+"a",RestrictionType.PRECEDES,c.getIdentity(),linker.getIdentity());
                                cd.createSequenceConstraint("Constraint"+linker_index+"b",RestrictionType.PRECEDES,linker.getIdentity(),c_next.getIdentity());
                                linker_index++;
                            }
                        }
                        else{
                            break;
                        }
                    }
                    //Catch if more than 7 parts to join since we can only use L1-L7
                    if(linker_index>7){
                        System.out.println("Error: More than 7 linkers will be used.");
                        break;
                    }
                }
            }

            //Fetch new list of Components
            components = cd.getSortedComponents();

            //Check if Components list is empty (especially for Component Definitions with only Sequence Annotations)
            //In future, generate Component from Sequence Annotations if converted from Genbank, or ignore these types of files (perform validation)
            if (components.isEmpty()) {
                //Derive names of parts from Sequence Annotations
                for (SequenceAnnotation sa : sequenceAnnotations) {
                    System.out.println(sa.getName());
                }
            } else {
                for (Component c : components) {
                    //Derive name of Component from Component Definition
                    ComponentDefinition componentDefinition = c.getDefinition();
                    //If Component Definition is unnamed, derive name from Display ID
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
