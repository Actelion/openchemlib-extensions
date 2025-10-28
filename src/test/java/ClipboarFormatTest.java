import com.actelion.research.chem.IDCodeParser;
import com.actelion.research.chem.StereoMolecule;
import com.actelion.research.chem.io.RXNFileParser;
import com.actelion.research.chem.reaction.Reaction;
import com.actelion.research.gui.clipboard.ClipboardFormat;
import com.actelion.research.gui.clipboard.ClipboardHandler;
import com.actelion.research.util.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Copyright (c) 2009-2025
 * Christian Rufener async.ch
 * All rights reserved
 * User: christian
 * Date: 24.10.25
 * Time: 22:10
 */
public class ClipboarFormatTest {
    private String idCode = "fn}Ip@EZSdBBFBUjS\\kJr|s_AMSUUSUSUQbyARRl`@";
    private IDCodeParser parser = new IDCodeParser();
    private StereoMolecule molecule = parser.getCompactMolecule(idCode);
    private static Class clipHandlerClz;
    private static Method setClipBoardData;
    private static Method getClipboardData;
    private static ClipboardHandler handler = new ClipboardHandler();
    private static Reaction reaction = new Reaction();

    private boolean emptyClipboard = true;

    @BeforeAll
    public static void init() throws Exception {
        clipHandlerClz = loadNativeCliphandler();
        setClipBoardData = clipHandlerClz.getMethod("setClipBoardData", String.class, byte[].class, boolean.class);
        getClipboardData = clipHandlerClz.getMethod("getClipboardData", String.class);
        RXNFileParser parser = new RXNFileParser();
        parser.parse(reaction,RXN);

    }

    boolean testMolFormat(ClipboardFormat format) throws InvocationTargetException, IllegalAccessException {
        byte[] bytes = handler.molToRaw(molecule.getCompactCopy(), format);
        boolean ok = (boolean)setClipBoardData.invoke(clipHandlerClz, format.value(), bytes, emptyClipboard);
        if (ok) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ///
            }
            byte[] data = (byte[]) getClipboardData.invoke(clipHandlerClz, format.value());
            if (data == null)
                ok = false;
            else {
                StereoMolecule res = ClipboardHandler.rawToMol(data, format, true);

                ok = res != null && res.getAllAtoms() > 0 /*&& idCode.equals(res.getIDCode()*/;
                if (ok) {
                    System.out.printf("Molecule is %s/%s %s\n'%s'\n'%s'\n",
                            res.getAllAtoms(), res.getAllBonds(), molecule.getMolweight(),idCode,res.getIDCode());
                } else {
                    System.out.printf("Could not paste Molecule %s\n",format.value());
                }
            }
        }
        return ok;
    }

    boolean testRxnFormat(ClipboardFormat format) throws InvocationTargetException, IllegalAccessException {
        byte[] bytes = handler.rxnToRaw(reaction, null, format);
        boolean ok = (boolean)setClipBoardData.invoke(clipHandlerClz, format.value(), bytes, emptyClipboard);
        if (ok) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ///
            }
            byte[] data = (byte[]) getClipboardData.invoke(clipHandlerClz, format.value());
            if (data == null)
                ok = false;
            else {
                Reaction res = ClipboardHandler.rawToRxn(data, format);

                ok = res != null && res.getMolecules() > 0 /*&& idCode.equals(res.getIDCode()*/;
                if (ok) {
                    System.out.printf("Reaction is %s/%s\n",
                            res.getReactants(), res.getProducts());
                } else {
                    System.out.printf("Could not paste Molecule %s\n",format.value());
                }
            }
        }
        return ok;
    }

    //@Test
    public void testMetafile() {
        boolean ok;
        ClipboardFormat format = Platform.isWindows() ?
                ClipboardFormat.NC_METAFILE : ClipboardFormat.NC_SKETCH;
        try {
            System.out.printf("Testing %s\n",format.name());
            ok = testMolFormat(format);
            assertTrue(ok);
            System.out.printf("Test OK %s\n",ok);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception: " + e.getMessage());
            assertFalse(true);
        }

    }
    @Test
    public void testMolFormats() {
        boolean ok = false;
        ClipboardFormat[] formats = ClipboardFormat.values();
        List<ClipboardFormat> list = Arrays.stream(formats).filter(f -> {
            switch (f) {
                case NC_SERIALIZEREACTION:
                case NC_DATAFLAVOR_SERIALIZEDREACTION:
                case NC_DATAFLAVOR_RXNSMILES:
                case NC_EMBEDDEDSKETCH:
                case NC_CHEMDRAWINTERCHANGE:

                case NC_METAFILE: // disabled due to paste error
//                case COM_PE_CDX:
                    return false;
                default:
                    return true;
            }
        }).collect(Collectors.toList());

        for (ClipboardFormat format : list) {
            try {
                System.out.printf("Testing %s\n",format.name());
                ok = testMolFormat(format);
                assertTrue(ok);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception: " + e.getMessage());
                assertFalse(true);
            }
        }
    }


    @Test
    public void testRXNFormats() {
        boolean ok = false;
        ClipboardFormat[] formats = ClipboardFormat.values();
        List<ClipboardFormat> list = Arrays.stream(formats).filter(f -> {
            switch (f) {
                case NC_SERIALIZEMOLECULE:
                case NC_DATAFLAVOR_IDCODE:
                case NC_DATAFLAVOR_SERIALIZEDMOLECULE:
                case COM_MDLI_SKETCHFILE:
                case NC_MOLFILE:
                case NC_SKETCH:
                case NC_SMILES:
                case NC_MOLFILE_V3:
                case NC_EMBEDDEDSKETCH:
                case NC_CHEMDRAWINTERCHANGE:
                case NC_METAFILE:
                case COM_PE_CDX:
                    return false;
                default:
                    return true;
            }
        }).collect(Collectors.toList());

        for (ClipboardFormat format : list) {
            try {
                System.out.printf("Testing %s\n",format.name());
                ok = testRxnFormat(format);
                assertTrue(ok);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception: " + e.getMessage());
                assertFalse(true);
            }
        }
    }

    @Test
    public void placeRxn() throws InvocationTargetException, IllegalAccessException {
        testRxnFormat(ClipboardFormat.NC_DATAFLAVOR_SERIALIZEDREACTION);
        ClipboardFormat format = ClipboardFormat.NC_DATAFLAVOR_SERIALIZEDREACTION;

        boolean ok = false;
        byte[] data = (byte[]) getClipboardData.invoke(clipHandlerClz, format.value());
        if (data == null)
            ok = false;
        else {
            Reaction res = ClipboardHandler.rawToRxn(data, format);

            ok = res != null && res.getMolecules() > 0 /*&& idCode.equals(res.getIDCode()*/;
            if (ok) {
                System.out.printf("Reaction is %s/%s\n",
                        res.getReactants(), res.getProducts());
            } else {
                System.out.printf("Could not paste Molecule %s\n",format.value());
            }
        }

        assertTrue(ok);
    }

    private static Class loadNativeCliphandler() throws Exception {
        Class clipHandlerClz = null;

        String clz;
        if (Platform.isMacintosh())
            clz = "com.actelion.research.gui.clipboard.JNAMacClipboardHandler";
        else if (Platform.isWindows()) {
            clz = "com.actelion.research.gui.clipboard.JNAWinClipboardHandler";
        } else
            throw new RuntimeException("Platform not supported");
        clipHandlerClz = Class.forName(clz);
        if (clipHandlerClz != null && (boolean) clipHandlerClz.getMethod("isInitOK")
                .invoke(clipHandlerClz)) {
            System.out.println("WinClipHandler class: " + clz);
        }
        return clipHandlerClz;
    }

    private static String RXN =
            "$RXN\n" +
            "testRxnV2.rxn\n" +
            "  ChemDraw10252514022D\n" +
            "\n" +
            "  2  1\n" +
            "$MOL\n" +
            "\n" +
            "\n" +
            "\n" +
            "  6  6  0  0  0  0  0  0  0  0999 V2000\n" +
            "   -0.7145    0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "   -0.7145   -0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.0000   -0.8250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.7145   -0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.7145    0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.0000    0.8250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "  1  2  2  0        0\n" +
            "  2  3  1  0        0\n" +
            "  3  4  2  0        0\n" +
            "  4  5  1  0        0\n" +
            "  5  6  2  0        0\n" +
            "  6  1  1  0        0\n" +
            "M  END\n" +
            "$MOL\n" +
            "\n" +
            "\n" +
            "\n" +
            "  4  3  0  0  0  0  0  0  0  0999 V2000\n" +
            "   -0.7145   -0.6188    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.0000   -0.2062    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.7145   -0.6188    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.0000    0.6188    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "  1  2  1  0        0\n" +
            "  2  3  1  0        0\n" +
            "  2  4  2  0        0\n" +
            "M  END\n" +
            "$MOL\n" +
            "\n" +
            "\n" +
            "\n" +
            "  9  9  0  0  0  0  0  0  0  0999 V2000\n" +
            "   -1.4289   -0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "   -1.4289   -0.8250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "   -0.7145   -1.2375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "   -0.0000   -0.8250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "   -0.0000   -0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "   -0.7145    0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.7145    0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    1.4289    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "    0.7145    1.2375    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
            "  1  2  2  0        0\n" +
            "  2  3  1  0        0\n" +
            "  3  4  2  0        0\n" +
            "  4  5  1  0        0\n" +
            "  5  6  2  0        0\n" +
            "  6  1  1  0        0\n" +
            "  5  7  1  0        0\n" +
            "  7  8  1  0        0\n" +
            "  7  9  2  0        0\n" +
            "M  END\n";


}

