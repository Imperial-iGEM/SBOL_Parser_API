import org.sbolstandard.core2.*;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;

public class linkerOutput {
    public static SBOLDocument createLinkerOutput() throws SBOLValidationException{

        String prURI = "http://www.dummy.org/";
        String prPrefix = "pr";

        //Set up SBOL document
        SBOLDocument document = new SBOLDocument();
        document.setTypesInURIs(true);
        document.addNamespace(URI.create(prURI),prPrefix);
        document.setDefaultURIprefix(prURI);

        //Define Linker Sequence, Component Definition
        //Linker: 5'-6bp_RE_Scar + Adaptor + Homology + Adaptor + 4bp_RE_Scar-3'
        //Linker-S: 5'-6bp_RE_Scar + Adaptor + ss_Oligo-3'
        //Linker-P: 5'-4bp_RE_Scar + Adaptor + ss_Oligo-3'

        //Neutral Linkers
        //L1
        Sequence seqL1 = document.createSequence(
                "L1",
                "",
                "GGCTCGTTACTTACGACACTCCGAGACAGTCAGAGGGTATTTATTGAACTAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1_S = document.createSequence(
                "L1_Suffix",
                "",
                "GGCTCGttacttacgacactccgagacagtcagagggta",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1_P = document.createSequence(
                "L1_Prefix",
                "",
                "GGACtagttcaataaataccctctgactgtctcggag",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L1 = document.createComponentDefinition(
                "L1",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1.setName("L1");
        L1.setDescription("Neutral Linker");
        L1.addSequence(seqL1.getIdentity());

        ComponentDefinition L1_S = document.createComponentDefinition(
                "L1_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1_S.setName("L1_Suffix");
        L1_S.setDescription("Neutral Linker Suffix");
        L1_S.addSequence(seqL1_S.getIdentity());

        ComponentDefinition L1_P = document.createComponentDefinition(
                "L1_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1_P.setName("L1_Prefix");
        L1_P.setDescription("Neutral Linker Prefix");
        L1_P.addSequence(seqL1_P.getIdentity());

        //L2
        Sequence seqL2 = document.createSequence(
                "L2",
                "",
                "GGCTCGATCGGTGTGAAAAGTCAGTATCCAGTCGTGTAGTTCTTATTACCTGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2_S = document.createSequence(
                "L2_Suffix",
                "",
                "GGCTCGatcggtgtgaaaagtcagtatccagtcgtgtag",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2_P = document.createSequence(
                "L2_Prefix",
                "",
                "GGACaggtaataagaactacacgactggatactgact",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L2 = document.createComponentDefinition(
                "L2",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2.setName("L2");
        L2.setDescription("Neutral Linker");
        L2.addSequence(seqL2.getIdentity());

        ComponentDefinition L2_S = document.createComponentDefinition(
                "L2_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2_S.setName("L2_Suffix");
        L2_S.setDescription("Neutral Linker Suffix");
        L2_S.addSequence(seqL2_S.getIdentity());

        ComponentDefinition L2_P = document.createComponentDefinition(
                "L2_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2_P.setName("L2_Prefix");
        L2_P.setDescription("Neutral Linker Prefix");
        L2_P.addSequence(seqL2_P.getIdentity());

        //L3
        Sequence seqL3 = document.createSequence(
                "L3",
                "",
                "GGCTCGATCACGGCACTACACTCGTTGCTTTATCGGTATTGTTATTACAGAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL3_S = document.createSequence(
                "L3_Suffix",
                "",
                "GGCTCGatcacggcactacactcgttgctttatcggtat",
                Sequence.IUPAC_DNA
        );

        Sequence seqL3_P = document.createSequence(
                "L3_Prefix",
                "",
                "GGACtctgtaataacaataccgataaagcaacgagtg",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L3 = document.createComponentDefinition(
                "L3",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L3.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L3.setName("L3");
        L3.setDescription("Neutral Linker");
        L3.addSequence(seqL3.getIdentity());

        ComponentDefinition L3_S = document.createComponentDefinition(
                "L3_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L3_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L3_S.setName("L3_Suffix");
        L3_S.setDescription("Neutral Linker Suffix");
        L3_S.addSequence(seqL3_S.getIdentity());

        ComponentDefinition L3_P = document.createComponentDefinition(
                "L3_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L3_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L3_P.setName("L3_Prefix");
        L3_P.setDescription("Neutral Linker Prefix");
        L3_P.addSequence(seqL3_P.getIdentity());

        //L4
        Sequence seqL4 = document.createSequence(
                "L4",
                "",
                "GGCTCGACCCACGACTATTGACTGCTCTGAGAAAGTTGATTGTTACGATTAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL4_S = document.createSequence(
                "L4_Suffix",
                "",
                "GGCTCGacccacgactattgactgctctgagaaagttga",
                Sequence.IUPAC_DNA
        );

        Sequence seqL4_P = document.createSequence(
                "L4_Prefix",
                "",
                "GGACtaatcgtaacaatcaactttctcagagcagtca",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L4 = document.createComponentDefinition(
                "L4",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L4.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L4.setName("L4");
        L4.setDescription("Neutral Linker");
        L4.addSequence(seqL4.getIdentity());

        ComponentDefinition L4_S = document.createComponentDefinition(
                "L4_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L4_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L4_S.setName("L4_Suffix");
        L4_S.setDescription("Neutral Linker Suffix");
        L4_S.addSequence(seqL4_S.getIdentity());

        ComponentDefinition L4_P = document.createComponentDefinition(
                "L4_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L4_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L4_P.setName("L4_Prefix");
        L4_P.setDescription("Neutral Linker Prefix");
        L4_P.addSequence(seqL4_P.getIdentity());

        //L5
        Sequence seqL5 = document.createSequence(
                "L5",
                "",
                "GGCTCGAGAAGTAGTGCCACAGACAGTATTGCTTACGAGTTGATTTATCCTGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL5_S = document.createSequence(
                "L5_Suffix",
                "",
                "GGCTCGagaagtagtgccacagacagtattgcttacgag",
                Sequence.IUPAC_DNA
        );

        Sequence seqL5_P = document.createSequence(
                "L5_Prefix",
                "",
                "GGACaggataaatcaactcgtaagcaatactgtctgt",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L5 = document.createComponentDefinition(
                "L5",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L5.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L5.setName("L5");
        L5.setDescription("Neutral Linker");
        L5.addSequence(seqL5.getIdentity());

        ComponentDefinition L5_S = document.createComponentDefinition(
                "L5_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L5_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L5_S.setName("L5_Suffix");
        L5_S.setDescription("Neutral Linker Suffix");
        L5_S.addSequence(seqL5_S.getIdentity());

        ComponentDefinition L5_P = document.createComponentDefinition(
                "L5_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L5_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L5_P.setName("L5_Prefix");
        L5_P.setDescription("Neutral Linker Prefix");
        L5_P.addSequence(seqL5_P.getIdentity());

        //L6
        Sequence seqL6 = document.createSequence(
                "L6",
                "",
                "GGCTCGGTATTGTAAAGCACGAAACCTACGATAAGAGTGTCAGTTCTCCTTGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL6_S = document.createSequence(
                "L6_Suffix",
                "",
                "GGCTCGgtattgtaaagcacgaaacctacgataagagtg",
                Sequence.IUPAC_DNA
        );

        Sequence seqL6_P = document.createSequence(
                "L6_Prefix",
                "",
                "GGACaaggagaactgacactcttatcgtaggtttcgt",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L6 = document.createComponentDefinition(
                "L6",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L6.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L6.setName("L6");
        L6.setDescription("Neutral Linker");
        L6.addSequence(seqL6.getIdentity());

        ComponentDefinition L6_S = document.createComponentDefinition(
                "L6_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L6_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L6_S.setName("L6_Suffix");
        L6_S.setDescription("Neutral Linker Suffix");
        L6_S.addSequence(seqL6_S.getIdentity());

        ComponentDefinition L6_P = document.createComponentDefinition(
                "L6_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L6_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L6_P.setName("L6_Prefix");
        L6_P.setDescription("Neutral Linker Prefix");
        L6_P.addSequence(seqL6_P.getIdentity());

        //L7
        Sequence seqL7 = document.createSequence(
                "L7",
                "",
                "GGCTCGAACTTTTACGGGTGCCGACTCACTATTACAGACTTACTACAATCTGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL7_S = document.createSequence(
                "L7_Suffix",
                "",
                "GGCTCGaacttttacgggtgccgactcactattacagac",
                Sequence.IUPAC_DNA
        );

        Sequence seqL7_P = document.createSequence(
                "L7_Prefix",
                "",
                "GGACagattgtagtaagtctgtaatagtgagtcggca",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L7 = document.createComponentDefinition(
                "L7",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L7.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L7.setName("L7");
        L7.setDescription("Neutral Linker");
        L7.addSequence(seqL7.getIdentity());

        ComponentDefinition L7_S = document.createComponentDefinition(
                "L7_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L7_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L7_S.setName("L7_Suffix");
        L7_S.setDescription("Neutral Linker Suffix");
        L7_S.addSequence(seqL7_S.getIdentity());

        ComponentDefinition L7_P = document.createComponentDefinition(
                "L7_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L7_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L7_P.setName("L7_Prefix");
        L7_P.setDescription("Neutral Linker Prefix");
        L7_P.addSequence(seqL7_P.getIdentity());

        //Methylated Linkers
        //LMP
        Sequence seqLMP = document.createSequence(
                "LMP",
                "",
                "GGCTCGGGTAAGAACTCGCACTTCGTGGAAACACTATTATCTGGTGGGTCTCTGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqLMP_S = document.createSequence(
                "LMP_Suffix",
                "",
                "GGCTCGGGTAAGAACTCGCACTTCGTGGAAACACTATTA",
                Sequence.IUPAC_DNA
        );

        Sequence seqLMP_P = document.createSequence(
                "LMP_Prefix",
                "",
                "GGACAGAGACCCACCAGATAATAGTGTTTCCACGAAGTG",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition LMP = document.createComponentDefinition(
                "LMP",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LMP.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LMP.setName("LMP");
        LMP.setDescription("Methylated Linker");
        LMP.addSequence(seqLMP.getIdentity());

        ComponentDefinition LMP_S = document.createComponentDefinition(
                "LMP_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LMP_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LMP_S.setName("LMP_Suffix");
        LMP_S.setDescription("Methylated Linker Suffix");
        LMP_S.addSequence(seqLMP_S.getIdentity());

        ComponentDefinition LMP_P = document.createComponentDefinition(
                "LMP_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LMP_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LMP_P.setName("LMP_Prefix");
        LMP_P.setDescription("Methylated Linker Prefix");
        LMP_P.addSequence(seqLMP_P.getIdentity());

        //LMS
        Sequence seqLMS = document.createSequence(
                "LMS",
                "",
                "GGCTCGGGAGACCTATCGGTAATAACAGTCCAATCTGGTGTAACTTCGGAATCGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqLMS_S = document.createSequence(
                "LMS_Suffix",
                "",
                "GGCTCGGGAGACCTATCGGTAATAACAGTCCAATCTGGTGT",
                Sequence.IUPAC_DNA
        );

        Sequence seqLMS_P = document.createSequence(
                "LMS_Prefix",
                "",
                "GGACGATTCCGAAGTTACACCAGATTGGACTGTTATTAC",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition LMS = document.createComponentDefinition(
                "LMS",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LMS.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LMS.setName("LMS");
        LMS.setDescription("Methylated Linker");
        LMS.addSequence(seqLMS.getIdentity());

        ComponentDefinition LMS_S = document.createComponentDefinition(
                "LMS_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LMS_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LMS_S.setName("LMS_Suffix");
        LMS_S.setDescription("Methylated Linker Suffix");
        LMS_S.addSequence(seqLMS_S.getIdentity());

        ComponentDefinition LMS_P = document.createComponentDefinition(
                "LMS_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LMS_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LMS_P.setName("LMS_Prefix");
        LMS_P.setDescription("Methylated Linker Prefix");
        LMS_P.addSequence(seqLMS_P.getIdentity());

        //RBS Linkers
        //L1RBS1
        Sequence seqL1RBS1 = document.createSequence(
                "L1RBS1",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAAATCACACAGGACTAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS1_S = document.createSequence(
                "L1RBS1_Suffix",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS1_P = document.createSequence(
                "L1RBS1_Prefix",
                "",
                "GGACTAGTCCTGTGTGATTTACAACTGATACTTACCTGA",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L1RBS1 = document.createComponentDefinition(
                "L1RBS1",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS1.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS1.setName("L1RBS1");
        L1RBS1.setDescription("RBS Linkers");
        L1RBS1.addSequence(seqL1RBS1.getIdentity());

        ComponentDefinition L1RBS1_S = document.createComponentDefinition(
                "L1RBS1_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS1_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS1_S.setName("L1RBS1_Suffix");
        L1RBS1_S.setDescription("RBS Linkers Suffix");
        L1RBS1_S.addSequence(seqL1RBS1_S.getIdentity());

        ComponentDefinition L1RBS1_P = document.createComponentDefinition(
                "L1RBS1_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS1_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS1_P.setName("L1RBS1_Prefix");
        L1RBS1_P.setDescription("RBS Linkers Prefix");
        L1RBS1_P.addSequence(seqL1RBS1_P.getIdentity());

        //L1RBS2
        Sequence seqL1RBS2 = document.createSequence(
                "L1RBS2",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAAAAAGAGGGGAAATAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS2_S = document.createSequence(
                "L1RBS2_Suffix",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS2_P = document.createSequence(
                "L1RBS2_Prefix",
                "",
                "GGACTATTTCCCCTCTTTTTACAACTGATACTTACCTGA",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L1RBS2 = document.createComponentDefinition(
                "L1RBS2",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS2.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS2.setName("L1RBS2");
        L1RBS2.setDescription("RBS Linkers");
        L1RBS2.addSequence(seqL1RBS2.getIdentity());

        ComponentDefinition L1RBS2_S = document.createComponentDefinition(
                "L1RBS2_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS2_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS2_S.setName("L1RBS2_Suffix");
        L1RBS2_S.setDescription("RBS Linkers Suffix");
        L1RBS2_S.addSequence(seqL1RBS2_S.getIdentity());

        ComponentDefinition L1RBS2_P = document.createComponentDefinition(
                "L1RBS2_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS2_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS2_P.setName("L1RBS2_Prefix");
        L1RBS2_P.setDescription("RBS Linkers Prefix");
        L1RBS2_P.addSequence(seqL1RBS2_P.getIdentity());

        //L1RBS3
        Sequence seqL1RBS3 = document.createSequence(
                "L1RBS3",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAAAAAGAGGAGAAATAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS3_S = document.createSequence(
                "L1RBS3_Suffix",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS3_P = document.createSequence(
                "L1RBS3_Prefix",
                "",
                "GGACTATTTCTCCTCTTTTTACAACTGATACTTACCTGA",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L1RBS3 = document.createComponentDefinition(
                "L1RBS3",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS3.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS3.setName("L1RBS3");
        L1RBS3.setDescription("RBS Linkers");
        L1RBS3.addSequence(seqL1RBS3.getIdentity());

        ComponentDefinition L1RBS3_S = document.createComponentDefinition(
                "L1RBS3_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS3_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS3_S.setName("L1RBS3_Suffix");
        L1RBS3_S.setDescription("RBS Linkers Suffix");
        L1RBS3_S.addSequence(seqL1RBS3_S.getIdentity());

        ComponentDefinition L1RBS3_P = document.createComponentDefinition(
                "L1RBS3_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS3_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS3_P.setName("L1RBS3_Prefix");
        L1RBS3_P.setDescription("RBS Linkers Prefix");
        L1RBS3_P.addSequence(seqL1RBS3_P.getIdentity());

        //L1RBS4
        Sequence seqL1RBS4 = document.createSequence(
                "L1RBS4",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAAATCACAAGGAGGTAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS4_S = document.createSequence(
                "L1RBS4_Suffix",
                "",
                "GGCTCGTTGAACACCGTCTCAGGTAAGTATCAGTTGTAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL1RBS4_P = document.createSequence(
                "L1RBS4_Prefix",
                "",
                "GGACTACCTCCTTGTGATTTACAACTGATACTTACCTGA",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L1RBS4 = document.createComponentDefinition(
                "L1RBS4",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS4.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS4.setName("L1RBS4");
        L1RBS4.setDescription("RBS Linkers");
        L1RBS4.addSequence(seqL1RBS4.getIdentity());

        ComponentDefinition L1RBS4_S = document.createComponentDefinition(
                "L1RBS4_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS4_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS4_S.setName("L1RBS4_Suffix");
        L1RBS4_S.setDescription("RBS Linkers Suffix");
        L1RBS4_S.addSequence(seqL1RBS4_S.getIdentity());

        ComponentDefinition L1RBS4_P = document.createComponentDefinition(
                "L1RBS4_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L1RBS4_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L1RBS4_P.setName("L1RBS4_Prefix");
        L1RBS4_P.setDescription("RBS Linkers Prefix");
        L1RBS4_P.addSequence(seqL1RBS4_P.getIdentity());

        //L2RBS1
        Sequence seqL2RBS1 = document.createSequence(
                "L2RBS1",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAAATCACACAGGACTAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS1_S = document.createSequence(
                "L2RBS1_Suffix",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS1_P = document.createSequence(
                "L2RBS1_Prefix",
                "",
                "GGACTAGTCCTGTGTGATTTTCTGCTACCCTTATCTCAG",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L2RBS1 = document.createComponentDefinition(
                "L2RBS1",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS1.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS1.setName("L2RBS1");
        L2RBS1.setDescription("RBS Linkers");
        L2RBS1.addSequence(seqL2RBS1.getIdentity());

        ComponentDefinition L2RBS1_S = document.createComponentDefinition(
                "L2RBS1_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS1_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS1_S.setName("L2RBS1_Suffix");
        L2RBS1_S.setDescription("RBS Linkers");
        L2RBS1_S.addSequence(seqL2RBS1_S.getIdentity());

        ComponentDefinition L2RBS1_P = document.createComponentDefinition(
                "L2RBS1_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS1_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS1_P.setName("L2RBS1_Prefix");
        L2RBS1_P.setDescription("RBS Linkers");
        L2RBS1_P.addSequence(seqL2RBS1_P.getIdentity());

        //L2RBS2
        Sequence seqL2RBS2 = document.createSequence(
                "L2RBS2",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAAAAAGAGGGGAAATAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS2_S = document.createSequence(
                "L2RBS2_Suffix",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS2_P = document.createSequence(
                "L2RBS2_Prefix",
                "",
                "GGACTATTTCCCCTCTTTTTTCTGCTACCCTTATCTCAG",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L2RBS2 = document.createComponentDefinition(
                "L2RBS2",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS2.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS2.setName("L2RBS2");
        L2RBS2.setDescription("RBS Linkers");
        L2RBS2.addSequence(seqL2RBS2.getIdentity());

        ComponentDefinition L2RBS2_S = document.createComponentDefinition(
                "L2RBS2_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS2_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS2_S.setName("L2RBS2_Suffix");
        L2RBS2_S.setDescription("RBS Linkers Suffix");
        L2RBS2_S.addSequence(seqL2RBS2_S.getIdentity());

        ComponentDefinition L2RBS2_P = document.createComponentDefinition(
                "L2RBS2_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS2_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS2_P.setName("L2RBS2_Prefix");
        L2RBS2_P.setDescription("RBS Linkers Prefix");
        L2RBS2_P.addSequence(seqL2RBS2_P.getIdentity());

        //L2RBS3
        Sequence seqL2RBS3 = document.createSequence(
                "L2RBS3",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAAAAAGAGGAGAAATAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS3_S = document.createSequence(
                "L2RBS3_Suffix",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS3_P = document.createSequence(
                "L2RBS3_Prefix",
                "",
                "GGACTATTTCTCCTCTTTTTTCTGCTACCCTTATCTCAG",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L2RBS3 = document.createComponentDefinition(
                "L2RBS3",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS3.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS3.setName("L2RBS3");
        L2RBS3.setDescription("RBS Linkers");
        L2RBS3.addSequence(seqL2RBS3.getIdentity());

        ComponentDefinition L2RBS3_S = document.createComponentDefinition(
                "L2RBS3_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS3_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS3_S.setName("L2RBS3_Suffix");
        L2RBS3_S.setDescription("RBS Linkers Suffix");
        L2RBS3_S.addSequence(seqL2RBS3_S.getIdentity());

        ComponentDefinition L2RBS3_P = document.createComponentDefinition(
                "L2RBS3_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS3_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS3_P.setName("L2RBS3_Prefix");
        L2RBS3_P.setDescription("RBS Linkers Prefix");
        L2RBS3_P.addSequence(seqL2RBS3_P.getIdentity());

        //L2RBS4
        Sequence seqL2RBS4 = document.createSequence(
                "L2RBS4",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAAATCACAAGGAGGTAGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS4_S = document.createSequence(
                "L2RBS4_Suffix",
                "",
                "GGCTCGTGTTACTATTGGCTGAGATAAGGGTAGCAGAAA",
                Sequence.IUPAC_DNA
        );

        Sequence seqL2RBS4_P = document.createSequence(
                "L2RBS4_Prefix",
                "",
                "GGACTACCTCCTTGTGATTTTCTGCTACCCTTATCTCAG",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition L2RBS4 = document.createComponentDefinition(
                "L2RBS4",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS4.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS4.setName("L2RBS4");
        L2RBS4.setDescription("RBS Linkers");
        L2RBS4.addSequence(seqL2RBS4.getIdentity());

        ComponentDefinition L2RBS4_S = document.createComponentDefinition(
                "L2RBS4_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS4_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS4_S.setName("L2RBS4_Suffix");
        L2RBS4_S.setDescription("RBS Linkers Suffix");
        L2RBS4_S.addSequence(seqL2RBS4_S.getIdentity());

        ComponentDefinition L2RBS4_P = document.createComponentDefinition(
                "L2RBS4_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        L2RBS4_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        L2RBS4_P.setName("L2RBS4_Prefix");
        L2RBS4_P.setDescription("RBS Linkers Prefix");
        L2RBS4_P.addSequence(seqL2RBS4_P.getIdentity());

        //Fusion Linkers
        //LF1
        Sequence seqLF1 = document.createSequence(
                "LF1",
                "",
                "GGCTCGGCCGAAGCGGCTGCTAAAGAAGCAGCTGCTAAAGAGGCGGCCGCCAAGGCAGGGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqLF1_S = document.createSequence(
                "LF1_Suffix",
                "",
                "GGCTCGGCCGAAGCGGCTGCTAAAGAAGCAGCTGCTAAAGAGGCGGC",
                Sequence.IUPAC_DNA
        );

        Sequence seqLF1_P = document.createSequence(
                "LF1_Prefix",
                "",
                "GGACCCTGCCTTGGCGGCCGCCTCTTTAGCAGCTGCTTCTTTAGC",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition LF1 = document.createComponentDefinition(
                "LF1",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF1.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF1.setName("LF1");
        LF1.setDescription("Fusion Linker");
        LF1.addSequence(seqLF1.getIdentity());

        ComponentDefinition LF1_S = document.createComponentDefinition(
                "LF1_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF1_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF1_S.setName("LF1_Suffix");
        LF1_S.setDescription("Fusion Linker Suffix");
        LF1_S.addSequence(seqLF1_S.getIdentity());

        ComponentDefinition LF1_P = document.createComponentDefinition(
                "LF1_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF1_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF1_P.setName("LF1_Prefix");
        LF1_P.setDescription("Fusion Linker Prefix");
        LF1_P.addSequence(seqLF1_P.getIdentity());

        //LF2
        Sequence seqLF2 = document.createSequence(
                "LF2",
                "",
                "GGCTCGGGCTCGGGCTCCGGATCTGGTTCAGGTTCAGGATCGGGCTCCGGGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqLF2_S = document.createSequence(
                "LF2_Suffix",
                "",
                "GGCTCGGGCTCGGGCTCCGGATCTGGTTCAGGTTCAGG",
                Sequence.IUPAC_DNA
        );

        Sequence seqLF2_P = document.createSequence(
                "LF2_Prefix",
                "",
                "GGACCCGGAGCCCGATCCTGAACCTGAACCAGATCC",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition LF2 = document.createComponentDefinition(
                "LF2",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF2.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF2.setName("LF2");
        LF2.setDescription("Fusion Linker");
        LF2.addSequence(seqLF2.getIdentity());

        ComponentDefinition LF2_S = document.createComponentDefinition(
                "LF2_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF2_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF2_S.setName("LF2_Suffix");
        LF2_S.setDescription("Fusion Linker Suffix");
        LF2_S.addSequence(seqLF2_S.getIdentity());

        ComponentDefinition LF2_P = document.createComponentDefinition(
                "LF2_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF2_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF2_P.setName("LF2_Prefix");
        LF2_P.setDescription("Fusion Linker Prefix");
        LF2_P.addSequence(seqLF2_P.getIdentity());

        //LF3
        Sequence seqLF3 = document.createSequence(
                "LF3",
                "",
                "GGCTCGCTGCTTGAGAGCCCTAAAGCATTAGAAGAAGCACCTTGGCCTCCACCAGAAGGGTCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqLF3_S = document.createSequence(
                "LF3_Suffix",
                "",
                "GGCTCGCTGCTTGAGAGCCCTAAAGCATTAGAAGAAGCACCTTGGCC",
                Sequence.IUPAC_DNA
        );

        Sequence seqLF3_P = document.createSequence(
                "LF3_Prefix",
                "",
                "GGACCCTTCTGGTGGAGGCCAAGGTGCTTCTTCTAATGCTTTAGG",
                Sequence.IUPAC_DNA
        );

        ComponentDefinition LF3 = document.createComponentDefinition(
                "LF3",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF3.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF3.setName("LF3");
        LF3.setDescription("Fusion Linker");
        LF3.addSequence(seqLF3.getIdentity());

        ComponentDefinition LF3_S = document.createComponentDefinition(
                "LF3_Suffix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF3_S.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF3_S.setName("LF3_Suffix");
        LF3_S.setDescription("Fusion Linker Suffix");
        LF3_S.addSequence(seqLF3_S.getIdentity());

        ComponentDefinition LF3_P = document.createComponentDefinition(
                "LF3_Prefix",
                "",
                new HashSet<URI>(Arrays.asList(ComponentDefinition.DNA))
        );

        LF3_P.addRole(URI.create("http://identifiers.org/so/SO:0000696")); //oligo
        LF3_P.setName("LF3_Prefix");
        LF3_P.setDescription("Fusion Linker Prefix");
        LF3_P.addSequence(seqLF3_P.getIdentity());

        return document;
    }

    public static void main(String[] args) throws Exception{
        SBOLDocument document = createLinkerOutput();
        SBOLWriter.write(document,"examples/sbol_files/linker_parts.xml");
    }
}
