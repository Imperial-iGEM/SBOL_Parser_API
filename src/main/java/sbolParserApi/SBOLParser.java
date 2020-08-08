package sbolParserApi;

import javassist.compiler.ast.Variable;
import org.sbolstandard.core2.*;
import org.sbolstandard.core2.Collection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
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
     * Returns several non-overlapping subsets of a set of Component Definitions
     *
     * @param allComponentDefs Set of Component Definitions
     * @param maxSize Maximum number of constructs to be assembled in one run
     * @param numberOfSubsets Number of runs to be executed
     * @return Non-overlapping subsets of a set of Component Definitions
     */
    private static HashSet<HashSet<ComponentDefinition>> getRandomSubsets(HashSet<ComponentDefinition> allComponentDefs, int maxSize, int numberOfSubsets){
        ArrayList<ComponentDefinition> listOfAllComponentDefs = new ArrayList<ComponentDefinition>(allComponentDefs);
        Collections.shuffle(listOfAllComponentDefs);
        HashSet<HashSet<ComponentDefinition>> subsets = new HashSet<>();
        //Check if number of designs is less than one plate
        if(allComponentDefs.size()<maxSize){
            maxSize = allComponentDefs.size();
            HashSet<ComponentDefinition> subset = new HashSet<>(listOfAllComponentDefs.subList(0, maxSize));
            subsets.add(subset);
            System.out.println("Number of designs is less than size specified for one run. All designs will be included. (Number of designs: " +maxSize+")");
            return subsets;
        }
        //Check if number of designs can fill all plates
        else if(allComponentDefs.size()<maxSize*(numberOfSubsets-1)){
            numberOfSubsets = allComponentDefs.size()/maxSize;
            while(allComponentDefs.size()>maxSize){
                HashSet<ComponentDefinition> subset = new HashSet<>(listOfAllComponentDefs.subList(0,maxSize));
                subsets.add(subset);
                allComponentDefs.removeAll(subset);
                listOfAllComponentDefs.removeAll(subset);
            }
            subsets.add(allComponentDefs);
            System.out.println("Number of designs is insufficient for number of runs specified. Only "+numberOfSubsets+" of runs will be executed.");
            return subsets;
        }
        else{
            for(int i=0;i<numberOfSubsets;i++){
                HashSet<ComponentDefinition> subset = new HashSet<>(listOfAllComponentDefs.subList(0,maxSize));
                subsets.add(subset);
                allComponentDefs.removeAll(subset);
                listOfAllComponentDefs.removeAll(subset);
            }
            System.out.println("Number of designs: "+maxSize*numberOfSubsets);
            System.out.println("Number of runs: "+numberOfSubsets);
            return subsets;
        }
    }

    /**
     * Retrieves a random subset of a set of Component Definitions.
     *
     * @param allComponentDefs HashSet of Component Definitions from which subset is drawn
     * @param size Size of subset
     * @return Subset of Component Definitions
     */
    private static HashSet<ComponentDefinition> getRandomSubset(HashSet<ComponentDefinition> allComponentDefs, int size){
        ArrayList<ComponentDefinition> listOfAllComponentDefs = new ArrayList<ComponentDefinition>(allComponentDefs);
        Collections.shuffle(listOfAllComponentDefs);
        if(allComponentDefs.size()<size){
            size = allComponentDefs.size();
            System.out.println("Number of designs is less than size specified. All designs will be included. (Number of designs: " +size+")");
            return new HashSet<ComponentDefinition>(listOfAllComponentDefs.subList(0, size));
        }
        else{
            return new HashSet<ComponentDefinition>(listOfAllComponentDefs.subList(0, size));
        }
    }

    /**
     * Returns a flattened List of Components (unsorted) from a hierarchical design.
     *
     * @param componentDefinition Component Definition defining Top Level design
     * @return List of Components
     */
    private static List<Component> getAllComponents(ComponentDefinition componentDefinition){
        List<Component> allComponents = new ArrayList<Component>();
        ArrayDeque<Component> deque = new ArrayDeque<Component>(componentDefinition.getComponents());

        while(!deque.isEmpty()){
            Component component = deque.pop();
            ComponentDefinition cdOfComponent = component.getDefinition();
            if(!cdOfComponent.getComponents().isEmpty()){
                for(Component c: cdOfComponent.getComponents()){
                    deque.push(c);
                }
            }
            else{
                allComponents.add(component);
            }
        }
        return allComponents;
    }

    /**
     * Filters a (Hash)Set of Component Definitions. Current implementation removes designs with repeated parts.
     *
     * @param allComponentDefs HashSet of Component Definitions to be filtered.
     * @return Filtered HashSet of Component Definitions.
     */
    private static HashSet<ComponentDefinition> filter(HashSet<ComponentDefinition> allComponentDefs){
        HashSet<ComponentDefinition> filteredComponentDefs = new HashSet<>();
        for(ComponentDefinition cd: allComponentDefs){
            filteredComponentDefs.add(cd);
            List<Component> allComponents = getAllComponents(cd);
            HashSet<ComponentDefinition> definitionHashSet = new HashSet<>();
            for(Component c: allComponents){
                if(!definitionHashSet.add(c.getDefinition())){
                    filteredComponentDefs.remove(cd);
                    break;
                }
            }
        }
        return filteredComponentDefs;
    }

    /*
     The following section contains code which was adapted from:
     M. Zhang, J. McLaughlin, A. Wipat, and C. Myers, SBOLDesigner 2: An Intuitive Tool for Structural Genetic Design, in ACS Synthetic Biology 6(7): 1150-1160, July 21, 2017.
     Github: https://github.com/SynBioDex/SBOLDesigner
     */

    public static String getUniqueDisplayId(ComponentDefinition comp, CombinatorialDerivation derivation,
                                            String displayId, String version, String dataType, SBOLDocument design) throws SBOLValidationException {
        // if can get using some displayId, then try the next number
        switch (dataType) {
            case "CD":
                for (int i = 1; true; i++) {
                    if (i == 1 && design.getComponentDefinition(displayId, version) == null) {
                        return displayId;
                    }
                    if (design.getComponentDefinition(displayId + i, version) == null) {
                        return displayId + i;
                    }
                }
            case "SequenceAnnotation":
                for (int i = 1; true; i++) {
                    if (i == 1 && comp.getSequenceAnnotation(displayId) == null) {
                        return displayId;
                    }
                    if (comp.getSequenceAnnotation(displayId + i) == null) {
                        return displayId + i;
                    }
                }
            case "SequenceConstraint":
                for (int i = 1; true; i++) {
                    if (i == 1 && comp.getSequenceConstraint(displayId) == null) {
                        return displayId;
                    }
                    if (comp.getSequenceConstraint(displayId + i) == null) {
                        return displayId + i;
                    }
                }
            case "Component":
                for (int i = 1; true; i++) {
                    if (i == 1 && comp.getComponent(displayId) == null) {
                        return displayId;
                    }
                    if (comp.getComponent(displayId + i) == null) {
                        return displayId + i;
                    }
                }
            case "Sequence":
                for (int i = 1; true; i++) {
                    if (i == 1 && design.getSequence(displayId, version) == null) {
                        return displayId;
                    }
                    if (design.getSequence(displayId + i, version) == null) {
                        return displayId + i;
                    }
                }
            case "Range":
                test: for (int i = 1; true; i++) {
                    for (SequenceAnnotation sa : comp.getSequenceAnnotations()) {
                        if (i == 1 && sa.getLocation(displayId) != null) {
                            continue test;
                        }
                        if (sa.getLocation(displayId + i) != null) {
                            continue test;
                        }
                    }
                    // This will always return Range, Range2, Range3... etc,
                    // skipping Range1
                    return i == 1 ? displayId : displayId + i;
                }
            case "CombinatorialDerivation":
                for (int i = 1; true; i++) {
                    if (i == 1 && design.getCombinatorialDerivation(displayId, version) == null) {
                        return displayId;
                    }
                    if (design.getCombinatorialDerivation(displayId + i, version) == null) {
                        return displayId + i;
                    }
                }
            case "VariableComponent":
                for (int i = 1; true; i++) {
                    if (i == 1 && derivation.getVariableComponent(displayId) == null) {
                        return displayId;
                    }
                    if (derivation.getVariableComponent(displayId + i) == null) {
                        return displayId + i;
                    }
                }
            default:
                throw new IllegalArgumentException();
        }
    }

    private static ComponentDefinition createTemplateCopy(SBOLDocument doc, CombinatorialDerivation derivation)
            throws SBOLValidationException {
        ComponentDefinition template = derivation.getTemplate();

        String uniqueId = getUniqueDisplayId(null, null, template.getDisplayId() + "_Var",
                template.getVersion(), "CD", doc);
        //ComponentDefinition copy = (ComponentDefinition) doc.createCopy(template, uniqueId, template.getVersion());
        ComponentDefinition copy = doc.createComponentDefinition(uniqueId, template.getVersion(), template.getTypes());
        copy.setRoles(template.getRoles());
        Component prev = null;
        Component curr;
        for(Component c : template.getSortedComponents())
        {
            curr = copy.createComponent(c.getDisplayId(), c.getAccess(), c.getDefinitionURI());
            if(prev != null)
            {
                uniqueId = getUniqueDisplayId(copy, null,
                        copy.getDisplayId() + "_SequenceConstraint", null, "SequenceConstraint", null);
                copy.createSequenceConstraint(uniqueId, RestrictionType.PRECEDES, prev.getIdentity(),
                        curr.getIdentity());
            }
            prev = curr;
        }
        copy.addWasDerivedFrom(template.getIdentity());
        copy.addWasDerivedFrom(derivation.getIdentity());
        for (Component component : copy.getComponents()) {
            component.addWasDerivedFrom(template.getComponent(component.getDisplayId()).getIdentity());
        }

        //copy.clearSequenceAnnotations();

        return copy;
    }

    private static HashSet<ComponentDefinition> enumerate(SBOLDocument doc, CombinatorialDerivation derivation)
            throws SBOLValidationException {
        HashSet<ComponentDefinition> parents = new HashSet<>();
        parents.add(createTemplateCopy(doc, derivation));
        for (VariableComponent vc : derivation.getVariableComponents()) {
            HashSet<ComponentDefinition> newParents = new HashSet<>();
            for (ComponentDefinition parent : parents) {
                for (HashSet<ComponentDefinition> children : group(collectVariants(doc, vc), vc.getOperator())) {
                    String varDisplayId = concatenateChildrenDisplayId(children);

                    // create copy of parent
                    String uniqueId = getUniqueDisplayId(null, null, parent.getDisplayId() + "_" + varDisplayId,
                            parent.getVersion(), "CD", doc);
                    ComponentDefinition newParent = (ComponentDefinition) doc.createCopy(parent, uniqueId, "1");

                    //Try - set newParent to existing Component Definition
                    newParent = doc.getComponentDefinition(parent.getDisplayId()+"_"+varDisplayId,"1");

                    // add children
                    ComponentDefinition template = derivation.getTemplate();
                    addChildren(template, template.getComponent(vc.getVariableURI()), newParent, children);

                    // add to newParents
                    newParents.add(newParent);
                }
            }
            //Try - Delete old parents
            for(ComponentDefinition cd: parents){
                doc.removeComponentDefinition(cd);
            }
            parents = newParents;
        }
        return parents;
    }

    //Try - concatenate Display ID of children of parent
    private static String concatenateChildrenDisplayId(HashSet<ComponentDefinition> children){
        StringBuilder concDisplayId = new StringBuilder();
        for(ComponentDefinition child: children){
            concDisplayId.append(child.getDisplayId());
        }
        return concDisplayId.toString();
    }


    private static void addChildren(ComponentDefinition originalTemplate, Component originalComponent,
                                    ComponentDefinition newParent, HashSet<ComponentDefinition> children) throws SBOLValidationException {
        Component newComponent = newParent.getComponent(originalComponent.getDisplayId());
        newComponent.addWasDerivedFrom(originalComponent.getIdentity());

        if (children.isEmpty()) {
            removeConstraintReferences(newParent, newComponent);
            for (SequenceAnnotation sa : newParent.getSequenceAnnotations()) {
                if (sa.isSetComponent() && sa.getComponentURI().equals(newComponent.getIdentity())) {
                    newParent.removeSequenceAnnotation(sa);
                }
            }
            newParent.removeComponent(newComponent);
            return;
        }

        boolean first = true;
        for (ComponentDefinition child : children) {
            if (first) {
                // take over the definition of newParent's version of the
                // original component
                newComponent.setDefinition(child.getIdentity());
                first = false;
            } else {
                // create a new component
                String uniqueId = getUniqueDisplayId(newParent, null, child.getDisplayId() + "_Component",
                        "1", "Component", null);
                Component link = newParent.createComponent(uniqueId, AccessType.PUBLIC, child.getIdentity());
                link.addWasDerivedFrom(originalComponent.getIdentity());

                // create a new 'prev precedes link' constraint
                Component oldPrev = getBeforeComponent(originalTemplate, originalComponent);
                if (oldPrev != null) {
                    Component newPrev = newParent.getComponent(oldPrev.getDisplayId());
                    if (newPrev != null) {
                        uniqueId = getUniqueDisplayId(newParent, null,
                                newParent.getDisplayId() + "_SequenceConstraint", null, "SequenceConstraint", null);
                        newParent.createSequenceConstraint(uniqueId, RestrictionType.PRECEDES, newPrev.getIdentity(),
                                link.getIdentity());
                    }
                }

                // create a new 'link precedes next' constraint
                Component oldNext = getAfterComponent(originalTemplate, originalComponent);
                if (oldNext != null) {
                    Component newNext = newParent.getComponent(oldNext.getDisplayId());
                    if (newNext != null) {
                        uniqueId = getUniqueDisplayId(newParent, null,
                                newParent.getDisplayId() + "_SequenceConstraint", null, "SequenceConstraint", null);
                        newParent.createSequenceConstraint(uniqueId, RestrictionType.PRECEDES, link.getIdentity(),
                                newNext.getIdentity());
                    }
                }
            }
        }
    }

    private static void removeConstraintReferences(ComponentDefinition newParent, Component newComponent) throws SBOLValidationException {
        Component subject = null;
        Component object = null;
        for (SequenceConstraint sc : newParent.getSequenceConstraints()) {
            if (sc.getSubject().equals(newComponent)) {
                object = sc.getObject();
                //If we know what the new subject of this sequence constraint should be, modify it
                if(subject != null) {
                    sc.setSubject(subject.getIdentity());
                    object = null;
                    subject = null;
                }else {//else remove it
                    newParent.removeSequenceConstraint(sc);
                }
            }
            if(sc.getObject().equals(newComponent)){
                subject = sc.getSubject();
                //If we know what the new object of this sequence constraint should be, modify it
                if(object != null) {
                    sc.setObject(object.getIdentity());
                    object = null;
                    subject = null;
                }else {//else remove it
                    newParent.removeSequenceConstraint(sc);
                }
            }
        }
    }

    private static Component getBeforeComponent(ComponentDefinition template, Component component) {
        for (SequenceConstraint sc : template.getSequenceConstraints()) {
            if (sc.getRestriction().equals(RestrictionType.PRECEDES) && sc.getObject().equals(component)) {
                return sc.getSubject();
            }
        }
        return null;
    }

    private static Component getAfterComponent(ComponentDefinition template, Component component) {
        for (SequenceConstraint sc : template.getSequenceConstraints()) {
            if (sc.getRestriction().equals(RestrictionType.PRECEDES) && sc.getSubject().equals(component)) {
                return sc.getObject();
            }
        }
        return null;
    }

    private static HashSet<HashSet<ComponentDefinition>> group(HashSet<ComponentDefinition> variants,
                                                               OperatorType operator) {
        HashSet<HashSet<ComponentDefinition>> groups = new HashSet<>();

        for (ComponentDefinition CD : variants) {
            HashSet<ComponentDefinition> group = new HashSet<>();
            group.add(CD);
            groups.add(group);
        }

        if (operator == OperatorType.ONE) {
            return groups;
        }

        if (operator == OperatorType.ZEROORONE) {
            groups.add(new HashSet<>());
            return groups;
        }

        groups.clear();
        generateCombinations(groups, variants.toArray(new ComponentDefinition[0]), 0, new HashSet<>());
        if (operator == OperatorType.ONEORMORE) {
            return groups;
        }

        if (operator == OperatorType.ZEROORMORE) {
            groups.add(new HashSet<>());
            return groups;
        }

        throw new IllegalArgumentException(operator.toString() + " operator not supported");
    }

    /**
     * Generates all combinations except the empty set.
     */
    private static void generateCombinations(HashSet<HashSet<ComponentDefinition>> groups,
                                             ComponentDefinition[] variants, int i, HashSet<ComponentDefinition> set) {
        if (i == variants.length) {
            if (!set.isEmpty()) {
                groups.add(set);
            }
            return;
        }

        HashSet<ComponentDefinition> no = new HashSet<>(set);
        generateCombinations(groups, variants, i + 1, no);

        HashSet<ComponentDefinition> yes = new HashSet<>(set);
        yes.add(variants[i]);
        generateCombinations(groups, variants, i + 1, yes);
    }

    private static HashSet<ComponentDefinition> collectVariants(SBOLDocument doc, VariableComponent vc)
            throws SBOLValidationException {
        HashSet<ComponentDefinition> variants = new HashSet<>();

        // add all variants
        variants.addAll(vc.getVariants());

        // add all variants from variantCollections
        for (Collection c : vc.getVariantCollections()) {
            for (TopLevel tl : c.getMembers()) {
                if (tl instanceof ComponentDefinition) {
                    variants.add((ComponentDefinition) tl);
                }
            }
        }

        // add all variants from variantDerivations
        for (CombinatorialDerivation derivation : vc.getVariantDerivations()) {
            variants.addAll(enumerate(doc, derivation));
        }
        return variants;
    }

    /*
    End of adapted section.
     */

    /**
     * Alternative method for obtaining root Component Definitions, treats variants as child Components of corresponding template Component Definition
     *
     * @param doc SBOL Document containing designs
     * @return Set of root Component Definitions
     */
    private Set<ComponentDefinition> getRootCds(SBOLDocument doc){
        Set<ComponentDefinition> componentDefs = doc.getComponentDefinitions();
        for (ComponentDefinition componentDefinition : doc.getComponentDefinitions()) {
            for (Component component : componentDefinition.getComponents()) {
                ComponentDefinition childDefinition = component.getDefinition();
                if (childDefinition != null && componentDefs.contains(childDefinition)) {
                    componentDefs.remove(childDefinition);
                }
            }
        }
        for (CombinatorialDerivation combinatorialDerivation : doc.getCombinatorialDerivations()){
            for (VariableComponent variableComponent : combinatorialDerivation.getVariableComponents()){
                for(ComponentDefinition childDefinition : variableComponent.getVariants()){
                    if (childDefinition != null && componentDefs.contains(childDefinition)) {
                        componentDefs.remove(childDefinition);
                    }
                }
            }
        }

        return componentDefs;
    }

    /**
     * Checks whether a given Component is a Linker against a collection of linkers specified in an SBOL Document from which the linkers were obtained.
     *
     * @param c Component to check
     * @param linkers SBOL Document containing linkers that were inserted into the current design
     * @return {@code true} if component is a linker, {@code false} otherwise
     */
    private Boolean isLinker(Component c, SBOLDocument linkers){
        boolean isLinker = false;
        //Get Set of all Component Definitions in linkers Document
        Set<ComponentDefinition> cdLinkers = linkers.getComponentDefinitions();
        if(cdLinkers.contains(c.getDefinition())){
            isLinker = true;
        }
        return isLinker;
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
        }
        if(components.size()-2>max_linkers){
            System.out.println("Number of parts exceeds number of available linkers. Please include more linkers or reduce the number of parts to be assembled.");
        }
    }

    /**
     * Inserts a plasmid vector for plasmid into the Root Component Definition, together with Sequence Constraints.
     *
     * @param cd Root Component Definition containing current design
     * @param components List of Components in Root Component Definition
     * @param backbone SBOL Document containing backbones
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     */
    private void insertPlasmid(ComponentDefinition cd, List<Component> components, SBOLDocument doc, SBOLDocument backbone) throws SBOLValidationException{
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
        boolean containsPlasmid = false;
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
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    private void createConstructCsvHeader(FileWriter constructWriter, int minNumberOfParts) throws IOException{
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
    }

    /**
     * Instantiates parts/linkers csv file with headers
     *
     * @return FileWriter for parts/linkers csv
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    private void createPartsLinkersCsvHeader(FileWriter partsLinkersWriter) throws IOException{
        partsLinkersWriter.append("Part/linker");
        partsLinkersWriter.append(",");
        partsLinkersWriter.append("Well");
        partsLinkersWriter.append(",");
        partsLinkersWriter.append("Part concentration (ng/uL)");
        partsLinkersWriter.append("\n");
    }

    /**
     * Generates several construct csv for DNABot using SBOL Document (with Combinatorial Derivations) as input
     *
     * @param doc SBOL Document containing combinatorial construct designs
     * @param constructType Accepts COMPONENT_DEFINITION or COMBINATORIAL_DERIVATION as input
     * @param constructURI URI of Combinatorial Derivation to enumerate
     * @param maxSize Maximum number of designs in a single run
     * @param numberOfRuns Number of runs to be executed
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     * @throws SBOLConversionException Throws exception if any SBOL Conversion Exceptions are encountered.
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    public void generateCsv(SBOLDocument doc, String constructType, String constructURI, int maxSize, int numberOfRuns) throws SBOLValidationException, SBOLConversionException, IOException{
        //Instantiate well index
        int well_index = 1;

        //Instantiate set of all Component Definitions
        HashSet<ComponentDefinition> allComponentDefs = new HashSet<ComponentDefinition>();

        //Cases
        switch (constructType) {
            case "COMPONENT_DEFINITION":
                //Add Root Component Definitions to all Component Definitions
                allComponentDefs.add(doc.getComponentDefinition(URI.create(constructURI)));
                break;
            case "COMBINATORIAL_DERIVATION":
                //Enumerate Combinatorial Derivations and add to set of all Component Definitions
                System.out.println("Enumerating Combinatorial Derivations...");
                HashSet<ComponentDefinition> designs = enumerate(doc,doc.getCombinatorialDerivation(URI.create(constructURI)));
                System.out.println("Completed.");
                allComponentDefs.addAll(designs);
                break;
        }

        //Remove designs with repeated parts
        System.out.println("Removing designs with repeated parts...");
        allComponentDefs = filter(allComponentDefs);

        //Display number of Component Definitions to be constructed
        int numberOfDesigns = allComponentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfDesigns+" designs to be constructed.");

        //Select number of experimental runs (determined by number of construct csv) and how many designs per run
        HashSet<HashSet<ComponentDefinition>> runs = getRandomSubsets(allComponentDefs,maxSize,numberOfRuns);

        for(HashSet<ComponentDefinition> run: runs) {
            //Set/Reset well index
            well_index = 1;

            //Find minimum number of parts required for construct csv
            int minNumberOfParts = getMinNumberOfParts(run);

            //Timestamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            //Initialize FileWriter for construct csv
            FileWriter constructCsv = new FileWriter("./examples/sbol_files/constructs"+timestamp.getTime()+".csv");
            //Add parts/linkers csv header
            createConstructCsvHeader(constructCsv, minNumberOfParts);

            //Initialize FileWriter for parts/linkers csv
            FileWriter partsLinkersCsv = new FileWriter("./examples/sbol_files/parts_linkers"+timestamp.getTime()+".csv");
            //Add parts/linkers csv header
            createPartsLinkersCsvHeader(partsLinkersCsv);

            //Import Linkers
            SBOLDocument linkers = SBOLReader.read("examples/sbol_files/linker_parts.xml");

            //Import Dummy Backbone
            SBOLDocument dummyBackbone = SBOLReader.read("examples/sbol_files/dummyBackbone.xml");

            //Instantiate List of all components across all designs
            HashSet<Component> allComponents = new HashSet<Component>();

            //Iterate through each Root Component Definition from the Set of Component Definitions
            for (ComponentDefinition cd : run) {
                //Display name of Root Component Definition
                String cdName = display(cd);
                System.out.println("Component Definition: " + cdName);

                //Get sorted list of Components
                List<Component> components = cd.getSortedComponents();

                //Check if Root Component Definition contains a plasmid vector
                Boolean containsPlasmid = validatePlasmid(cd);

                //If Root Component Definition does not contain a plasmid vector, insert plasmid vector at the start of the list of Components
                if (!containsPlasmid) {
                    insertPlasmid(cd, components, doc, dummyBackbone);
                }

                //Insert Linkers
                //Need to implement validation on whether construct already contains linkers
                insertLinkers(cd, components, doc, linkers);

                //Fetch new List of Components
                components = cd.getSortedComponents();

                //Generate construct csv
                //Currently, each Root Component Definition only includes 1 construct
                //The subsequent code for writing construct csv may need changes to write Combinatorial Derivations

                //Write Well
                constructCsv.append(WELLS.get(well_index));

                //Append List of Components into construct csv
                for (Component c : components) {
                    constructCsv.append(",");
                    constructCsv.append(display(c));
                    System.out.println(display(c));
                }
                constructCsv.append("\n");
                well_index++;

                //Break out if the plate is full
                //Currently DNABot can only generate one full plate of constructs
                //In future, add ability to generate multiple construct csvs to accommodate overflow
                if (well_index > MAX_WELLS) {
                    System.out.println("Plate full. Additional constructs will not be included.");
                    break;
                }

                //Add parts/linkers to List of all Components
                allComponents.addAll(components);
            }

            constructCsv.flush();
            constructCsv.close();

            //Reset well index for parts/linkers csv
            well_index = 1;

            //Sort all Components alphabetically
            TreeSet<String> sortedAllComponents = new TreeSet<String>();
            for (Component c : allComponents) {
                //If Component is a linker, include prefix and suffix entries for the linker
                if (isLinker(c, linkers)) {
                    sortedAllComponents.add(display(c) + "-S");
                    sortedAllComponents.add(display(c) + "-P");
                } else {
                    sortedAllComponents.add(display(c));
                }
            }

            //Write parts/linkers to csv
            for (String s : sortedAllComponents) {
                partsLinkersCsv.append(s);
                partsLinkersCsv.append(",");
                partsLinkersCsv.append(WELLS.get(well_index));
                partsLinkersCsv.append("\n");
                well_index++;
            }

            partsLinkersCsv.flush();
            partsLinkersCsv.close();
        }
    }

    /**
     * Generates a construct csv for DNABot using SBOL Document (with Combinatorial Derivations) as input
     *
     * @param doc SBOL Document containing combinatorial construct designs
     * @param constructType Accepts COMPONENT_DEFINITION or COMBINATORIAL_DERIVATION as input
     * @param constructURI URI of Combinatorial Derivation or Component Definition to assemble
     * @param maxSize Maximum number of designs in a single run
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     * @throws SBOLConversionException Throws exception if any SBOL Conversion Exceptions are encountered.
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    public void generateCsv(SBOLDocument doc, String constructType, String constructURI, int maxSize) throws SBOLValidationException, SBOLConversionException, IOException{
        //Instantiate well index
        int well_index = 1;

        //Instantiate set of all Component Definitions
        HashSet<ComponentDefinition> allComponentDefs = new HashSet<ComponentDefinition>();

        //Cases
        switch (constructType){
            case "COMPONENT_DEFINITION":
                allComponentDefs.add(doc.getComponentDefinition(URI.create(constructURI)));
                break;
            case "COMBINATORIAL_DERIVATION":
                //Enumerate Combinatorial Derivations and add to set of all Component Definitions
                System.out.println("Enumerating Combinatorial Derivations...");
                HashSet<ComponentDefinition> designs = enumerate(doc,doc.getCombinatorialDerivation(URI.create(constructURI)));
                System.out.println("Completed.");
                allComponentDefs.addAll(designs);
                break;
        }

        //Remove designs with repeated parts
        System.out.println("Removing designs with repeated parts...");
        allComponentDefs = filter(allComponentDefs);

        //Display number of Component Definitions to be constructed
        int numberOfDesigns = allComponentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfDesigns+" designs to be constructed.");

        //Select number of experimental runs (determined by number of construct csv) and how many designs per run
        HashSet<ComponentDefinition> run = getRandomSubset(allComponentDefs,maxSize);

        //Find minimum number of parts required for construct csv
        int minNumberOfParts = getMinNumberOfParts(run);

        //Timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //Initialize FileWriter for construct csv
        FileWriter constructCsv = new FileWriter("./examples/sbol_files/constructs"+timestamp.getTime()+".csv");
        //Add parts/linkers csv header
        createConstructCsvHeader(constructCsv, minNumberOfParts);

        //Initialize FileWriter for parts/linkers csv
        FileWriter partsLinkersCsv = new FileWriter("./examples/sbol_files/parts_linkers"+timestamp.getTime()+".csv");
        //Add parts/linkers csv header
        createPartsLinkersCsvHeader(partsLinkersCsv);

        //Import Linkers
        SBOLDocument linkers = SBOLReader.read("examples/sbol_files/linker_parts.xml");

        //Import Dummy Backbone
        SBOLDocument dummyBackbone = SBOLReader.read("examples/sbol_files/dummyBackbone.xml");

        //Instantiate List of all components across all designs
        HashSet<Component> allComponents = new HashSet<Component>();

        //Iterate through each Root Component Definition from the Set of Component Definitions
        for (ComponentDefinition cd : run) {
            //Display name of Root Component Definition
            String cdName = display(cd);
            System.out.println("Component Definition: " + cdName);

            //Get sorted list of Components
            List<Component> components = cd.getSortedComponents();

            //Check if Root Component Definition contains a plasmid vector
            Boolean containsPlasmid = validatePlasmid(cd);

            //If Root Component Definition does not contain a plasmid vector, insert plasmid vector at the start of the list of Components
            if (!containsPlasmid) {
                insertPlasmid(cd, components, doc, dummyBackbone);
            }

            //Insert Linkers
            //Need to implement validation on whether construct already contains linkers
            insertLinkers(cd, components, doc, linkers);

            //Fetch new List of Components
            components = cd.getSortedComponents();

            //Generate construct csv
            //Currently, each Root Component Definition only includes 1 construct
            //The subsequent code for writing construct csv may need changes to write Combinatorial Derivations

            //Write Well
            constructCsv.append(WELLS.get(well_index));

            //Append List of Components into construct csv
            for (Component c : components) {
                constructCsv.append(",");
                constructCsv.append(display(c));
                System.out.println(display(c));
            }
            constructCsv.append("\n");
            well_index++;

            //Break out if the plate is full
            //Currently DNABot can only generate one full plate of constructs
            //In future, add ability to generate multiple construct csvs to accommodate overflow
            if (well_index > MAX_WELLS) {
                System.out.println("Plate full. Additional constructs will not be included.");
                break;
            }

            //Add parts/linkers to List of all Components
            allComponents.addAll(components);
        }

        constructCsv.flush();
        constructCsv.close();

        //Reset well index for parts/linkers csv
        well_index = 1;

        //Sort all Components alphabetically
        TreeSet<String> sortedAllComponents = new TreeSet<String>();
        for (Component c : allComponents) {
            //If Component is a linker, include prefix and suffix entries for the linker
            if (isLinker(c, linkers)) {
                sortedAllComponents.add(display(c) + "-S");
                sortedAllComponents.add(display(c) + "-P");
            } else {
                sortedAllComponents.add(display(c));
            }
        }

        //Write parts/linkers to csv
        for (String s : sortedAllComponents) {
            partsLinkersCsv.append(s);
            partsLinkersCsv.append(",");
            partsLinkersCsv.append(WELLS.get(well_index));
            partsLinkersCsv.append("\n");
            well_index++;
        }

        partsLinkersCsv.flush();
        partsLinkersCsv.close();
    }

    /**
     * Generates a construct csv for all Component Definitions or all Combinatorial Derivations for DNABot using SBOL Document as input
     *
     * @param doc SBOL Document containing construct designs
     * @param constructType Accepts COMPONENT_DEFINITION or COMBINATORIAL_DERIVATION or BOTH as input
     * @param maxSize Maximum number of designs in a single run
     * @param numberOfRuns Number of runs to be executed
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     * @throws SBOLConversionException Throws exception if any SBOL Conversion Exceptions are encountered.
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    public void generateCsv(SBOLDocument doc, String constructType, int maxSize, int numberOfRuns) throws SBOLValidationException, SBOLConversionException, IOException{
        //Instantiate well index
        int well_index = 1;

        //Instantiate set of all Component Definitions
        HashSet<ComponentDefinition> allComponentDefs = new HashSet<ComponentDefinition>();

        //Obtain set of Combinatorial Derivations
        Set<CombinatorialDerivation> combinatorialDerivations = doc.getCombinatorialDerivations();

        //Obtain set of Root Component Definitions
        Set<ComponentDefinition> componentDefs = getRootCds(doc);

        //Cases
        switch (constructType){
            case "COMPONENT_DEFINITION":
                //Remove templates from Root Component Definitions
                for(CombinatorialDerivation derivation: combinatorialDerivations){
                    ComponentDefinition template = derivation.getTemplate();
                    componentDefs.remove(template);
                }                //Add Root Component Definitions to all Component Definitions
                allComponentDefs.addAll(componentDefs);
                break;
            case "COMBINATORIAL_DERIVATION":
                //Enumerate Combinatorial Derivations and add to set of all Component Definitions
                System.out.println("Enumerating Combinatorial Derivations...");
                for(CombinatorialDerivation combDeriv: combinatorialDerivations){
                    HashSet<ComponentDefinition> designs = enumerate(doc,combDeriv);
                    allComponentDefs.addAll(designs);
                }
                System.out.println("Completed.");
                break;
            case "BOTH":
                //Remove templates from Root Component Definitions
                for(CombinatorialDerivation derivation: combinatorialDerivations){
                    ComponentDefinition template = derivation.getTemplate();
                    componentDefs.remove(template);
                }
                //Add Root Component Definitions to all Component Definitions
                allComponentDefs.addAll(componentDefs);
                //Enumerate Combinatorial Derivations and add to set of all Component Definitions
                System.out.println("Enumerating Combinatorial Derivations...");
                for(CombinatorialDerivation combDeriv: combinatorialDerivations){
                    HashSet<ComponentDefinition> designs = enumerate(doc,combDeriv);
                    allComponentDefs.addAll(designs);
                }
                System.out.println("Completed.");
                break;
        }

        //Remove designs with repeated parts
        System.out.println("Removing designs with repeated parts...");
        allComponentDefs = filter(allComponentDefs);

        //Display number of Component Definitions to be constructed
        int numberOfDesigns = allComponentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfDesigns+" designs to be constructed.");

        //Select number of experimental runs (determined by number of construct csv) and how many designs per run
        HashSet<HashSet<ComponentDefinition>> runs = getRandomSubsets(allComponentDefs,maxSize,numberOfRuns);

        for(HashSet<ComponentDefinition> run: runs) {
            //Set/Reset well index
            well_index = 1;

            //Find minimum number of parts required for construct csv
            int minNumberOfParts = getMinNumberOfParts(run);

            //Timestamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            //Initialize FileWriter for construct csv
            FileWriter constructCsv = new FileWriter("./examples/sbol_files/constructs"+timestamp.getTime()+".csv");
            //Add parts/linkers csv header
            createConstructCsvHeader(constructCsv, minNumberOfParts);

            //Initialize FileWriter for parts/linkers csv
            FileWriter partsLinkersCsv = new FileWriter("./examples/sbol_files/parts_linkers"+timestamp.getTime()+".csv");
            //Add parts/linkers csv header
            createPartsLinkersCsvHeader(partsLinkersCsv);

            //Import Linkers
            SBOLDocument linkers = SBOLReader.read("examples/sbol_files/linker_parts.xml");

            //Import Dummy Backbone
            SBOLDocument dummyBackbone = SBOLReader.read("examples/sbol_files/dummyBackbone.xml");

            //Instantiate List of all components across all designs
            HashSet<Component> allComponents = new HashSet<Component>();

            //Iterate through each Root Component Definition from the Set of Component Definitions
            for (ComponentDefinition cd : run) {
                //Display name of Root Component Definition
                String cdName = display(cd);
                System.out.println("Component Definition: " + cdName);

                //Get sorted list of Components
                List<Component> components = cd.getSortedComponents();

                //Check if Root Component Definition contains a plasmid vector
                Boolean containsPlasmid = validatePlasmid(cd);

                //If Root Component Definition does not contain a plasmid vector, insert plasmid vector at the start of the list of Components
                if (!containsPlasmid) {
                    insertPlasmid(cd, components, doc, dummyBackbone);
                }

                //Insert Linkers
                //Need to implement validation on whether construct already contains linkers
                insertLinkers(cd, components, doc, linkers);

                //Fetch new List of Components
                components = cd.getSortedComponents();

                //Generate construct csv
                //Currently, each Root Component Definition only includes 1 construct
                //The subsequent code for writing construct csv may need changes to write Combinatorial Derivations

                //Write Well
                constructCsv.append(WELLS.get(well_index));

                //Append List of Components into construct csv
                for (Component c : components) {
                    constructCsv.append(",");
                    constructCsv.append(display(c));
                    System.out.println(display(c));
                }
                constructCsv.append("\n");
                well_index++;

                //Break out if the plate is full
                //Currently DNABot can only generate one full plate of constructs
                //In future, add ability to generate multiple construct csvs to accommodate overflow
                if (well_index > MAX_WELLS) {
                    System.out.println("Plate full. Additional constructs will not be included.");
                    break;
                }

                //Add parts/linkers to List of all Components
                allComponents.addAll(components);
            }

            constructCsv.flush();
            constructCsv.close();

            //Reset well index for parts/linkers csv
            well_index = 1;

            //Sort all Components alphabetically
            TreeSet<String> sortedAllComponents = new TreeSet<String>();
            for (Component c : allComponents) {
                //If Component is a linker, include prefix and suffix entries for the linker
                if (isLinker(c, linkers)) {
                    sortedAllComponents.add(display(c) + "-S");
                    sortedAllComponents.add(display(c) + "-P");
                } else {
                    sortedAllComponents.add(display(c));
                }
            }

            //Write parts/linkers to csv
            for (String s : sortedAllComponents) {
                partsLinkersCsv.append(s);
                partsLinkersCsv.append(",");
                partsLinkersCsv.append(WELLS.get(well_index));
                partsLinkersCsv.append("\n");
                well_index++;
            }

            partsLinkersCsv.flush();
            partsLinkersCsv.close();
        }
    }

    /**
     * Generates a construct csv for all Component Definitions or all Combinatorial Derivations for DNABot using SBOL Document as input
     *
     * @param doc SBOL Document containing construct designs
     * @param constructType Accepts COMPONENT_DEFINITION or COMBINATORIAL_DERIVATION or BOTH as input
     * @param maxSize Maximum number of designs in a single run
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     * @throws SBOLConversionException Throws exception if any SBOL Conversion Exceptions are encountered.
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    public void generateCsv(SBOLDocument doc, String constructType, int maxSize) throws SBOLValidationException, SBOLConversionException, IOException{
        //Instantiate well index
        int well_index = 1;

        //Instantiate set of all Component Definitions
        HashSet<ComponentDefinition> allComponentDefs = new HashSet<ComponentDefinition>();

        //Obtain set of Combinatorial Derivations
        Set<CombinatorialDerivation> combinatorialDerivations = doc.getCombinatorialDerivations();

        //Obtain set of Root Component Definitions
        Set<ComponentDefinition> componentDefs = getRootCds(doc);

        //Cases
        switch (constructType){
            case "COMPONENT_DEFINITION":
                //Remove templates from Root Component Definitions
                for(CombinatorialDerivation derivation: combinatorialDerivations){
                    ComponentDefinition template = derivation.getTemplate();
                    componentDefs.remove(template);
                }
                //Add Root Component Definitions to all Component Definitions
                allComponentDefs.addAll(componentDefs);
                break;
            case "COMBINATORIAL_DERIVATION":
                //Enumerate Combinatorial Derivations and add to set of all Component Definitions
                System.out.println("Enumerating Combinatorial Derivations...");
                for(CombinatorialDerivation combDeriv: combinatorialDerivations){
                    HashSet<ComponentDefinition> designs = enumerate(doc,combDeriv);
                    allComponentDefs.addAll(designs);
                }
                System.out.println("Completed.");
                break;
            case "BOTH":
                //Remove templates from Root Component Definitions
                for(CombinatorialDerivation derivation: combinatorialDerivations){
                    ComponentDefinition template = derivation.getTemplate();
                    componentDefs.remove(template);
                }
                //Add Root Component Definitions to all Component Definitions
                allComponentDefs.addAll(componentDefs);
                //Enumerate Combinatorial Derivations and add to set of all Component Definitions
                System.out.println("Enumerating Combinatorial Derivations...");
                for(CombinatorialDerivation combDeriv: combinatorialDerivations){
                    HashSet<ComponentDefinition> designs = enumerate(doc,combDeriv);
                    allComponentDefs.addAll(designs);
                }
                System.out.println("Completed.");
                break;
        }

        //Remove designs with repeated parts
        System.out.println("Removing designs with repeated parts...");
        allComponentDefs = filter(allComponentDefs);

        //Display number of Component Definitions to be constructed
        int numberOfDesigns = allComponentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfDesigns+" designs to be constructed.");

        //Select number of experimental runs (determined by number of construct csv) and how many designs per run
        HashSet<ComponentDefinition> run = getRandomSubset(allComponentDefs,maxSize);

        //Find minimum number of parts required for construct csv
        int minNumberOfParts = getMinNumberOfParts(run);

        //Timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //Initialize FileWriter for construct csv
        FileWriter constructCsv = new FileWriter("./examples/sbol_files/constructs"+timestamp.getTime()+".csv");
        //Add parts/linkers csv header
        createConstructCsvHeader(constructCsv, minNumberOfParts);

        //Initialize FileWriter for parts/linkers csv
        FileWriter partsLinkersCsv = new FileWriter("./examples/sbol_files/parts_linkers"+timestamp.getTime()+".csv");
        //Add parts/linkers csv header
        createPartsLinkersCsvHeader(partsLinkersCsv);

        //Import Linkers
        SBOLDocument linkers = SBOLReader.read("examples/sbol_files/linker_parts.xml");

        //Import Dummy Backbone
        SBOLDocument dummyBackbone = SBOLReader.read("examples/sbol_files/dummyBackbone.xml");

        //Instantiate List of all components across all designs
        HashSet<Component> allComponents = new HashSet<Component>();

        //Iterate through each Root Component Definition from the Set of Component Definitions
        for (ComponentDefinition cd : run) {
            //Display name of Root Component Definition
            String cdName = display(cd);
            System.out.println("Component Definition: " + cdName);

            //Get sorted list of Components
            List<Component> components = cd.getSortedComponents();

            //Check if Root Component Definition contains a plasmid vector
            Boolean containsPlasmid = validatePlasmid(cd);

            //If Root Component Definition does not contain a plasmid vector, insert plasmid vector at the start of the list of Components
            if (!containsPlasmid) {
                insertPlasmid(cd, components, doc, dummyBackbone);
            }

            //Insert Linkers
            //Need to implement validation on whether construct already contains linkers
            insertLinkers(cd, components, doc, linkers);

            //Fetch new List of Components
            components = cd.getSortedComponents();

            //Generate construct csv
            //Currently, each Root Component Definition only includes 1 construct
            //The subsequent code for writing construct csv may need changes to write Combinatorial Derivations

            //Write Well
            constructCsv.append(WELLS.get(well_index));

            //Append List of Components into construct csv
            for (Component c : components) {
                constructCsv.append(",");
                constructCsv.append(display(c));
                System.out.println(display(c));
            }
            constructCsv.append("\n");
            well_index++;

            //Break out if the plate is full
            //Currently DNABot can only generate one full plate of constructs
            //In future, add ability to generate multiple construct csvs to accommodate overflow
            if (well_index > MAX_WELLS) {
                System.out.println("Plate full. Additional constructs will not be included.");
                break;
            }

            //Add parts/linkers to List of all Components
            allComponents.addAll(components);
        }

        constructCsv.flush();
        constructCsv.close();

        //Reset well index for parts/linkers csv
        well_index = 1;

        //Sort all Components alphabetically
        TreeSet<String> sortedAllComponents = new TreeSet<String>();
        for (Component c : allComponents) {
            //If Component is a linker, include prefix and suffix entries for the linker
            if (isLinker(c, linkers)) {
                sortedAllComponents.add(display(c) + "-S");
                sortedAllComponents.add(display(c) + "-P");
            } else {
                sortedAllComponents.add(display(c));
            }
        }

        //Write parts/linkers to csv
        for (String s : sortedAllComponents) {
            partsLinkersCsv.append(s);
            partsLinkersCsv.append(",");
            partsLinkersCsv.append(WELLS.get(well_index));
            partsLinkersCsv.append("\n");
            well_index++;
        }

        partsLinkersCsv.flush();
        partsLinkersCsv.close();
    }

    /**
     * Generates a construct csv for DAMPLab MoClo Assembly using SBOL Document (with Combinatorial Derivations) as input
     *
     * @param doc SBOL Document containing combinatorial construct designs
     * @param constructType Accepts COMPONENT_DEFINITION or COMBINATORIAL_DERIVATION as input
     * @param constructURI URI of Combinatorial Derivation or Component Definition to assemble
     * @param maxSize Maximum number of designs in a single run
     * @throws SBOLValidationException Throws exception if any SBOL Validation Exceptions are encountered.
     * @throws SBOLConversionException Throws exception if any SBOL Conversion Exceptions are encountered.
     * @throws IOException Throws exception if any IO Exceptions are encountered.
     */
    public void generateMoCloCsv(SBOLDocument doc, String constructType, String constructURI, int maxSize) throws SBOLValidationException, SBOLConversionException, IOException{
        //Instantiate well index
        int well_index = 1;

        //Instantiate set of all Component Definitions
        HashSet<ComponentDefinition> allComponentDefs = new HashSet<ComponentDefinition>();

        //Cases
        switch (constructType){
            case "COMPONENT_DEFINITION":
                allComponentDefs.add(doc.getComponentDefinition(URI.create(constructURI)));
                break;
            case "COMBINATORIAL_DERIVATION":
                //Enumerate Combinatorial Derivations and add to set of all Component Definitions
                System.out.println("Enumerating Combinatorial Derivations...");
                HashSet<ComponentDefinition> designs = enumerate(doc,doc.getCombinatorialDerivation(URI.create(constructURI)));
                System.out.println("Completed.");
                allComponentDefs.addAll(designs);
                break;
        }

        //Remove designs with repeated parts
        System.out.println("Removing designs with repeated parts...");
        allComponentDefs = filter(allComponentDefs);

        //Display number of Component Definitions to be constructed
        int numberOfDesigns = allComponentDefs.size();
        System.out.println("This SBOL Document contains "+numberOfDesigns+" designs to be constructed.");

        //Select number of experimental runs (determined by number of construct csv) and how many designs per run
        HashSet<ComponentDefinition> run = getRandomSubset(allComponentDefs,maxSize);

        //Find minimum number of parts required for construct csv
        int minNumberOfParts = getMinNumberOfParts(run);

        //Timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //Initialize FileWriter for construct csv
        FileWriter constructCsv = new FileWriter("./examples/sbol_files/constructs"+timestamp.getTime()+".csv");
        //Add parts/linkers csv header
        createConstructCsvHeader(constructCsv, minNumberOfParts);

        //Initialize FileWriter for parts/linkers csv
        FileWriter partsLinkersCsv = new FileWriter("./examples/sbol_files/parts_linkers"+timestamp.getTime()+".csv");
        //Add parts/linkers csv header
        createPartsLinkersCsvHeader(partsLinkersCsv);

        //Import Linkers
        SBOLDocument linkers = SBOLReader.read("examples/sbol_files/linker_parts.xml");

        //Import Dummy Backbone
        SBOLDocument dummyBackbone = SBOLReader.read("examples/sbol_files/dummyBackbone.xml");

        //Instantiate List of all components across all designs
        HashSet<Component> allComponents = new HashSet<Component>();

        //Iterate through each Root Component Definition from the Set of Component Definitions
        for (ComponentDefinition cd : run) {
            //Display name of Root Component Definition
            String cdName = display(cd);
            System.out.println("Component Definition: " + cdName);

            //Get sorted list of Components
            List<Component> components = cd.getSortedComponents();

            //Check if Root Component Definition contains a plasmid vector
            Boolean containsPlasmid = validatePlasmid(cd);

            //If Root Component Definition does not contain a plasmid vector, insert plasmid vector at the start of the list of Components
            if (!containsPlasmid) {
                insertPlasmid(cd, components, doc, dummyBackbone);
            }

            //Insert Linkers
            //Need to implement validation on whether construct already contains linkers
            insertLinkers(cd, components, doc, linkers);

            //Fetch new List of Components
            components = cd.getSortedComponents();

            //Generate construct csv
            //Currently, each Root Component Definition only includes 1 construct
            //The subsequent code for writing construct csv may need changes to write Combinatorial Derivations

            //Write Well
            constructCsv.append(WELLS.get(well_index));

            //Append List of Components into construct csv
            for (Component c : components) {
                constructCsv.append(",");
                constructCsv.append(display(c));
                System.out.println(display(c));
            }
            constructCsv.append("\n");
            well_index++;

            //Break out if the plate is full
            //Currently DNABot can only generate one full plate of constructs
            //In future, add ability to generate multiple construct csvs to accommodate overflow
            if (well_index > MAX_WELLS) {
                System.out.println("Plate full. Additional constructs will not be included.");
                break;
            }

            //Add parts/linkers to List of all Components
            allComponents.addAll(components);
        }

        constructCsv.flush();
        constructCsv.close();

        //Reset well index for parts/linkers csv
        well_index = 1;

        //Sort all Components alphabetically
        TreeSet<String> sortedAllComponents = new TreeSet<String>();
        for (Component c : allComponents) {
            //If Component is a linker, include prefix and suffix entries for the linker
            if (isLinker(c, linkers)) {
                sortedAllComponents.add(display(c) + "-S");
                sortedAllComponents.add(display(c) + "-P");
            } else {
                sortedAllComponents.add(display(c));
            }
        }

        //Write parts/linkers to csv
        for (String s : sortedAllComponents) {
            partsLinkersCsv.append(s);
            partsLinkersCsv.append(",");
            partsLinkersCsv.append(WELLS.get(well_index));
            partsLinkersCsv.append("\n");
            well_index++;
        }

        partsLinkersCsv.flush();
        partsLinkersCsv.close();
    }
}