import org.sbolstandard.core2.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class SBOLParser {

    static final HashMap<Integer,String> WELLS = new HashMap<Integer, String>();
    static final Integer MAX_WELLS = 96;

    static{
        int index = 1;
        int maxRows = 8;
        int maxColumns = 12;
        char alphabet = 'A';
        for(int row=1;row<maxRows+1;row++){
            for(int column=1;column<maxColumns+1;column++){
                WELLS.put(index,Character.toString(alphabet)+Integer.toString(column));
                index++;
            }
            alphabet++;
        }
    }

    /**
     * Checks whether the SBOL Document contains topLevel. If the Top Level is not contained in the Document, a copy of the Top Level is created.
     *
     * @param doc SBOL Document containing construct designs
     * @param topLevel Top Level to be included inside the Document
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private void includeTopLevel(SBOLDocument doc, TopLevel topLevel) throws SBOLValidationException {
        if(!doc.getTopLevels().contains(topLevel)){
            doc.createCopy(topLevel);
        }
    }

    /**
     * Insert linkers between parts. The linkers LMS and LMP will be added as suffix/prefix respectively to the plasmid part.
     *
     * @param cd Root Component Definition containing current design
     * @param components List of Components in Root Component Definition
     * @param doc SBOL Document containing construct designs
     * @param linkers SBOL Document containing linkers
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private void insertLinkers(ComponentDefinition cd, List<Component> components, SBOLDocument doc, SBOLDocument linkers) throws SBOLValidationException{
        int linker_index = 1; //Counts number of linkers added
        int max_linkers = 7; //Maximum number of linkers that can be added
        int numberOfComponents = components.size();

        //Instantiate iterator for List of Components
        Iterator<Component> iterator = components.iterator();

        //Add plasmid linkers LMS and LMP to Root Component Definition
        //Possible to abstract this into a function?
        ComponentDefinition cdLMS = linkers.getComponentDefinition(URI.create("http://www.dummy.org/cd/LMS"));
        ComponentDefinition cdLMP = linkers.getComponentDefinition(URI.create("http://www.dummy.org/cd/LMP"));
        includeTopLevel(doc,cdLMS);
        includeTopLevel(doc,cdLMP);
        Component compLMS = cd.createComponent("LMS",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/LMS"));
        Component compLMP = cd.createComponent("LMP",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/LMP"));

        //Iterate through List of Components and add linkers
        while(iterator.hasNext()&&components.size()-2<max_linkers+1){
            Component c = iterator.next();
            int index = components.indexOf(c);

            //If first Component in List of Components is a plasmid, surround plasmid with LMS and LMP
            if(index==0&&c.getDefinition().getRoles().contains(URI.create("http://identifiers.org/so/SO:0000755"))){
                cd.createSequenceConstraint("lms_constraint",RestrictionType.PRECEDES,compLMS.getIdentity(),c.getIdentity());
                cd.createSequenceConstraint("backbone_constraint",RestrictionType.PRECEDES,c.getIdentity(),compLMP.getIdentity());
                cd.createSequenceConstraint("lmp_constraint",RestrictionType.PRECEDES,compLMP.getIdentity(),components.get(index+1).getIdentity());
            }
            //Checks for edge case
            //If there is no next part, the end of the list is reached and no further linkers will be added
            else if(iterator.hasNext()){
                //Check if the next part is a plasmid vector
                //If the next part is a plasmid vector, attach LMS and LMP as suffix and prefix linkers respectively to the plasmid vector
                if (components.get(index+1).getDefinition().getRoles().contains(URI.create("http://identifiers.org/so/SO:0000755"))){
                    cd.createSequenceConstraint("lms_constraint",RestrictionType.PRECEDES,compLMS.getIdentity(),components.get(index+1).getIdentity());
                    cd.createSequenceConstraint("backbone_constraint",RestrictionType.PRECEDES,components.get(index+1).getIdentity(),compLMP.getIdentity());
                    //Account for part after plasmid
                    //If the plasmid is at the end of the List of Components, there is LMP precedes the first Component in the List of Components
                    if(index+3>components.size()) {
                        cd.createSequenceConstraint("lmp_constraint",RestrictionType.PRECEDES,compLMP.getIdentity(),components.get(0).getIdentity());
                    }
                    else{
                        cd.createSequenceConstraint("lmp_constraint", RestrictionType.PRECEDES, compLMP.getIdentity(), components.get(index+2).getIdentity());
                    }
                }
                //If the next part is not a plasmid vector, simply append a linker after this part
                else{
                    Component c_next = components.get(index+1);
                    //Create linker Component Definition in Document
                    ComponentDefinition linker = linkers.getComponentDefinition(URI.create("http://www.dummy.org/cd/L"+linker_index));
                    includeTopLevel(doc,linker);
                    //Create linker Component in Root Component Definition
                    Component compLinker = cd.createComponent("L"+linker_index,AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/L"+linker_index));
                    cd.createSequenceConstraint("linker_constraint_"+linker_index+"a",RestrictionType.PRECEDES,c.getIdentity(),compLinker.getIdentity());
                    cd.createSequenceConstraint("linker_constraint_"+linker_index+"b",RestrictionType.PRECEDES,compLinker.getIdentity(),c_next.getIdentity());
                    linker_index++;
                }
            }
            else{
                break;
            }
            if(components.size()-2>max_linkers){
                System.out.println("Number of parts exceeds number of available linkers. Please include more linkers or reduce the number of parts to be assembled.");
            }
        }
    }

    /**
     * Inserts a plasmid vector for plasmid into the Root Component Definition, together with Sequence Constraints.
     *
     * @param cd Root Component Definition containing current design
     * @param components List of Components in Root Component Definition
     * @param backbone SBOL Document containing backbones
     * @param linkers SBOL Document containing linkers
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private void insertPlasmid(ComponentDefinition cd, List<Component> components, SBOLDocument doc, SBOLDocument backbone, SBOLDocument linkers) throws SBOLValidationException{
        //Get first component from the list of components
        Component firstComponent = components.get(0);

        //Instantiate Component Definitions of linkers and backbone to be inserted
        ComponentDefinition cdPlasmidBackbone = backbone.getComponentDefinition(URI.create("http://www.dummy.org/cd/dummyBackbone"));

        //Copy linkers and backbone into SBOL Document
        includeTopLevel(doc,cdPlasmidBackbone);

        //Create Components in Root Component Definition
        Component compPlasmidBackbone = cd.createComponent("dummyBackbone",AccessType.PUBLIC,URI.create("http://www.dummy.org/cd/dummyBackbone"));
//        cd.createSequenceConstraint("Constraint1",RestrictionType.PRECEDES,compLMS.getIdentity(),compPlasmidBackbone.getIdentity());
//        cd.createSequenceConstraint("Constraint2",RestrictionType.PRECEDES,compPlasmidBackbone.getIdentity(),compLMP.getIdentity());
//        cd.createSequenceConstraint("Constraint3",RestrictionType.PRECEDES,compLMP.getIdentity(),firstComponent.getIdentity());
        cd.createSequenceConstraint("backbone_insert_constraint",RestrictionType.PRECEDES,compPlasmidBackbone.getIdentity(),firstComponent.getIdentity());

        //Add plasmid backbone to List of Components
        components.add(compPlasmidBackbone);

        System.out.println("A dummy vector will be inserted.");
    }

    /**
     * Validates whether a Root Component Definition contains a plasmid.
     *
     * @param cd Root Component Definition containing current design
     * @return Returns {@code true} if Root Component Definition contains a plasmid vector, {@code false} otherwise
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private Boolean validatePlasmid(ComponentDefinition cd) throws SBOLValidationException{
                Boolean containsPlasmid = false;
        Set<Component> components = cd.getComponents();
        for(Component c: components){
            Set<URI> roles = c.getDefinition().getRoles();
            if(roles.contains(URI.create("http://identifiers.org/so/SO:0000755"))){
                System.out.println("This Component Definition contains plasmid vector: "+c.getDefinition().getName());
                containsPlasmid = true;
            }
        }
        if(!containsPlasmid){
            System.out.println("This Component Definition does not contain a plasmid vector.");
        }
        return containsPlasmid;
    }

    /**
     * Get the name of the Component from its Component Definition. If Component Definition is unnamed, get Display ID as name.
     *
     * @param c Component
     * @return Returns the Name of the Component. If Component is unnamed, returns Display ID of the Component Definition.
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private String display(Component c) throws SBOLValidationException{
        String cName = c.getDefinition().getName();
        if(cName==null){
            cName = c.getDefinition().getDisplayId();
        }
        return cName;
    }

    /**
     * Get the name of the Component Definition. If Component Definition is unnamed, get Display ID as name.
     *
     * @param cd Component Definition
     * @return Returns the Name of the Component Definition. If Component Definition is unnamed, returns Display ID of the Component Definition.
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private String display(ComponentDefinition cd) throws SBOLValidationException{
        String cdName = cd.getName();
        if(cdName==null){
            cdName = cd.getDisplayId();
        }
        return cdName;
    }

    /**
     * Get maximum number of parts contained within a Component Definition from a Set of Component Definitions
     * Maximum number of parts = Number of Components in a Component Definition (assuming no linkers) + 1 (to account for possible insertion of plasmid)
     *
     * @param componentDefinitions Set of Root Component Definitions contained in SBOL Document containing designs
     * @return Integer representing the minimum number of parts required to build all designs in the SBOL Document
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private int getMinNumberOfParts(Set<ComponentDefinition> componentDefinitions) throws SBOLValidationException{
        int minNumberOfParts = 0;
        int numberOfParts = 0;

        for(ComponentDefinition cd: componentDefinitions){
            Set<Component> c = cd.getComponents();
            numberOfParts = c.size()+1;
            if(numberOfParts>minNumberOfParts){
                minNumberOfParts = numberOfParts;
            }
        }
        return minNumberOfParts;
    }

    /**
     * Instantiates construct csv file writer with header
     *
     * @param minNumberOfParts Minimum number of parts required to build all designs in SBOL Document
     * @return FileWriter for construct csv
     * @throws IOException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private FileWriter createConstructCsvHeader(int minNumberOfParts) throws IOException{
        //Initialize FileWriter for construct csv
        FileWriter constructWriter = new FileWriter("./examples/sbol_files/constructs.csv");
        //Write Header
        constructWriter.append("Well");

        for(int i=1;i<minNumberOfParts+1;i++){
            constructWriter.append(",");
            constructWriter.append("Linker "+i);
            constructWriter.append(",");
            constructWriter.append("Part "+i);
        }
        constructWriter.append("\n");
        constructWriter.flush();
        return constructWriter;
    }

    /**
     * Generates contruct csv for DNABot using SBOL Document as input
     *
     * @param doc SBOL Document containing construct designs
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     * @throws SBOLConversionException Throws exception if any SBOL Conversion Exceptions are encountered.
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    public void generateConstructCsv(SBOLDocument doc) throws SBOLValidationException, SBOLConversionException, IOException{
       //Instantiate well index
        int well_index = 1;

        //Obtain set of Root Component Definitions
        Set<ComponentDefinition> componentDefs = doc.getRootComponentDefinitions();

        //Display number of Root Component Definitions
        Integer numberOfRootCDs = componentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfRootCDs+" Root Component Definition(s).");

        //Get minimum number of parts required to build all the constructs contained in the SBOL Document
        int minNumberOfParts = getMinNumberOfParts(componentDefs);
        System.out.println("The minimum number of parts required to build all the constructs in this SBOL Document is: "+minNumberOfParts);

        //Import Linkers
        SBOLDocument linkers = SBOLReader.read("examples/sbol_files/linker_parts.xml");

        //Import Dummy Backbone
        SBOLDocument dummyBackbone = SBOLReader.read("examples/sbol_files/dummyBackbone.xml");

        //Instantiate construct csv with header
        FileWriter constructCsv = createConstructCsvHeader(minNumberOfParts);

        //Iterate through each Root Component Definition from the Set of Component Definitions
        for(ComponentDefinition cd:componentDefs){
            //Display name of Root Component Definition
            String cdName = display(cd);
            System.out.println("Component Definition: "+cdName);

            //Get sorted list of Components
            List<Component> components = cd.getSortedComponents();

            //Check if Root Component Definition contains a plasmid vector
            Boolean containsPlasmid = validatePlasmid(cd);

            //If Root Component Definition does not contain a plasmid vector, insert plasmid vector at the start of the list of Components
            if(!containsPlasmid){
                insertPlasmid(cd,components,doc,dummyBackbone,linkers);
            }

            //Insert Linkers
            insertLinkers(cd,components,doc,linkers);

            //Fetch new List of Components
            components = cd.getSortedComponents();

            //Currently, each Root Component Definition only includes 1 construct
            //The subsequent code for writing construct csv may need changes to write Combinatorial Derivations

            //Write Well
            constructCsv.append(WELLS.get(well_index));

            //Append List of Components into csv
            for(Component c: components){
                constructCsv.append(",");
                constructCsv.append(display(c));
                System.out.println(display(c));
            }
            well_index++;

            //Break out if the plate is full
            //Currently DNABot can only generate one full plate of constructs
            //In future, add ability to generate multiple construct csvs to accommodate overflow
            if(well_index>MAX_WELLS){
                System.out.println("Plate full. Additional constructs will not be included.");
                break;
            }

            constructCsv.flush();
            constructCsv.close();
        }
    }

    /**
     * Generates construct csv for a specific Component Definition for DNABot using SBOL Document and Component Definition URI as input
     *
     * @param doc SBOL Document containing construct designs
     * @param cdURI
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     * @throws SBOLConversionException Throws exception if any SBOL Conversion Exceptions are encountered.
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    public void generateConstructCsv(SBOLDocument doc, URI cdURI) throws SBOLValidationException, SBOLConversionException, IOException{

    }
}
