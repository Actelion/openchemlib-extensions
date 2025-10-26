import com.actelion.research.chem.io.RXNFileParser;
import com.actelion.research.chem.reaction.Reaction;
import com.actelion.research.gui.clipboard.ClipboardFormat;
import com.actelion.research.gui.clipboard.ClipboardHandler;
import com.actelion.research.gui.clipboard.foundation.*;
import com.actelion.research.gui.dnd.ReactionTransferable;
import com.sun.jna.Pointer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import static com.actelion.research.gui.clipboard.ClipboardFormat.*;
import static com.actelion.research.gui.clipboard.ClipboardHandler.rawToRxn;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Copyright (c) 2009-2025
 * Christian Rufener async.ch
 * All rights reserved
 * User: christian
 * Date: 24.10.25
 * Time: 16:09
 */

@EnabledOnOs(OS.MAC)
public class MacRxnClipboardTest {
    static Reaction reaction = new Reaction();
    static String serializedRxnFormat = "JAVA_DATAFLAVOR:application/x-java-serialized-object; class=com.actelion.research.chem.reaction.Reaction";
    static String smilesRxnFormat = "JAVA_DATAFLAVOR:chemical/x-daylight-reactionsmiles; class=java.lang.String";

    static ClipboardHandler handler = new ClipboardHandler();
    static NSPasteboard nsPasteboard = NSPasteboard.generalPasteboard();

    @BeforeAll
    public static void setup() throws Exception {
        RXNFileParser parser = new RXNFileParser();
        parser.parse(reaction,RXN);
    }

   // @Test
    public void shouldCopyAndPasteClassic() {
        try {
            ReactionTransferable transferable = new ReactionTransferable(reaction);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, transferable);
            NSData dataForType = nsPasteboard.getDataForType(new NSString(serializedRxnFormat));
            if (dataForType != null) {
                int size = dataForType.length();
                byte[] data = new Pointer(dataForType.getData().longValue()).getByteArray(0, size);
                assertTrue(data != null && data.length > 0);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(data));
                Object o = is.readObject();
                assertTrue(o instanceof Reaction);
                Reaction r = (Reaction)o;
                assertEquals(r.getReactants(),2);
                assertEquals(1, r.getProducts());
            } else {
                assertFalse(true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }

    @Test
    public void shouldCopyReaction() {
        nsPasteboard.clearContents();
        ClipboardFormat formats[] = {
                NC_CTAB,
               // NC_CHEMDRAWINTERCHANGE,
                NC_SERIALIZEREACTION,
                COM_MDLI_MOLFILE,
        };

        for (ClipboardFormat format : formats) {
            byte[] bytes = handler.rxnToRaw(reaction, null, format);
            setClipBoardData(format.value(),bytes,false);
            NSData dataForType = nsPasteboard.getDataForType(new NSString(format.value()));
            assertNotNull(dataForType);
            int size = dataForType.length();
            byte[] data = new Pointer(dataForType.getData().longValue()).getByteArray(0, size);
            assertTrue(data != null && data.length > 0);
            Reaction rxn = rawToRxn(data, format);
            if (rxn != null) {
                assertEquals(rxn.getReactants(), reaction.getReactants());
            } else {
                System.out.printf("Could not interpret %s\n",format.name());
            }

        }

    }
    static boolean setClipBoardData(String format, byte[] buffer, boolean emptyClipboard) {
        NSData data = NSData.initWithBytes(buffer);
        if (emptyClipboard)
            nsPasteboard.clearContents();
        return nsPasteboard.setData(data, new NSString(format));
    }

    /*
    @Test
    public void shouldPlaceChemDrawFormat() {
        StereoMolecule m = molecule.getCompactCopy();
        for (int atom = 0; atom < m.getAllAtoms(); atom++)
            m.setAtomMapNo(atom, 0, false);
        com.actelion.research.gui.clipboard.external.ChemDrawCDX cdx = new com.actelion.research.gui.clipboard.external.ChemDrawCDX();
        byte[] bytes = cdx.getChemDrawBuffer(m);
        nsPasteboard.clearContents();
        nsPasteboard.setData(NSData.initWithBytes(bytes),new NSString("com.perkinelmer.chemdraw.cdx-clipboard"));
    }

    @Test
    public void shouldPlaceMDLSketch() {
        StereoMolecule m = molecule.getCompactCopy();
        byte bytes[] = Sketch.createSketchFromMol(m);
        nsPasteboard.clearContents();
        nsPasteboard.setData(NSData.initWithBytes(bytes),new NSString("com.mdli.sketchfile"));
    }

    @Test
    public void shouldPlaceMolfile() throws IOException {
        StereoMolecule m = molecule.getCompactCopy();
        MolfileCreator creator = new MolfileCreator(m);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(stream);
        //ByteArrayOutputStream o = new ByteArrayOutputStream();
        //LittleEndianDataOutputStream os = new LittleEndianDataOutputStream(o);
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);

        String molfile = creator.getMolfile();
        String[] lines = molfile.split("\\r?\\n");
        for (String line : lines) {
            int length = line.length();
            os.write(length);
            if (length > 0)
                os.write(line.getBytes());
        }
        os.close();
        byte bytes[] = stream.toByteArray();

        nsPasteboard.clearContents();
        nsPasteboard.setData(NSData.initWithBytes(bytes),new NSString("com.mdli.molfile"));
    }

*/

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
