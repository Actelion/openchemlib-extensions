import com.actelion.research.chem.*;
import com.actelion.research.gui.clipboard.foundation.*;
import com.actelion.research.util.Sketch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Copyright (c) 2009-2025
 * Christian Rufener async.ch
 * All rights reserved
 * User: christian
 * Date: 24.10.25
 * Time: 16:09
 */

@EnabledOnOs(OS.MAC)
public class MacMolClipboardTest {
    static NSPasteboard nsPasteboard = NSPasteboard.generalPasteboard();

    private String idCode = "fn}Ip@EZSdBBFBUjS\\kJr|s_AMSUUSUSUQbyARRl`@";
    private IDCodeParser parser = new IDCodeParser();
    private StereoMolecule molecule = parser.getCompactMolecule(idCode);

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

}
