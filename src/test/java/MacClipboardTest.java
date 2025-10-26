import com.actelion.research.chem.IDCodeParser;
import com.actelion.research.chem.StereoMolecule;
import com.actelion.research.gui.clipboard.foundation.*;
import com.sun.jna.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static com.actelion.research.gui.clipboard.foundation.NSData.initWithBytes;
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
public class MacClipboardTest {
    static NSPasteboard nsPasteboard = NSPasteboard.generalPasteboard();

    @Test
    public void schouldBeAbleToCallMacAPI() {
        // Example Codde
        Pointer classId = Foundation.INSTANCE.objc_getClass("NSPasteboard");
        assertNotNull(classId);
        Pointer clearContents = Foundation.INSTANCE.sel_registerName("clearContents");
        assertNotNull(clearContents);
        Pointer selector = Foundation.INSTANCE.sel_registerName("generalPasteboard");
        assertNotNull(selector);
        System.out.printf("class id %s Selector %s\n", classId, selector);
        NativeLong pasteBoard = Foundation.INSTANCE.objc_msgSend(classId, selector);
        assertNotNull(pasteBoard);
        System.out.printf("Pointer %s \n", pasteBoard);
        NativeLong ptr = Foundation.INSTANCE.objc_msgSend(pasteBoard, clearContents);
        assertNotNull(ptr);
        System.out.printf("clearContent returned %s\n", ptr.longValue());
        assertTrue(ptr.longValue() > 0);
        Pointer name = Foundation.INSTANCE.sel_registerName("name");
        NativeLong namePtr = Foundation.INSTANCE.objc_msgSend(pasteBoard, name);
        //new ID(namePtr);
        String value = toStringViaUTF8(namePtr);
        System.out.printf("name returned '%s'\n", value);
        assertEquals("Apple CFPasteboard general",value);
    }

    @Test
    public void shouldNotFindData() {
        nsPasteboard.clearContents();
        NSString format = new NSString("plain.text");
        String copyValue = "xx.yy";
        nsPasteboard.setData(initWithBytes(copyValue.getBytes()), format);
        nsPasteboard.clearContents();
        NSData dataForType = nsPasteboard.getDataForType(format);
        if (dataForType != null) {
            assertFalse(true);
        } else
            assertTrue(true);
    }

    @Test
    public void shouldPlaceMoleculeOnClipboard() {
        String idCode = "fn}Ip@EZSdBBFBUjS\\kJr|s_AMSUUSUSUQbyARRl`@";
        IDCodeParser parser = new IDCodeParser();
        StereoMolecule m = parser.getCompactMolecule(idCode);
        for (int atom = 0; atom < m.getAllAtoms(); atom++)
            m.setAtomMapNo(atom, 0, false);
        com.actelion.research.gui.clipboard.external.ChemDrawCDX cdx = new com.actelion.research.gui.clipboard.external.ChemDrawCDX();
        byte[] bytes = cdx.getChemDrawBuffer(m);
        nsPasteboard.clearContents();
        nsPasteboard.setData(NSData.initWithBytes(bytes),new NSString("com.perkinelmer.chemdraw.cdx-clipboard"));


    }

    static String toStringViaUTF8(NativeLong cfString)
    {
        //if (ID.NIL.equals(cfString)) return null;

        int lengthInChars = Foundation.INSTANCE.CFStringGetLength(cfString);
        int potentialLengthInBytes = 3 * lengthInChars + 1; // UTF8 fully escaped 16 bit chars, plus nul

        byte[] buffer = new byte[potentialLengthInBytes];
        byte ok = Foundation.INSTANCE.CFStringGetCString(cfString, buffer, buffer.length, Foundation.INSTANCE.kCFStringEncodingUTF8);
        if (ok == 0) throw new RuntimeException("Could not convert string");
        return Native.toString(buffer);
    }

}
