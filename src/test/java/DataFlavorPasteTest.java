import com.actelion.research.chem.IDCodeParser;
import com.actelion.research.chem.StereoMolecule;
import com.actelion.research.chem.reaction.Reaction;
import com.actelion.research.gui.clipboard.ClipboardFormat;
import com.actelion.research.gui.clipboard.ClipboardHandler;
import com.actelion.research.util.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Copyright (c) 2009-2025
 * Christian Rufener async.ch
 * All rights reserved
 * User: christian
 * Date: 26.10.25
 * Time: 11:08
 */
public class DataFlavorPasteTest {

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
    }

//    @Test
    public void shouldPasteDataFlavorMolecule() throws InvocationTargetException, IllegalAccessException {
        pasteMolecule(ClipboardFormat.NC_DATAFLAVOR_SERIALIZEDMOLECULE);

    }

    void pasteMolecule(ClipboardFormat format) throws InvocationTargetException, IllegalAccessException {
        boolean ok = false;
        byte[] data = (byte[]) getClipboardData.invoke(clipHandlerClz, format.value());
        if (data == null)
            ok = false;
        else {
            StereoMolecule res = ClipboardHandler.rawToMol(data, format, true);

            ok = res != null && res.getAllAtoms() > 0 /*&& idCode.equals(res.getIDCode()*/;
            if (!ok) {
                System.out.printf("Could not paste Molecule %s\n",format.value());
            } else {
                byte[] bytes = handler.molToRaw(res,format);
                ok = (boolean)setClipBoardData.invoke(clipHandlerClz, format.value(), bytes, emptyClipboard);

            }
        }
        assertTrue(ok);
    }


    //@Test
    public void shouldPasteDataFlavorReaction() throws InvocationTargetException, IllegalAccessException {
        pasteReaction(ClipboardFormat.NC_DATAFLAVOR_SERIALIZEDREACTION);

    }

    void pasteReaction(ClipboardFormat format) throws InvocationTargetException, IllegalAccessException {
        boolean ok = false;
        byte[] data = (byte[]) getClipboardData.invoke(clipHandlerClz, format.value());
        if (data == null)
            ok = false;
        else {
            Reaction res = ClipboardHandler.rawToRxn(data, format);

            ok = res != null && res.getProducts() > 0 /*&& idCode.equals(res.getIDCode()*/;
            if (!ok) {
                System.out.printf("Could not paste Reaction %s\n",format.value());
            } else {
                byte[] bytes = handler.rxnToRaw(res,null, format);
                ok = (boolean)setClipBoardData.invoke(clipHandlerClz, format.value(), bytes, emptyClipboard);

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

}
