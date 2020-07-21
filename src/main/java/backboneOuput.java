import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;

public class backboneOuput {
    public static SBOLDocument createBackboneOutput() throws SBOLValidationException{
        String prURI = "http://www.dummy.org/";
        String prPrefix = "pr";

        //Set up SBOL document
        SBOLDocument document = new SBOLDocument();
        document.setTypesInURIs(true);
        document.addNamespace(URI.create(prURI),prPrefix);
        document.setDefaultURIprefix(prURI);

        ComponentDefinition dummyBackbone = document.createComponentDefinition(
                "dummyBackbone",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        dummyBackbone.addRole(URI.create("http://identifiers.org/so/SO:0000755")); //plasmid_vector
        dummyBackbone.setName("dummyBackbone");
        dummyBackbone.setDescription("Dummy destination vector backbone");

        return document;
    }

    public static void main(String[] args) throws Exception{
        SBOLDocument document = createBackboneOutput();
        SBOLWriter.write(document,"examples/sbol_files/dummyBackbone.xml");
    }
}
